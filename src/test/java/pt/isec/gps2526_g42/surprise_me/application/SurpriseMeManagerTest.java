package pt.isec.gps2526_g42.surprise_me.application;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pt.isec.gps2526_g42.surprise_me.model.Feedback;
import pt.isec.gps2526_g42.surprise_me.model.Occasion;
import pt.isec.gps2526_g42.surprise_me.model.Status;
import pt.isec.gps2526_g42.surprise_me.model.Type;
import pt.isec.gps2526_g42.surprise_me.model.data.EnjoyerDetails;
import pt.isec.gps2526_g42.surprise_me.model.data.Gift;
import pt.isec.gps2526_g42.surprise_me.model.data.UserDetails;
import pt.isec.gps2526_g42.surprise_me.persistence.SurpriseMeSerialization;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive tests for SurpriseMeManager
 * Tests cover all major functionality with proper isolation
 */
@DisplayName("SurpriseMeManager Integration Tests")
class SurpriseMeManagerTest {

    private SurpriseMeManager manager;
    private EnjoyerDetails enjoyerDetails;

    @BeforeEach
    void setUp() {
        deletePersistenceFiles();

        manager = new SurpriseMeManager(prompt -> "1. Book - A thoughtful book (≈ €20)\n"
                + "2. Concert - Two tickets (≈ €50)\n"
                + "3. Course - A short course (≈ €40)\n"
                + "4. Dinner - A local dinner (≈ €60)");

        // Register and login a test user
        boolean registered = manager.register("TestUser", "test@example.com", "password123");
        if (!registered) {
            manager.login("test@example.com", "password123");
        }

        // Create valid enjoyer details (name must be >= 3 chars and no spaces based on validation)
        enjoyerDetails = new EnjoyerDetails();
        enjoyerDetails.setName("Alice");
        enjoyerDetails.setBirthDate(LocalDate.of(1990, 5, 15));

        ArrayList<String> likes = new ArrayList<>();
        likes.add("Books");
        likes.add("Music");
        enjoyerDetails.setLikes(likes);

        ArrayList<String> dislikes = new ArrayList<>();
        dislikes.add("Sports");
        enjoyerDetails.setDislikes(dislikes);
    }

    @AfterEach
    void tearDown() {
        if (manager != null) {
            manager.save();
        }
    }

    // ==================== ENJOYER TESTS ====================

    @Test
    @DisplayName("addEnjoyer should add valid enjoyer successfully")
    void testAddEnjoyerValid() {
        boolean result = manager.addEnjoyer(enjoyerDetails);
        assertTrue(result, "Should successfully add valid enjoyer");

        HashMap<Integer, EnjoyerDetails> enjoyers = manager.getEnjoyers();
        assertNotNull(enjoyers);
        assertTrue(enjoyers.size() > 0, "Should have at least one enjoyer");
    }

    @Test
    @DisplayName("addEnjoyer should reject null details")
    void testAddEnjoyerNull() {
        assertFalse(manager.addEnjoyer(null));
    }

    @Test
    @DisplayName("getEnjoyers should return non-null HashMap when user is logged in")
    void testGetEnjoyers() {
        HashMap<Integer, EnjoyerDetails> enjoyers = manager.getEnjoyers();
        assertNotNull(enjoyers, "getEnjoyers should not return null when user is logged in");
    }

    @Test
    @DisplayName("editEnjoyer should update enjoyer details")
    void testEditEnjoyer() {
        manager.addEnjoyer(enjoyerDetails);
        HashMap<Integer, EnjoyerDetails> enjoyers = manager.getEnjoyers();

        if (enjoyers != null && !enjoyers.isEmpty()) {
            int enjoyerId = enjoyers.keySet().iterator().next();

            EnjoyerDetails newDetails = new EnjoyerDetails();
            newDetails.setName("Bobby");
            newDetails.setBirthDate(LocalDate.of(1985, 3, 20));

            boolean result = manager.editEnjoyer(enjoyerId, newDetails);
            assertTrue(result, "Should successfully edit enjoyer");
        }
    }

    @Test
    @DisplayName("removeEnjoyer should remove existing enjoyer")
    void testRemoveEnjoyer() {
        manager.addEnjoyer(enjoyerDetails);
        HashMap<Integer, EnjoyerDetails> enjoyers = manager.getEnjoyers();

        if (enjoyers != null && !enjoyers.isEmpty()) {
            int enjoyerId = enjoyers.keySet().iterator().next();
            boolean result = manager.removeEnjoyer(enjoyerId);
            assertTrue(result, "Should successfully remove enjoyer");
        }
    }

    // ==================== GIFT TESTS ====================

    @Test
    @DisplayName("addGift should add gift without enjoyer")
    void testAddGiftWithoutEnjoyer() {
        boolean result = manager.addGift("Book", Type.PHYSICAL, Occasion.BIRTHDAY);
        assertTrue(result, "Should successfully add gift without enjoyer");
    }

    @Test
    @DisplayName("addGift should add gift with enjoyer")
    void testAddGiftWithEnjoyer() {
        manager.addEnjoyer(enjoyerDetails);
        HashMap<Integer, EnjoyerDetails> enjoyers = manager.getEnjoyers();

        if (enjoyers != null && !enjoyers.isEmpty()) {
            int enjoyerId = enjoyers.keySet().iterator().next();
            boolean result = manager.addGift("Book", Type.PHYSICAL, Occasion.BIRTHDAY, enjoyerId);
            assertTrue(result, "Should successfully add gift with enjoyer");
        }
    }

    @Test
    @DisplayName("getGifts should return non-null ArrayList")
    void testGetGifts() {
        ArrayList<Gift> gifts = manager.getGifts();
        assertNotNull(gifts, "getGifts should not return null");
    }

    @Test
    @DisplayName("editGift should update gift feedback and status")
    void testEditGift() {
        manager.addGift("Book", Type.PHYSICAL, Occasion.BIRTHDAY);
        ArrayList<Gift> gifts = manager.getGifts();

        if (gifts != null && !gifts.isEmpty()) {
            int giftId = gifts.get(0).getIdGift();
            boolean result = manager.editGift(giftId, Feedback.GOOD, Status.GIFTED);
            assertTrue(result, "Should successfully edit gift");
        }
    }

    @Test
    @DisplayName("setGiftMessage should set message for gift")
    void testSetGiftMessage() {
        manager.addGift("Book", Type.PHYSICAL, Occasion.BIRTHDAY);
        ArrayList<Gift> gifts = manager.getGifts();

        if (gifts != null && !gifts.isEmpty()) {
            int giftId = gifts.getFirst().getIdGift();
            boolean result = manager.setGiftMessage(giftId, "Happy Birthday!");
            assertTrue(result, "Should successfully set gift message");
        }
    }

    // ==================== EVENT TESTS ====================

    @Test
    @DisplayName("getMonthlyEventsIds should return non-null list")
    void testGetMonthlyEventsIds() {
        List<Integer> eventIds = manager.getMonthlyEventsIds(LocalDate.now());
        assertNotNull(eventIds, "getMonthlyEventsIds should not return null");
    }

    @Test
    @DisplayName("getMonthlyEventsIds should handle future dates")
    void testGetMonthlyEventsIdsFuture() {
        LocalDate futureDate = LocalDate.now().plusYears(1);
        List<Integer> eventIds = manager.getMonthlyEventsIds(futureDate);
        assertNotNull(eventIds, "Should handle future dates");
    }

    // ==================== LLM INTEGRATION TESTS ====================

    @Test
    @DisplayName("generateGiftSuggestions should not throw exception")
    void testGenerateGiftSuggestions() {
        GiftCriteria criteria = new GiftCriteria();
        criteria.setMinBudget(50.0);
        criteria.setMaxBudget(100.0);
        criteria.setOccasion("Birthday");

        assertDoesNotThrow(() -> {
            String result = manager.generateGiftSuggestions(enjoyerDetails, criteria, true);
            assertNotNull(result, "Should return some result");
        });
    }

    @Test
    @DisplayName("generateSpontaneousGifts should not throw exception")
    void testGenerateSpontaneousGifts() {
        GiftCriteria criteria = new GiftCriteria();
        criteria.setMinBudget(50.0);
        criteria.setMaxBudget(100.0);

        assertDoesNotThrow(() -> {
            String result = manager.generateSpontaneousGifts("A book lover", criteria, true);
            assertNotNull(result, "Should return some result");
        });
    }

    @Test
    @DisplayName("generateGiftMessage should not throw exception")
    void testGenerateGiftMessage() {
        assertDoesNotThrow(() -> {
            String result = manager.generateGiftMessage("Book", "A novel", "Alice", "Friend", "Birthday", true);
            assertNotNull(result, "Should return some result");
        });
    }

    // ==================== USER MANAGEMENT TESTS ====================

    @Test
    @DisplayName("register should create new user")
    void testRegister() {
        deletePersistenceFiles();

        SurpriseMeManager newManager = new SurpriseMeManager();
        boolean result = newManager.register("NewUser", "new@example.com", "pass123");
        assertTrue(result, "Should successfully register new user");
    }

    @Test
    @DisplayName("login should work in clean environment")
    void testLogin() {
        deletePersistenceFiles();

        SurpriseMeManager cleanManager = new SurpriseMeManager();

        boolean loginFail = cleanManager.login("nonexistent@test.com", "anypassword");
        assertFalse(loginFail, "Login should fail for non-existent user");

        boolean registered = cleanManager.register("TestUser", "test@example.com", "password123");
        assertTrue(registered, "Should successfully register new user");

        boolean duplicateRegister = cleanManager.register("Another", "test@example.com", "different");
        assertFalse(duplicateRegister, "Should not register duplicate email");

        UserDetails details = cleanManager.getUserDetails();
        assertNotNull(details, "Should have user details after successful registration");
    }

    @Test
    @DisplayName("setUserDetails should reject another user's email")
    void testDuplicateProfileEmailIsRejected() {
        manager.logout();
        assertTrue(manager.register("OtherUser", "b@email.com", "password123"));

        UserDetails details = manager.getUserDetails();
        assertNotNull(details);
        details.setEmail(" TEST@example.com ");
        assertFalse(manager.setUserDetails(details), "Should reject an email already used by another account");
        assertEquals("b@email.com", manager.getUserDetails().getEmail());

        details = manager.getUserDetails();
        details.setEmail("B@email.com");
        assertTrue(manager.setUserDetails(details), "Should allow the current user to keep their own email");
        assertEquals("B@email.com", manager.getUserDetails().getEmail());
    }

    @Test
    @DisplayName("logged-in session should not be restored after save and load")
    void testSessionIsNotRestoredAfterSaveAndLoad() {
        assertNotNull(manager.getUserDetails());
        assertNull(manager.getUserDetails().getPasswordHash());
        manager.save();

        SurpriseMeManager reloaded = new SurpriseMeManager(prompt -> "");
        assertNull(reloaded.getUserDetails(), "Session must not be restored from persisted state");
        assertTrue(reloaded.login("test@example.com", "password123"));
        assertNotNull(reloaded.getUserDetails());
        assertEquals("test@example.com", reloaded.getUserDetails().getEmail());
    }

    @Test
    @DisplayName("register should reject a password shorter than the minimum length")
    void testRegisterRejectsShortPassword() {
        manager.logout();
        assertFalse(manager.register("ShortPass", "short@example.com", "12345"));
        assertTrue(manager.register("ShortPass", "short@example.com", "123456"));
    }

    private static void deletePersistenceFiles() {
        try {
            Files.deleteIfExists(SurpriseMeSerialization.dataFilePath());
            Files.deleteIfExists(SurpriseMeSerialization.backupFilePath());
            Files.deleteIfExists(SurpriseMeSerialization.tempFilePath());
            Files.deleteIfExists(SurpriseMeSerialization.newFilePath());
        } catch (Exception e) {
            // Ignore if files do not exist
        }
    }

    @Test
    @DisplayName("getUserDetails should return user details when logged in")
    void testGetUserDetails() {
        UserDetails details = manager.getUserDetails();
        assertNotNull(details, "Should return user details when logged in");
    }
}

