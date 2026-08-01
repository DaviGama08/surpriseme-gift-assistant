package pt.isec.gps2526_g42.surprise_me.model.data;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Enjoyer Tests")
class EnjoyerTest {

    private EnjoyerDetails enjoyerDetails;
    private Enjoyer enjoyer;

    @BeforeEach
    void setUp() {
        enjoyerDetails = new EnjoyerDetails();
        enjoyerDetails.setName("Alice");
        enjoyerDetails.setBirthDate(LocalDate.of(1990, 5, 15));
        enjoyer = new Enjoyer(enjoyerDetails);
    }

    @Test
    @DisplayName("Test Enjoyer constructor with valid details")
    void testConstructorValid() {
        assertNotNull(enjoyer);
        assertEquals("Alice", enjoyer.getDetails().getName());
    }

    @Test
    @DisplayName("Test Enjoyer constructor assigns unique id")
    void testConstructorUniqueId() {
        EnjoyerDetails details2 = new EnjoyerDetails();
        details2.setName("Bob");
        Enjoyer enjoyer2 = new Enjoyer(details2);

        assertNotEquals(enjoyer.getIdEnjoyer(), enjoyer2.getIdEnjoyer());
    }

    @Test
    @DisplayName("Test getIdEnjoyer returns valid id")
    void testGetIdEnjoyer() {
        assertTrue(enjoyer.getIdEnjoyer() > 0);
    }

    @Test
    @DisplayName("Test getDetails returns EnjoyerDetails")
    void testGetDetails() {
        EnjoyerDetails details = enjoyer.getDetails();
        assertNotNull(details);
        assertEquals("Alice", details.getName());
        assertEquals(LocalDate.of(1990, 5, 15), details.getBirthDate());
    }

    @Test
    @DisplayName("Test editEnjoyer with valid details")
    void testEditEnjoyerValid() {
        EnjoyerDetails newDetails = new EnjoyerDetails();
        newDetails.setName("Bob");
        newDetails.setBirthDate(LocalDate.of(1985, 3, 20));

        enjoyer.editEnjoyer(newDetails);

        assertEquals("Bob", enjoyer.getDetails().getName());
        assertEquals(LocalDate.of(1985, 3, 20), enjoyer.getDetails().getBirthDate());
    }

    @Test
    @DisplayName("Test editEnjoyer with null details throws exception")
    void testEditEnjoyerNull() {
        // The current implementation does not validate null and will throw NullPointerException
        // This is the actual behavior of the code
        assertThrows(NullPointerException.class, () -> enjoyer.editEnjoyer(null));
    }

    @Test
    @DisplayName("Test addEvent adds event to collection")
    void testAddEvent() {
        // When Event is created with an enjoyer, it automatically adds itself to the enjoyer
        Event event = new Event("Birthday Party", LocalDate.now().plusDays(10),
                               pt.isec.gps2526_g42.surprise_me.model.Occasion.BIRTHDAY, enjoyer);

        // No need to call enjoyer.addEvent(event) - constructor already did it
        ArrayList<Event> events = enjoyer.getEvents();
        assertTrue(events.contains(event), "Event should be automatically added by constructor");
    }

    @Test
    @DisplayName("Test addEvent with null - implementation accepts it")
    void testAddEventNull() {
        // Create completely isolated enjoyer
        EnjoyerDetails testDetails = new EnjoyerDetails();
        testDetails.setName("IsolatedTest");
        Enjoyer testEnjoyer = new Enjoyer(testDetails);

        // addEvent should work with null (no validation in current implementation)
        assertDoesNotThrow(() -> testEnjoyer.addEvent(null),
                          "addEvent should not throw when adding null");
    }

    @Test
    @DisplayName("Test removeEvent functionality")
    void testRemoveEvent() {
        // Create completely isolated enjoyer
        EnjoyerDetails testDetails = new EnjoyerDetails();
        testDetails.setName("RemoveTest");
        Enjoyer testEnjoyer = new Enjoyer(testDetails);

        // Event constructor automatically adds itself to the enjoyer
        Event event = new Event("TestEvent", LocalDate.now().plusDays(5),
                               pt.isec.gps2526_g42.surprise_me.model.Occasion.OTHER, testEnjoyer);

        // Verify event is in collection (added by constructor)
        assertTrue(testEnjoyer.getEvents().stream().anyMatch(e -> e != null && e.equals(event)),
                  "Event should be in collection after constructor");

        // Remove the event - should not throw
        assertDoesNotThrow(() -> testEnjoyer.removeEvent(event),
                          "removeEvent should not throw exception");
    }

    @Test
    @DisplayName("Test removeEvent with null does nothing")
    void testRemoveEventNull() {
        int initialSize = enjoyer.getEvents().size();
        enjoyer.removeEvent(null);
        assertEquals(initialSize, enjoyer.getEvents().size());
    }

    @Test
    @DisplayName("Test removeEvent with non-existing event")
    void testRemoveEventNonExisting() {
        // Constructor automatically adds event1 to enjoyer
        Event event1 = new Event("Event1", LocalDate.now(),
                                pt.isec.gps2526_g42.surprise_me.model.Occasion.OTHER, enjoyer);
        // event2 is created with null enjoyer, so NOT added automatically
        Event event2 = new Event("Event2", LocalDate.now(),
                                pt.isec.gps2526_g42.surprise_me.model.Occasion.OTHER, null);

        int initialSize = enjoyer.getEvents().size();

        // Verify event1 is in collection but event2 is not
        assertTrue(enjoyer.getEvents().contains(event1), "Event1 should be in collection");
        assertFalse(enjoyer.getEvents().contains(event2), "Event2 should not be in collection");

        // Try to remove event2 which was never added
        enjoyer.removeEvent(event2);
        assertEquals(initialSize, enjoyer.getEvents().size(),
                    "Size should not change when removing non-existing event");
    }

    @Test
    @DisplayName("Test getEvents returns ArrayList")
    void testGetEvents() {
        ArrayList<Event> events = enjoyer.getEvents();
        assertNotNull(events);
    }

    @Test
    @DisplayName("Test multiple events can be added")
    void testMultipleEvents() {
        // Event constructor automatically adds itself to the enjoyer
        Event event1 = new Event("Event1", LocalDate.now().plusDays(5),
                                pt.isec.gps2526_g42.surprise_me.model.Occasion.BIRTHDAY, enjoyer);
        Event event2 = new Event("Event2", LocalDate.now().plusDays(10),
                                pt.isec.gps2526_g42.surprise_me.model.Occasion.OTHER, enjoyer);

        // No need to call addEvent - constructor already did it
        ArrayList<Event> events = enjoyer.getEvents();
        assertTrue(events.contains(event1), "First event should be in collection");
        assertTrue(events.contains(event2), "Second event should be in collection");
        assertTrue(events.size() >= 2, "Should have at least 2 events");
    }

    @Test
    @DisplayName("Test addEvent multiple times with same event creates duplicate")
    void testAddEventDuplicate() {
        // Constructor automatically adds event once
        Event event = new Event("Event", LocalDate.now(),
                               pt.isec.gps2526_g42.surprise_me.model.Occasion.OTHER, enjoyer);
        int sizeAfterConstructor = enjoyer.getEvents().size();

        // Manually adding again should create duplicate
        enjoyer.addEvent(event);

        // Size should increase (no duplicate prevention in current implementation)
        assertEquals(sizeAfterConstructor + 1, enjoyer.getEvents().size(),
                    "Adding same event manually should create duplicate");
    }

    @Test
    @DisplayName("Test editEnjoyer preserves id")
    void testEditEnjoyerPreservesId() {
        int originalId = enjoyer.getIdEnjoyer();

        EnjoyerDetails newDetails = new EnjoyerDetails();
        newDetails.setName("Updated Name");
        enjoyer.editEnjoyer(newDetails);

        assertEquals(originalId, enjoyer.getIdEnjoyer());
    }

    @Test
    @DisplayName("Test editEnjoyer updates all fields")
    void testEditEnjoyerUpdatesAllFields() {
        EnjoyerDetails newDetails = new EnjoyerDetails();
        newDetails.setName("Charlie");
        newDetails.setBirthDate(LocalDate.of(2000, 12, 25));

        ArrayList<String> likes = new ArrayList<>();
        likes.add("Gaming");
        newDetails.setLikes(likes);

        ArrayList<String> dislikes = new ArrayList<>();
        dislikes.add("Vegetables");
        newDetails.setDislikes(dislikes);

        newDetails.setNotes("Loves tech");
        newDetails.setRelationship("Cousin");

        enjoyer.editEnjoyer(newDetails);

        assertEquals("Charlie", enjoyer.getDetails().getName());
        assertEquals(LocalDate.of(2000, 12, 25), enjoyer.getDetails().getBirthDate());
        assertEquals(likes, enjoyer.getDetails().getLikes());
        assertEquals(dislikes, enjoyer.getDetails().getDislikes());
        assertEquals("Loves tech", enjoyer.getDetails().getNotes());
        assertEquals("Cousin", enjoyer.getDetails().getRelationship());
    }

    @Test
    @DisplayName("Test getEvents after removing all events")
    void testGetEventsAfterRemovingAll() {
        // Constructor automatically adds events
        Event event1 = new Event("Event1", LocalDate.now(),
                                pt.isec.gps2526_g42.surprise_me.model.Occasion.OTHER, enjoyer);
        Event event2 = new Event("Event2", LocalDate.now(),
                                pt.isec.gps2526_g42.surprise_me.model.Occasion.OTHER, enjoyer);

        // No need to call addEvent - constructor already did it

        enjoyer.removeEvent(event1);
        enjoyer.removeEvent(event2);

        ArrayList<Event> events = enjoyer.getEvents();
        assertNotNull(events, "getEvents should never return null");
        assertFalse(events.contains(event1), "Event1 should be removed");
        assertFalse(events.contains(event2), "Event2 should be removed");
    }
}

