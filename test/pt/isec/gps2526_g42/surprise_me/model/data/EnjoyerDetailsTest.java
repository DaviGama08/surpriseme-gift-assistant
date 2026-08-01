package pt.isec.gps2526_g42.surprise_me.model.data;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("EnjoyerDetails Tests")
class EnjoyerDetailsTest {

    private EnjoyerDetails enjoyerDetails;

    @BeforeEach
    void setUp() {
        enjoyerDetails = new EnjoyerDetails();
    }

    @Test
    @DisplayName("Test default constructor")
    void testDefaultConstructor() {
        assertNotNull(enjoyerDetails);
        assertNull(enjoyerDetails.getName());
        assertNull(enjoyerDetails.getBirthDate());
        assertTrue(enjoyerDetails.getLikes().isEmpty());
        assertTrue(enjoyerDetails.getDislikes().isEmpty());
    }

    @Test
    @DisplayName("Test setName and getName")
    void testSetAndGetName() {
        enjoyerDetails.setName("Alice");
        assertEquals("Alice", enjoyerDetails.getName());
    }

    @Test
    @DisplayName("Test setName with space ignores value")
    void testSetNameNull() {
        enjoyerDetails.setName("Alice");
        enjoyerDetails.setName(" ");
        assertEquals("Alice", enjoyerDetails.getName());
    }

    @Test
    @DisplayName("Test setBirthDate and getBirthDate")
    void testSetAndGetBirthDate() {
        LocalDate birthDate = LocalDate.of(1990, 5, 15);
        enjoyerDetails.setBirthDate(birthDate);
        assertEquals(birthDate, enjoyerDetails.getBirthDate());
    }

    @Test
    @DisplayName("Test setLikes and getLikes")
    void testSetAndGetLikes() {
        ArrayList<String> likes = new ArrayList<>();
        likes.add("Books");
        likes.add("Music");
        enjoyerDetails.setLikes(likes);
        assertEquals(2, enjoyerDetails.getLikes().size());
        assertTrue(enjoyerDetails.getLikes().contains("Books"));
    }

    @Test
    @DisplayName("Test setDislikes and getDislikes")
    void testSetAndGetDislikes() {
        ArrayList<String> dislikes = new ArrayList<>();
        dislikes.add("Sports");
        enjoyerDetails.setDislikes(dislikes);
        assertEquals(1, enjoyerDetails.getDislikes().size());
    }

    @Test
    @DisplayName("Test setNotes and getNotes")
    void testSetAndGetNotes() {
        enjoyerDetails.setNotes("Prefers handmade gifts");
        assertEquals("Prefers handmade gifts", enjoyerDetails.getNotes());
    }

    @Test
    @DisplayName("Test setRelationship and getRelationship")
    void testSetAndGetRelationship() {
        enjoyerDetails.setRelationship("Friend");
        assertEquals("Friend", enjoyerDetails.getRelationship());
    }

    @Test
    @DisplayName("Test setCity and getCity")
    void testSetAndGetCity() {
        enjoyerDetails.setCity("Lisbon");
        assertEquals("Lisbon", enjoyerDetails.getCity());
    }

    @Test
    @DisplayName("Test setCountry and getCountry")
    void testSetAndGetCountry() {
        enjoyerDetails.setCountry("Portugal");
        assertEquals("Portugal", enjoyerDetails.getCountry());
    }
}

