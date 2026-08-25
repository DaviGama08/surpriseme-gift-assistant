package pt.isec.gps2526_g42.surprise_me.model.data;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pt.isec.gps2526_g42.surprise_me.application.SurpriseMeManager;
import pt.isec.gps2526_g42.surprise_me.persistence.SurpriseMeSerialization;
import pt.isec.gps2526_g42.surprise_me.security.PasswordHasher;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

class LegacyPasswordMigrationTest {
    private static final String EMAIL = "legacy@example.com";
    private static final String PASSWORD = "legacy-password";

    @BeforeEach
    void setUp() {
        deletePersistenceFiles();
    }

    @AfterEach
    void tearDown() {
        deletePersistenceFiles();
    }

    @Test
    void loginMigratesSha3HashToPbkdf2AndSubsequentLoginStillWorks() throws Exception {
        seedAccountWithStoredPassword(sha3Hex(PASSWORD));
        assertTrue(storedPasswordHash().matches("(?i)[0-9a-f]{64}"));

        assertLoginPersistsMigratedHash(PASSWORD);

        SurpriseMeManager reloaded = manager();
        assertTrue(reloaded.login(EMAIL, PASSWORD));
        assertEquals("LegacyUser", reloaded.getUserDetails().getName());
    }

    @Test
    void loginMigratesPlaintextPasswordToPbkdf2AndSubsequentLoginStillWorks() throws Exception {
        seedAccountWithStoredPassword(PASSWORD);
        assertEquals(PASSWORD, storedPasswordHash());

        assertLoginPersistsMigratedHash(PASSWORD);
        assertNotEquals(PASSWORD, storedPasswordHash());

        SurpriseMeManager reloaded = manager();
        assertTrue(reloaded.login(EMAIL, PASSWORD));
        assertEquals("LegacyUser", reloaded.getUserDetails().getName());
    }

    private static void seedAccountWithStoredPassword(String storedValue) throws Exception {
        SurpriseMeManager manager = manager();
        assertTrue(manager.register("LegacyUser", EMAIL, PASSWORD));
        manager.logout();

        SurpriseMe data = SurpriseMeSerialization.load();
        userOf(data).setPasswordHash(storedValue);
        SurpriseMeSerialization.save(data);
    }

    private static void assertLoginPersistsMigratedHash(String password) throws Exception {
        SurpriseMeManager manager = manager();
        assertTrue(manager.login(EMAIL, password));
        assertNotNull(manager.getUserDetails());

        String migrated = storedPasswordHash();
        assertTrue(migrated.startsWith("pbkdf2-sha256$210000$"));
        assertFalse(PasswordHasher.needsRehash(migrated));
        assertTrue(PasswordHasher.verify(password, migrated));
    }

    private static SurpriseMeManager manager() {
        return new SurpriseMeManager(prompt -> "unused");
    }

    private static String storedPasswordHash() throws Exception {
        return userOf(SurpriseMeSerialization.load()).getPasswordHash();
    }

    @SuppressWarnings("unchecked")
    private static User userOf(SurpriseMe data) throws Exception {
        Field usersField = SurpriseMe.class.getDeclaredField("users");
        usersField.setAccessible(true);
        HashMap<Integer, User> users = (HashMap<Integer, User>) usersField.get(data);
        assertFalse(users.isEmpty());
        return users.values().iterator().next();
    }

    private static String sha3Hex(String value) throws Exception {
        byte[] digest = MessageDigest.getInstance("SHA3-256")
                .digest(value.getBytes(StandardCharsets.UTF_8));
        StringBuilder result = new StringBuilder(digest.length * 2);
        for (byte item : digest) {
            result.append(String.format("%02x", item));
        }
        return result.toString();
    }

    private static void deletePersistenceFiles() {
        try {
            Files.deleteIfExists(SurpriseMeSerialization.dataFilePath());
            Files.deleteIfExists(SurpriseMeSerialization.backupFilePath());
            Files.deleteIfExists(SurpriseMeSerialization.tempFilePath());
            Files.deleteIfExists(SurpriseMeSerialization.newFilePath());
        } catch (IOException ignored) {
        }
    }
}
