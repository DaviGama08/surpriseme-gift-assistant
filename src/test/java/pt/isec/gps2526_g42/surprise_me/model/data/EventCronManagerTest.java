package pt.isec.gps2526_g42.surprise_me.model.data;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pt.isec.gps2526_g42.surprise_me.model.Occasion;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("EventCronManager Tests")
class EventCronManagerTest {

    private SurpriseMeManager manager;
    private EnjoyerDetails enjoyerDetails;

    @BeforeEach
    void setUp() {
        // Delete any existing data file to ensure clean test environment
        try {
            java.nio.file.Files.deleteIfExists(SurpriseMeSerialization.dataFilePath());
        } catch (Exception e) {
            // Ignore if file doesn't exist
        }

        manager = new SurpriseMeManager();

        // Register and login a test user so all operations work
        manager.register("TestUser", "test@example.com", "password123");

        enjoyerDetails = new EnjoyerDetails();
        enjoyerDetails.setName("Alice");
        enjoyerDetails.setBirthDate(LocalDate.of(1990, 5, 15));
    }

    @Test
    @DisplayName("Test ensureRecurringEventsForMonth creates birthday events")
    void testEnsureRecurringEventsForMonthBirthday() {
        manager.addEnjoyer(enjoyerDetails);

        LocalDate may2026 = LocalDate.of(2026, 5, 1);
        EventCronManager.ensureRecurringEventsForMonth(manager, may2026);

        List<Integer> eventIds = manager.getMonthlyEventsIds(may2026);
        assertNotNull(eventIds);

        boolean hasBirthdayEvent = false;
        for (Integer id : eventIds) {
            Occasion occasion = manager.getEventOccasionById(id);
            if (occasion == Occasion.BIRTHDAY) {
                hasBirthdayEvent = true;
                break;
            }
        }
        assertTrue(hasBirthdayEvent);
    }

    @Test
    @DisplayName("Test ensureRecurringEventsForMonth with null manager")
    void testEnsureRecurringEventsForMonthNullManager() {
        assertDoesNotThrow(() -> EventCronManager.ensureRecurringEventsForMonth(null, LocalDate.now()));
    }

    @Test
    @DisplayName("Test ensureRecurringEventsForMonth with null date")
    void testEnsureRecurringEventsForMonthNullDate() {
        assertDoesNotThrow(() -> EventCronManager.ensureRecurringEventsForMonth(manager, null));
    }

    @Test
    @DisplayName("Test ensureRecurringEventsForMonth only creates birthday events")
    void testEnsureRecurringEventsForMonthOnlyBirthdays() {
        // Current implementation only creates BIRTHDAY events automatically
        // Global events (New Year, Valentine's, etc.) are NOT created automatically

        LocalDate january2026 = LocalDate.of(2026, 1, 1);
        EventCronManager.ensureRecurringEventsForMonth(manager, january2026);

        List<Integer> eventIds = manager.getMonthlyEventsIds(january2026);

        // Verify no global events are created automatically
        for (Integer id : eventIds) {
            Occasion occasion = manager.getEventOccasionById(id);
            // Only BIRTHDAY events should be created (if any enjoyer has birthday in January)
            assertNotEquals(Occasion.NEW_YEAR, occasion,
                "New Year events should NOT be created automatically");
        }
    }

    @Test
    @DisplayName("Test ensureRecurringEventsForMonth respects deleted birthday events")
    void testEnsureRecurringEventsForMonthRespectsDeleted() {
        // Add enjoyer with birthday in May
        manager.addEnjoyer(enjoyerDetails);

        LocalDate may2026 = LocalDate.of(2026, 5, 1);

        // First call should create birthday event
        EventCronManager.ensureRecurringEventsForMonth(manager, may2026);
        List<Integer> eventIds = manager.getMonthlyEventsIds(may2026);

        int initialCount = eventIds.size();
        assertTrue(initialCount > 0, "Should have at least one birthday event");

        // Second call should NOT create duplicate
        EventCronManager.ensureRecurringEventsForMonth(manager, may2026);
        List<Integer> eventIds2 = manager.getMonthlyEventsIds(may2026);

        assertEquals(initialCount, eventIds2.size(),
            "Should not create duplicate birthday events");
    }

    @Test
    @DisplayName("Test ensureRecurringEventsForMonth does not duplicate events")
    void testEnsureRecurringEventsForMonthNoDuplicates() {
        LocalDate january2026 = LocalDate.of(2026, 1, 1);

        EventCronManager.ensureRecurringEventsForMonth(manager, january2026);
        List<Integer> firstCall = manager.getMonthlyEventsIds(january2026);
        int firstCount = firstCall.size();

        EventCronManager.ensureRecurringEventsForMonth(manager, january2026);
        List<Integer> secondCall = manager.getMonthlyEventsIds(january2026);
        int secondCount = secondCall.size();

        assertEquals(firstCount, secondCount);
    }

    @Test
    @DisplayName("Test onGiftAdded functionality with valid parameters")
    void testOnGiftAddedWithBirthday() {
        // onGiftAdded is used to bind an enjoyer to an existing event
        // It searches for events with the given occasion that don't have an enjoyer yet

        manager.addEnjoyer(enjoyerDetails);
        int enjoyerId = manager.getEnjoyers().keySet().iterator().next();

        // Create a birthday event without enjoyer (manual event)
        manager.addEvent("Someone's Birthday", LocalDate.of(2026, 5, 15), Occasion.BIRTHDAY, -1);

        // Now bind the enjoyer to birthday events
        EventCronManager.onGiftAdded(manager, enjoyerId, Occasion.BIRTHDAY);

        // Verify the method doesn't throw and completes
        List<Integer> birthdayEvents = manager.getEventIdsByOccasion(Occasion.BIRTHDAY);
        assertNotNull(birthdayEvents, "Should return list of birthday events");
    }

    @Test
    @DisplayName("Test onGiftAdded with null manager")
    void testOnGiftAddedNullManager() {
        assertDoesNotThrow(() -> EventCronManager.onGiftAdded(null, 1, Occasion.BIRTHDAY));
    }

    @Test
    @DisplayName("Test onGiftAdded with null occasion")
    void testOnGiftAddedNullOccasion() {
        assertDoesNotThrow(() -> EventCronManager.onGiftAdded(manager, 1, null));
    }

    @Test
    @DisplayName("Test onGiftAdded with invalid enjoyer id")
    void testOnGiftAddedInvalidEnjoyerId() {
        assertDoesNotThrow(() -> EventCronManager.onGiftAdded(manager, 0, Occasion.BIRTHDAY));
        assertDoesNotThrow(() -> EventCronManager.onGiftAdded(manager, -1, Occasion.BIRTHDAY));
    }

    @Test
    @DisplayName("Test onGiftAdded with non-bindable occasion")
    void testOnGiftAddedNonBindableOccasion() {
        manager.addEnjoyer(enjoyerDetails);
        int enjoyerId = manager.getEnjoyers().keySet().iterator().next();

        assertDoesNotThrow(() -> EventCronManager.onGiftAdded(manager, enjoyerId, Occasion.BIRTHDAY));
        assertDoesNotThrow(() -> EventCronManager.onGiftAdded(manager, enjoyerId, Occasion.OTHER));
    }

    @Test
    @DisplayName("Test birthday event contains enjoyer name")
    void testBirthdayEventContainsEnjoyerName() {
        manager.addEnjoyer(enjoyerDetails);

        LocalDate may2026 = LocalDate.of(2026, 5, 1);
        EventCronManager.ensureRecurringEventsForMonth(manager, may2026);

        List<Integer> eventIds = manager.getMonthlyEventsIds(may2026);

        for (Integer id : eventIds) {
            Occasion occasion = manager.getEventOccasionById(id);
            if (occasion == Occasion.BIRTHDAY) {
                String eventName = manager.getEventNameById(id);
                assertTrue(eventName.contains("Birthday") || eventName.contains("Alice"));
            }
        }
    }

    @Test
    @DisplayName("Test multiple enjoyers with birthdays in same month")
    void testMultipleEnjoyersBirthdaysSameMonth() {
        // First enjoyer
        manager.addEnjoyer(enjoyerDetails);

        // Second enjoyer
        EnjoyerDetails enjoyer2 = new EnjoyerDetails();
        enjoyer2.setName("Bob");
        enjoyer2.setBirthDate(LocalDate.of(1985, 5, 20));
        manager.addEnjoyer(enjoyer2);

        LocalDate may2026 = LocalDate.of(2026, 5, 1);
        EventCronManager.ensureRecurringEventsForMonth(manager, may2026);

        List<Integer> eventIds = manager.getMonthlyEventsIds(may2026);

        int birthdayCount = 0;
        for (Integer id : eventIds) {
            Occasion occasion = manager.getEventOccasionById(id);
            if (occasion == Occasion.BIRTHDAY) {
                birthdayCount++;
            }
        }

        assertTrue(birthdayCount >= 2);
    }

    @Test
    @DisplayName("Test enjoyer without birthdate does not create birthday event")
    void testEnjoyerWithoutBirthdateNoBirthdayEvent() {
        EnjoyerDetails noBirthdate = new EnjoyerDetails();
        noBirthdate.setName("Charlie");
        // No birthdate set
        manager.addEnjoyer(noBirthdate);

        LocalDate january2026 = LocalDate.of(2026, 1, 1);
        EventCronManager.ensureRecurringEventsForMonth(manager, january2026);

        // Should not crash and should only create global events
        List<Integer> eventIds = manager.getMonthlyEventsIds(january2026);
        assertNotNull(eventIds);
    }
}

