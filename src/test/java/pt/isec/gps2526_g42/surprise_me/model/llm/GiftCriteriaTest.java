package pt.isec.gps2526_g42.surprise_me.model.llm;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("GiftCriteria Tests")
class GiftCriteriaTest {

    private GiftCriteria criteria;

    @BeforeEach
    void setUp() {
        criteria = new GiftCriteria();
    }

    @Test
    @DisplayName("Test default constructor")
    void testDefaultConstructor() {
        assertNotNull(criteria);
        assertEquals(0.0, criteria.getMinBudget());
        assertEquals(0.0, criteria.getMaxBudget());
        assertNull(criteria.getOccasion());
        assertNull(criteria.getGiftType());
        assertNull(criteria.getThingsToAvoid());
        assertNull(criteria.getAdditionalIdeas());
        assertFalse(criteria.isSustainable());
        assertFalse(criteria.isUseful());
    }

    @Test
    @DisplayName("Test setMinBudget and getMinBudget")
    void testSetAndGetMinBudget() {
        criteria.setMinBudget(50.0);
        assertEquals(50.0, criteria.getMinBudget());
    }

    @Test
    @DisplayName("Test setMaxBudget and getMaxBudget")
    void testSetAndGetMaxBudget() {
        criteria.setMaxBudget(100.0);
        assertEquals(100.0, criteria.getMaxBudget());
    }

    @Test
    @DisplayName("Test budget range")
    void testBudgetRange() {
        criteria.setMinBudget(50.0);
        criteria.setMaxBudget(150.0);
        assertEquals(50.0, criteria.getMinBudget());
        assertEquals(150.0, criteria.getMaxBudget());
    }

    @Test
    @DisplayName("Test setOccasion and getOccasion")
    void testSetAndGetOccasion() {
        criteria.setOccasion("Birthday");
        assertEquals("Birthday", criteria.getOccasion());
    }

    @Test
    @DisplayName("Test setOccasion with null")
    void testSetOccasionNull() {
        criteria.setOccasion("Birthday");
        criteria.setOccasion(null);
        assertNull(criteria.getOccasion());
    }

    @Test
    @DisplayName("Test setGiftType and getGiftType")
    void testSetAndGetGiftType() {
        criteria.setGiftType("Physical");
        assertEquals("Physical", criteria.getGiftType());
    }

    @Test
    @DisplayName("Test setGiftType with null")
    void testSetGiftTypeNull() {
        criteria.setGiftType("Experience");
        criteria.setGiftType(null);
        assertNull(criteria.getGiftType());
    }

    @Test
    @DisplayName("Test setThingsToAvoid and getThingsToAvoid")
    void testSetAndGetThingsToAvoid() {
        criteria.setThingsToAvoid("Electronics, Books");
        assertEquals("Electronics, Books", criteria.getThingsToAvoid());
    }

    @Test
    @DisplayName("Test setThingsToAvoid with null")
    void testSetThingsToAvoidNull() {
        criteria.setThingsToAvoid("Something");
        criteria.setThingsToAvoid(null);
        assertNull(criteria.getThingsToAvoid());
    }

    @Test
    @DisplayName("Test setAdditionalIdeas and getAdditionalIdeas")
    void testSetAndGetAdditionalIdeas() {
        criteria.setAdditionalIdeas("Something personalized");
        assertEquals("Something personalized", criteria.getAdditionalIdeas());
    }

    @Test
    @DisplayName("Test setAdditionalIdeas with null")
    void testSetAdditionalIdeasNull() {
        criteria.setAdditionalIdeas("Ideas");
        criteria.setAdditionalIdeas(null);
        assertNull(criteria.getAdditionalIdeas());
    }

    @Test
    @DisplayName("Test setSustainable and isSustainable")
    void testSetAndIsSustainable() {
        criteria.setSustainable(true);
        assertTrue(criteria.isSustainable());

        criteria.setSustainable(false);
        assertFalse(criteria.isSustainable());
    }

    @Test
    @DisplayName("Test setUseful and isUseful")
    void testSetAndIsUseful() {
        criteria.setUseful(true);
        assertTrue(criteria.isUseful());

        criteria.setUseful(false);
        assertFalse(criteria.isUseful());
    }

    @Test
    @DisplayName("Test all fields set together")
    void testAllFieldsSet() {
        criteria.setMinBudget(50.0);
        criteria.setMaxBudget(200.0);
        criteria.setOccasion("Christmas");
        criteria.setGiftType("Physical");
        criteria.setThingsToAvoid("Electronics");
        criteria.setAdditionalIdeas("Handmade");
        criteria.setSustainable(true);
        criteria.setUseful(true);

        assertEquals(50.0, criteria.getMinBudget());
        assertEquals(200.0, criteria.getMaxBudget());
        assertEquals("Christmas", criteria.getOccasion());
        assertEquals("Physical", criteria.getGiftType());
        assertEquals("Electronics", criteria.getThingsToAvoid());
        assertEquals("Handmade", criteria.getAdditionalIdeas());
        assertTrue(criteria.isSustainable());
        assertTrue(criteria.isUseful());
    }

    @Test
    @DisplayName("Test updating fields multiple times")
    void testUpdateFieldsMultipleTimes() {
        criteria.setMinBudget(50.0);
        criteria.setMinBudget(100.0);
        criteria.setMinBudget(75.0);
        assertEquals(75.0, criteria.getMinBudget());

        criteria.setOccasion("Birthday");
        criteria.setOccasion("Anniversary");
        criteria.setOccasion("Christmas");
        assertEquals("Christmas", criteria.getOccasion());
    }

    @Test
    @DisplayName("Test with zero budget")
    void testZeroBudget() {
        criteria.setMinBudget(0.0);
        criteria.setMaxBudget(0.0);
        assertEquals(0.0, criteria.getMinBudget());
        assertEquals(0.0, criteria.getMaxBudget());
    }

    @Test
    @DisplayName("Test with negative budget")
    void testNegativeBudget() {
        criteria.setMinBudget(-10.0);
        criteria.setMaxBudget(-5.0);
        assertEquals(-10.0, criteria.getMinBudget());
        assertEquals(-5.0, criteria.getMaxBudget());
    }

    @Test
    @DisplayName("Test with large budget values")
    void testLargeBudgetValues() {
        criteria.setMinBudget(10000.99);
        criteria.setMaxBudget(99999.99);
        assertEquals(10000.99, criteria.getMinBudget());
        assertEquals(99999.99, criteria.getMaxBudget());
    }

    @Test
    @DisplayName("Test occasion with special characters")
    void testOccasionSpecialCharacters() {
        criteria.setOccasion("Valentine's Day");
        assertEquals("Valentine's Day", criteria.getOccasion());
    }

    @Test
    @DisplayName("Test clearing all string fields")
    void testClearingStringFields() {
        criteria.setOccasion("Birthday");
        criteria.setGiftType("Physical");
        criteria.setThingsToAvoid("Something");
        criteria.setAdditionalIdeas("Ideas");

        criteria.setOccasion(null);
        criteria.setGiftType(null);
        criteria.setThingsToAvoid(null);
        criteria.setAdditionalIdeas(null);

        assertNull(criteria.getOccasion());
        assertNull(criteria.getGiftType());
        assertNull(criteria.getThingsToAvoid());
        assertNull(criteria.getAdditionalIdeas());
    }
}

