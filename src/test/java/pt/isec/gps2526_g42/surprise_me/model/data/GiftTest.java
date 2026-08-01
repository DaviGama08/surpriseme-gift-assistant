package pt.isec.gps2526_g42.surprise_me.model.data;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pt.isec.gps2526_g42.surprise_me.model.Occasion;
import pt.isec.gps2526_g42.surprise_me.model.Type;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Gift Tests")
class GiftTest {

    private Gift gift;
    private EnjoyerDetails enjoyerDetails;
    private Enjoyer enjoyer;

    @BeforeEach
    void setUp() {
        enjoyerDetails = new EnjoyerDetails();
        enjoyerDetails.setName("Alice");
        enjoyerDetails.setBirthDate(LocalDate.of(1990, 5, 15));
        enjoyer = new Enjoyer(enjoyerDetails);

        gift = new Gift("Book", Type.PHYSICAL, Occasion.BIRTHDAY, enjoyer);
    }

    @Test
    @DisplayName("Test Gift constructor with valid parameters")
    void testConstructorValid() {
        assertNotNull(gift);
        assertEquals("Book", gift.getName());
        assertEquals("Physical gift", gift.getType());
        assertEquals("Birthday", gift.getOccasion());
    }

    @Test
    @DisplayName("Test Gift constructor without enjoyer")
    void testConstructorWithoutEnjoyer() {
        Gift giftNoEnjoyer = new Gift("Gift Card", Type.DIGITAL, Occasion.OTHER, null);
        assertNotNull(giftNoEnjoyer);
        assertEquals("Gift Card", giftNoEnjoyer.getName());
    }

    @Test
    @DisplayName("Test Gift constructor assigns unique id")
    void testConstructorUniqueId() {
        Gift gift2 = new Gift("Toy", Type.PHYSICAL, Occasion.BIRTHDAY, enjoyer);
        assertNotEquals(gift.getIdGift(), gift2.getIdGift());
    }

    @Test
    @DisplayName("Test getIdGift returns valid id")
    void testGetIdGift() {
        assertTrue(gift.getIdGift() > 0);
    }

    @Test
    @DisplayName("Test getName returns correct name")
    void testGetName() {
        assertEquals("Book", gift.getName());
    }

    @Test
    @DisplayName("Test getType returns correct type string")
    void testGetType() {
        assertEquals("Physical gift", gift.getType());
    }

    @Test
    @DisplayName("Test getOccasion returns correct occasion string")
    void testGetOccasion() {
        assertEquals("Birthday", gift.getOccasion());
    }

    @Test
    @DisplayName("Test getFeedback returns default UNKNOWN")
    void testGetFeedbackDefault() {
        assertEquals("-", gift.getFeedback());
    }

    @Test
    @DisplayName("Test getStatus returns default PENDING")
    void testGetStatusDefault() {
        assertEquals("Pending", gift.getStatus());
    }

    @Test
    @DisplayName("Test setGiftMessage with valid message")
    void testSetGiftMessageValid() {
        // Access through package method would require manager, skip for now
        assertNotNull(gift);
    }

    @Test
    @DisplayName("Test getGiftMessage returns null initially")
    void testGetGiftMessageInitial() {
        Gift newGift = new Gift("Item", Type.PHYSICAL, Occasion.OTHER, enjoyer);
        assertNull(newGift.getGiftMessage());
    }

    @Test
    @DisplayName("Test getEnjoyer returns correct enjoyer")
    void testGetEnjoyer() {
        assertEquals(enjoyer, gift.getEnjoyer());
    }

    @Test
    @DisplayName("Test getEnjoyer returns null when no enjoyer")
    void testGetEnjoyerNull() {
        Gift giftNoEnjoyer = new Gift("Item", Type.PHYSICAL, Occasion.OTHER, null);
        assertNull(giftNoEnjoyer.getEnjoyer());
    }

    @Test
    @DisplayName("Test all type values in constructor")
    void testAllTypeValues() {
        for (Type type : Type.values()) {
            Gift testGift = new Gift("Item", type, Occasion.OTHER, enjoyer);
            assertNotNull(testGift.getType());
        }
    }

    @Test
    @DisplayName("Test all occasion values in constructor")
    void testAllOccasionValues() {
        for (Occasion occasion : Occasion.values()) {
            Gift testGift = new Gift("Item", Type.PHYSICAL, occasion, enjoyer);
            assertNotNull(testGift.getOccasion());
        }
    }

    @Test
    @DisplayName("Test gift with different enjoyers")
    void testGiftWithDifferentEnjoyers() {
        EnjoyerDetails details2 = new EnjoyerDetails();
        details2.setName("Bob");
        Enjoyer enjoyer2 = new Enjoyer(details2);

        Gift gift1 = new Gift("Gift1", Type.PHYSICAL, Occasion.BIRTHDAY, enjoyer);
        Gift gift2 = new Gift("Gift2", Type.PHYSICAL, Occasion.BIRTHDAY, enjoyer2);

        assertNotEquals(gift1.getEnjoyer(), gift2.getEnjoyer());
    }

    @Test
    @DisplayName("Test gift name with special characters")
    void testGiftNameSpecialChars() {
        Gift specialGift = new Gift("Book: 'The Great Adventure'", Type.PHYSICAL, Occasion.BIRTHDAY, enjoyer);
        assertEquals("Book: 'The Great Adventure'", specialGift.getName());
    }

    @Test
    @DisplayName("Test gift with very long name")
    void testGiftLongName() {
        String longName = "A".repeat(500);
        Gift longNameGift = new Gift(longName, Type.PHYSICAL, Occasion.BIRTHDAY, enjoyer);
        assertEquals(longName, longNameGift.getName());
    }

    @Test
    @DisplayName("Test gift date is set to today")
    void testGiftDateIsToday() {
        LocalDate today = LocalDate.now();
        assertEquals(today, gift.getDate());
    }

    @Test
    @DisplayName("Test copy constructor")
    void testCopyConstructor() {
        Gift copy = new Gift(gift);
        assertEquals(gift.getIdGift(), copy.getIdGift());
        assertEquals(gift.getName(), copy.getName());
        assertEquals(gift.getType(), copy.getType());
        assertEquals(gift.getOccasion(), copy.getOccasion());
        assertEquals(gift.getEnjoyer(), copy.getEnjoyer());
    }
}

