package pt.isec.gps2526_g42.surprise_me.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Feedback Tests")
class FeedbackTest {

    @Test
    @DisplayName("Test all Feedback enum values exist")
    void testAllFeedbackValues() {
        Feedback[] values = Feedback.values();
        assertNotNull(values);
        assertTrue(values.length >= 4); // UNKNOWN, GOOD, NEUTRAL, BAD
    }

    @Test
    @DisplayName("Test Feedback.UNKNOWN")
    void testUnknown() {
        assertEquals("-", Feedback.UNKNOWN.getFeedback());
    }

    @Test
    @DisplayName("Test Feedback.GOOD")
    void testGood() {
        assertEquals("Good", Feedback.GOOD.getFeedback());
    }

    @Test
    @DisplayName("Test Feedback.NEUTRAL")
    void testNeutral() {
        assertEquals("Neutral", Feedback.NEUTRAL.getFeedback());
    }

    @Test
    @DisplayName("Test Feedback.BAD")
    void testBad() {
        assertEquals("Bad", Feedback.BAD.getFeedback());
    }

    @Test
    @DisplayName("Test convertFromString with valid values")
    void testConvertFromStringValid() {
        assertEquals(Feedback.GOOD, Feedback.convertFromString("Good"));
        assertEquals(Feedback.NEUTRAL, Feedback.convertFromString("Neutral"));
        assertEquals(Feedback.BAD, Feedback.convertFromString("Bad"));
        assertEquals(Feedback.UNKNOWN, Feedback.convertFromString("-"));
    }

    @Test
    @DisplayName("Test convertFromString case insensitive")
    void testConvertFromStringCaseInsensitive() {
        assertEquals(Feedback.GOOD, Feedback.convertFromString("good"));
        assertEquals(Feedback.NEUTRAL, Feedback.convertFromString("NEUTRAL"));
        assertEquals(Feedback.BAD, Feedback.convertFromString("BaD"));
    }

    @Test
    @DisplayName("Test convertFromString with null throws exception")
    void testConvertFromStringNull() {
        assertThrows(NullPointerException.class, () -> Feedback.convertFromString(null));
    }

    @Test
    @DisplayName("Test convertFromString with invalid string returns UNKNOWN")
    void testConvertFromStringInvalid() {
        assertEquals(Feedback.UNKNOWN, Feedback.convertFromString("invalid"));
        assertEquals(Feedback.UNKNOWN, Feedback.convertFromString(""));
    }

    @Test
    @DisplayName("Test getAllFeedbacks returns all feedback strings")
    void testGetAllFeedbacks() {
        ArrayList<String> allFeedbacks = Feedback.getAllFeedbacks();
        assertNotNull(allFeedbacks);
        assertTrue(allFeedbacks.size() >= 4);

        boolean hasGood = false;
        boolean hasNeutral = false;
        boolean hasBad = false;
        boolean hasUnknown = false;

        for (String feedback : allFeedbacks) {
            if ("Good".equals(feedback)) hasGood = true;
            if ("Neutral".equals(feedback)) hasNeutral = true;
            if ("Bad".equals(feedback)) hasBad = true;
            if ("-".equals(feedback)) hasUnknown = true;
        }

        assertTrue(hasGood);
        assertTrue(hasNeutral);
        assertTrue(hasBad);
        assertTrue(hasUnknown);
    }

    @Test
    @DisplayName("Test getFeedback returns correct string")
    void testGetFeedback() {
        assertEquals("Good", Feedback.GOOD.getFeedback());
        assertEquals("Neutral", Feedback.NEUTRAL.getFeedback());
        assertEquals("Bad", Feedback.BAD.getFeedback());
        assertEquals("-", Feedback.UNKNOWN.getFeedback());
    }

    @Test
    @DisplayName("Test valueOf works correctly")
    void testValueOf() {
        assertEquals(Feedback.GOOD, Feedback.valueOf("GOOD"));
        assertEquals(Feedback.NEUTRAL, Feedback.valueOf("NEUTRAL"));
        assertEquals(Feedback.BAD, Feedback.valueOf("BAD"));
        assertEquals(Feedback.UNKNOWN, Feedback.valueOf("UNKNOWN"));
    }

    @Test
    @DisplayName("Test convertFromString with trimmed whitespace")
    void testConvertFromStringWithWhitespace() {
        assertEquals(Feedback.GOOD, Feedback.convertFromString("  Good  "));
        assertEquals(Feedback.NEUTRAL, Feedback.convertFromString(" Neutral "));
    }
}

