package pt.isec.gps2526_g42.surprise_me.model.data;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pt.isec.gps2526_g42.surprise_me.model.Occasion;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Event Tests")
class EventTest {

    private EnjoyerDetails enjoyerDetails;
    private Enjoyer enjoyer;

    @BeforeEach
    void setUp() {
        enjoyerDetails = new EnjoyerDetails();
        enjoyerDetails.setName("John Doe");
        enjoyerDetails.setBirthDate(LocalDate.of(1990, 5, 15));
        enjoyer = new Enjoyer(enjoyerDetails);
    }

    @Test
    @DisplayName("Test Event constructor with valid parameters")
    void testConstructorValid() {
        Event event = new Event("Birthday Party", LocalDate.now().plusDays(10), Occasion.BIRTHDAY, enjoyer);

        assertNotNull(event);
        assertEquals("Birthday Party", event.getName());
        assertEquals(Occasion.BIRTHDAY, event.getOccasion());
        assertNotNull(event.getDate());
    }

    @Test
    @DisplayName("Test Event constructor with null enjoyer")
    void testConstructorNullEnjoyer() {
        Event event = new Event("Event", LocalDate.now(), Occasion.OTHER, null);

        assertNotNull(event);
        assertEquals("Event", event.getName());
    }

    @Test
    @DisplayName("Test Event constructor assigns unique id")
    void testConstructorUniqueId() {
        Event event1 = new Event("Event1", LocalDate.now(), Occasion.OTHER, null);
        Event event2 = new Event("Event2", LocalDate.now(), Occasion.OTHER, null);

        assertNotEquals(event1.getIdEvent(), event2.getIdEvent());
    }

    @Test
    @DisplayName("Test getName returns correct name")
    void testGetName() {
        Event event = new Event("Test Event", LocalDate.now(), Occasion.OTHER, null);
        assertEquals("Test Event", event.getName());
    }

    @Test
    @DisplayName("Test getDate returns correct date")
    void testGetDate() {
        LocalDate date = LocalDate.of(2026, 6, 15);
        Event event = new Event("Test Event", date, Occasion.OTHER, null);
        assertEquals(date, event.getDate());
    }

    @Test
    @DisplayName("Test getOccasion returns correct occasion")
    void testGetOccasion() {
        Event event = new Event("Test Event", LocalDate.now(), Occasion.CHRISTMAS, null);
        assertEquals(Occasion.CHRISTMAS, event.getOccasion());
    }

    @Test
    @DisplayName("Test getIdEvent returns valid id")
    void testGetIdEvent() {
        Event event = new Event("Test Event", LocalDate.now(), Occasion.OTHER, null);
        assertTrue(event.getIdEvent() > 0);
    }

    @Test
    @DisplayName("Test editEvent updates all fields")
    void testEditEvent() {
        Event event = new Event("Original", LocalDate.now(), Occasion.OTHER, null);

        LocalDate newDate = LocalDate.now().plusDays(20);
        EnjoyerDetails newDetails = new EnjoyerDetails();
        newDetails.setName("Jane Doe");
        Enjoyer newEnjoyer = new Enjoyer(newDetails);

        event.editEvent("Updated", newDate, Occasion.BIRTHDAY, newEnjoyer);

        assertEquals("Updated", event.getName());
        assertEquals(newDate, event.getDate());
        assertEquals(Occasion.BIRTHDAY, event.getOccasion());
    }

    @Test
    @DisplayName("Test editEvent with null enjoyer")
    void testEditEventNullEnjoyer() {
        Event event = new Event("Original", LocalDate.now(), Occasion.OTHER, enjoyer);

        event.editEvent("Updated", LocalDate.now().plusDays(5), Occasion.BIRTHDAY, null);

        assertEquals("Updated", event.getName());
    }

    @Test
    @DisplayName("Test getEnjoyerName returns enjoyer name")
    void testGetEnjoyerName() {
        Event event = new Event("Event", LocalDate.now(), Occasion.BIRTHDAY, enjoyer);
        assertEquals(enjoyer.getDetails().getName(), event.getEnjoyerName());
    }


    @Test
    @DisplayName("Test isDeleted returns false initially")
    void testIsDeletedInitially() {
        Event event = new Event("Event", LocalDate.now(), Occasion.OTHER, null);
        assertFalse(event.isDeleted());
    }

    @Test
    @DisplayName("Test markDeleted sets deleted flag")
    void testMarkDeleted() {
        Event event = new Event("Event", LocalDate.now(), Occasion.OTHER, null);
        assertFalse(event.isDeleted());

        event.markDeleted();
        assertTrue(event.isDeleted());
    }

    @Test
    @DisplayName("Test removeEnjoyer removes association")
    void testRemoveEnjoyer() {
        Event event = new Event("Event", LocalDate.now(), Occasion.BIRTHDAY, enjoyer);
        assertNotNull(event.getEnjoyer());

        event.removeEnjoyer();
        // After removal, the enjoyer should still be accessible but the bidirectional link is broken
        assertNotNull(event.getEnjoyer());
    }

    @Test
    @DisplayName("Test removeEnjoyer with null enjoyer does not throw")
    void testRemoveEnjoyerNull() {
        Event event = new Event("Event", LocalDate.now(), Occasion.OTHER, null);
        assertDoesNotThrow(() -> event.removeEnjoyer());
    }

    @Test
    @DisplayName("Test Event with all occasion types")
    void testAllOccasions() {
        for (Occasion occasion : Occasion.values()) {
            Event event = new Event("Event", LocalDate.now(), occasion, null);
            assertEquals(occasion, event.getOccasion());
        }
    }

    @Test
    @DisplayName("Test Event with past date")
    void testEventWithPastDate() {
        LocalDate pastDate = LocalDate.now().minusDays(10);
        Event event = new Event("Past Event", pastDate, Occasion.OTHER, null);
        assertEquals(pastDate, event.getDate());
    }

    @Test
    @DisplayName("Test Event with future date")
    void testEventWithFutureDate() {
        LocalDate futureDate = LocalDate.now().plusYears(1);
        Event event = new Event("Future Event", futureDate, Occasion.OTHER, null);
        assertEquals(futureDate, event.getDate());
    }
}

