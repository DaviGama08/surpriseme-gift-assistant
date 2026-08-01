package pt.isec.gps2526_g42.surprise_me.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Occasion Tests")
class OccasionTest {

    @Test
    @DisplayName("Test all Occasion enum values exist")
    void testAllOccasionValues() {
        Occasion[] values = Occasion.values();
        assertNotNull(values);
        assertTrue(values.length >= 8);
    }

    @Test
    @DisplayName("Test Occasion.BIRTHDAY")
    void testBirthday() {
        assertEquals("Birthday", Occasion.BIRTHDAY.getOccasion());
    }

    @Test
    @DisplayName("Test Occasion.CHRISTMAS")
    void testChristmas() {
        assertEquals("Christmas", Occasion.CHRISTMAS.getOccasion());
    }

    @Test
    @DisplayName("Test Occasion.NEW_YEAR")
    void testNewYear() {
        assertEquals("New Year", Occasion.NEW_YEAR.getOccasion());
    }

    @Test
    @DisplayName("Test Occasion.VALENTINES_DAY")
    void testValentinesDay() {
        assertEquals("Valentine's Day", Occasion.VALENTINES_DAY.getOccasion());
    }

    @Test
    @DisplayName("Test Occasion.MOTHERS_DAY")
    void testMothersDay() {
        assertEquals("Mother's Day", Occasion.MOTHERS_DAY.getOccasion());
    }

    @Test
    @DisplayName("Test Occasion.FATHERS_DAY")
    void testFathersDay() {
        assertEquals("Father's Day", Occasion.FATHERS_DAY.getOccasion());
    }

    @Test
    @DisplayName("Test Occasion.ANNIVERSARY")
    void testAnniversary() {
        assertEquals("Anniversary", Occasion.ANNIVERSARY.getOccasion());
    }

    @Test
    @DisplayName("Test Occasion.OTHER")
    void testOther() {
        assertEquals("Other", Occasion.OTHER.getOccasion());
    }

    @Test
    @DisplayName("Test convertFromString with valid values")
    void testConvertFromStringValid() {
        assertEquals(Occasion.BIRTHDAY, Occasion.convertFromString("Birthday"));
        assertEquals(Occasion.CHRISTMAS, Occasion.convertFromString("Christmas"));
        assertEquals(Occasion.NEW_YEAR, Occasion.convertFromString("New Year"));
        assertEquals(Occasion.VALENTINES_DAY, Occasion.convertFromString("Valentine's Day"));
        assertEquals(Occasion.MOTHERS_DAY, Occasion.convertFromString("Mother's Day"));
        assertEquals(Occasion.FATHERS_DAY, Occasion.convertFromString("Father's Day"));
        assertEquals(Occasion.ANNIVERSARY, Occasion.convertFromString("Anniversary"));
        assertEquals(Occasion.OTHER, Occasion.convertFromString("Other"));
    }

    @Test
    @DisplayName("Test convertFromString case insensitive")
    void testConvertFromStringCaseInsensitive() {
        assertEquals(Occasion.BIRTHDAY, Occasion.convertFromString("birthday"));
        assertEquals(Occasion.CHRISTMAS, Occasion.convertFromString("CHRISTMAS"));
        assertEquals(Occasion.NEW_YEAR, Occasion.convertFromString("new year"));
    }

    @Test
    @DisplayName("Test convertFromString with null throws exception")
    void testConvertFromStringNull() {
        assertThrows(NullPointerException.class, () -> Occasion.convertFromString(null));
    }

    @Test
    @DisplayName("Test convertFromString with invalid string returns OTHER")
    void testConvertFromStringInvalid() {
        assertEquals(Occasion.OTHER, Occasion.convertFromString("invalid"));
        assertEquals(Occasion.OTHER, Occasion.convertFromString(""));
    }

    @Test
    @DisplayName("Test getAllOccasions returns all occasion strings")
    void testGetAllOccasions() {
        ArrayList<String> allOccasions = Occasion.getAllOccasions();
        assertNotNull(allOccasions);
        assertTrue(allOccasions.size() >= 8);

        boolean hasBirthday = false;
        boolean hasChristmas = false;
        boolean hasNewYear = false;
        boolean hasValentines = false;
        boolean hasMothers = false;
        boolean hasFathers = false;
        boolean hasAnniversary = false;
        boolean hasOther = false;

        for (String occasion : allOccasions) {
            if ("Birthday".equals(occasion)) hasBirthday = true;
            if ("Christmas".equals(occasion)) hasChristmas = true;
            if ("New Year".equals(occasion)) hasNewYear = true;
            if ("Valentine's Day".equals(occasion)) hasValentines = true;
            if ("Mother's Day".equals(occasion)) hasMothers = true;
            if ("Father's Day".equals(occasion)) hasFathers = true;
            if ("Anniversary".equals(occasion)) hasAnniversary = true;
            if ("Other".equals(occasion)) hasOther = true;
        }

        assertTrue(hasBirthday);
        assertTrue(hasChristmas);
        assertTrue(hasNewYear);
        assertTrue(hasValentines);
        assertTrue(hasMothers);
        assertTrue(hasFathers);
        assertTrue(hasAnniversary);
        assertTrue(hasOther);
    }

    @Test
    @DisplayName("Test getOccasion returns correct string")
    void testGetOccasion() {
        assertEquals("Birthday", Occasion.BIRTHDAY.getOccasion());
        assertEquals("Christmas", Occasion.CHRISTMAS.getOccasion());
        assertEquals("New Year", Occasion.NEW_YEAR.getOccasion());
        assertEquals("Valentine's Day", Occasion.VALENTINES_DAY.getOccasion());
        assertEquals("Mother's Day", Occasion.MOTHERS_DAY.getOccasion());
        assertEquals("Father's Day", Occasion.FATHERS_DAY.getOccasion());
        assertEquals("Anniversary", Occasion.ANNIVERSARY.getOccasion());
        assertEquals("Other", Occasion.OTHER.getOccasion());
    }

    @Test
    @DisplayName("Test valueOf works correctly")
    void testValueOf() {
        assertEquals(Occasion.BIRTHDAY, Occasion.valueOf("BIRTHDAY"));
        assertEquals(Occasion.CHRISTMAS, Occasion.valueOf("CHRISTMAS"));
        assertEquals(Occasion.NEW_YEAR, Occasion.valueOf("NEW_YEAR"));
        assertEquals(Occasion.VALENTINES_DAY, Occasion.valueOf("VALENTINES_DAY"));
        assertEquals(Occasion.MOTHERS_DAY, Occasion.valueOf("MOTHERS_DAY"));
        assertEquals(Occasion.FATHERS_DAY, Occasion.valueOf("FATHERS_DAY"));
        assertEquals(Occasion.ANNIVERSARY, Occasion.valueOf("ANNIVERSARY"));
        assertEquals(Occasion.OTHER, Occasion.valueOf("OTHER"));
    }

    @Test
    @DisplayName("Test convertFromString with trimmed whitespace")
    void testConvertFromStringWithWhitespace() {
        assertEquals(Occasion.BIRTHDAY, Occasion.convertFromString("  Birthday  "));
        assertEquals(Occasion.CHRISTMAS, Occasion.convertFromString(" Christmas "));
    }

    @Test
    @DisplayName("Test all occasions can be converted back and forth")
    void testConvertBackAndForth() {
        for (Occasion occasion : Occasion.values()) {
            String occasionString = occasion.getOccasion();
            Occasion converted = Occasion.convertFromString(occasionString);
            assertEquals(occasion, converted);
        }
    }

    @Test
    @DisplayName("Test occasions with apostrophes")
    void testOccasionsWithApostrophes() {
        assertEquals("Valentine's Day", Occasion.VALENTINES_DAY.getOccasion());
        assertEquals("Mother's Day", Occasion.MOTHERS_DAY.getOccasion());
        assertEquals("Father's Day", Occasion.FATHERS_DAY.getOccasion());

        assertEquals(Occasion.VALENTINES_DAY, Occasion.convertFromString("Valentine's Day"));
        assertEquals(Occasion.MOTHERS_DAY, Occasion.convertFromString("Mother's Day"));
        assertEquals(Occasion.FATHERS_DAY, Occasion.convertFromString("Father's Day"));
    }
}

