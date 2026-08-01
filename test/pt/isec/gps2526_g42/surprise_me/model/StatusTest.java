package pt.isec.gps2526_g42.surprise_me.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Status Tests")
class StatusTest {

    @Test
    @DisplayName("Test all Status enum values exist")
    void testAllStatusValues() {
        Status[] values = Status.values();
        assertNotNull(values);
        assertTrue(values.length >= 2); // PENDING, GIFTED
    }

    @Test
    @DisplayName("Test Status.PENDING")
    void testPending() {
        assertEquals("Pending", Status.PENDING.getStatus());
    }

    @Test
    @DisplayName("Test Status.GIFTED")
    void testGifted() {
        assertEquals("Gifted", Status.GIFTED.getStatus());
    }

    @Test
    @DisplayName("Test convertFromString with valid values")
    void testConvertFromStringValid() {
        assertEquals(Status.PENDING, Status.convertFromString("Pending"));
        assertEquals(Status.GIFTED, Status.convertFromString("Gifted"));
    }

    @Test
    @DisplayName("Test convertFromString case insensitive")
    void testConvertFromStringCaseInsensitive() {
        assertEquals(Status.PENDING, Status.convertFromString("pending"));
        assertEquals(Status.GIFTED, Status.convertFromString("GIFTED"));
        assertEquals(Status.PENDING, Status.convertFromString("PeNdInG"));
    }

    @Test
    @DisplayName("Test convertFromString with null throws exception")
    void testConvertFromStringNull() {
        assertThrows(NullPointerException.class, () -> Status.convertFromString(null));
    }

    @Test
    @DisplayName("Test convertFromString with invalid string returns PENDING")
    void testConvertFromStringInvalid() {
        assertEquals(Status.PENDING, Status.convertFromString("invalid"));
        assertEquals(Status.PENDING, Status.convertFromString(""));
    }

    @Test
    @DisplayName("Test getAllStatuses returns all status strings")
    void testGetAllStatuses() {
        ArrayList<String> allStatuses = Status.getAllStatuses();
        assertNotNull(allStatuses);
        assertTrue(allStatuses.size() >= 2);

        boolean hasPending = false;
        boolean hasGifted = false;

        for (String status : allStatuses) {
            if ("Pending".equals(status)) hasPending = true;
            if ("Gifted".equals(status)) hasGifted = true;
        }

        assertTrue(hasPending);
        assertTrue(hasGifted);
    }

    @Test
    @DisplayName("Test getStatus returns correct string")
    void testGetStatus() {
        assertEquals("Pending", Status.PENDING.getStatus());
        assertEquals("Gifted", Status.GIFTED.getStatus());
    }

    @Test
    @DisplayName("Test valueOf works correctly")
    void testValueOf() {
        assertEquals(Status.PENDING, Status.valueOf("PENDING"));
        assertEquals(Status.GIFTED, Status.valueOf("GIFTED"));
    }

    @Test
    @DisplayName("Test convertFromString with trimmed whitespace")
    void testConvertFromStringWithWhitespace() {
        assertEquals(Status.PENDING, Status.convertFromString("  Pending  "));
        assertEquals(Status.GIFTED, Status.convertFromString(" Gifted "));
    }

    @Test
    @DisplayName("Test Status enum order")
    void testStatusEnumOrder() {
        Status[] values = Status.values();
        assertEquals(Status.PENDING, values[0]);
        assertEquals(Status.GIFTED, values[1]);
    }

    @Test
    @DisplayName("Test convertFromString returns first match")
    void testConvertFromStringReturnsCorrectMatch() {
        // Test that "pending" returns PENDING not GIFTED
        Status result = Status.convertFromString("pending");
        assertEquals(Status.PENDING, result);
        assertEquals("Pending", result.getStatus());
    }
}

