package pt.isec.gps2526_g42.surprise_me.model.data;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.io.InvalidClassException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Hashtable;

import static org.junit.jupiter.api.Assertions.*;

class SurpriseMeSerializationTest {

    @BeforeEach
    void setUp() throws IOException {
        deletePersistenceFiles();
        Files.createDirectories(SurpriseMeSerialization.dataDirectory());
    }

    @AfterEach
    void tearDown() {
        deletePersistenceFiles();
    }

    @Test
    void sessionIsNotRestoredAfterSaveAndLoad() {
        SurpriseMe stored = new SurpriseMe();
        assertTrue(stored.register("Alice", "alice@example.com", "password123"));
        assertNotNull(stored.getUserDetails());

        SurpriseMeSerialization.save(stored);
        SurpriseMe loaded = SurpriseMeSerialization.load();

        assertNull(loaded.getUserDetails());
        assertTrue(loaded.login("alice@example.com", "password123"));
        assertEquals("Alice", loaded.getUserDetails().getName());
    }

    @Test
    void loadRecoversFromBackupWhenPrimaryIsCorrupt() throws IOException {
        SurpriseMe stored = new SurpriseMe();
        assertTrue(stored.register("Alice", "alice@example.com", "password123"));
        SurpriseMeSerialization.save(stored);

        stored.addEnjoyer(validEnjoyer("AliceFriend"));
        SurpriseMeSerialization.save(stored);
        assertTrue(Files.exists(SurpriseMeSerialization.backupFilePath()));

        Path primary = SurpriseMeSerialization.dataFilePath();
        assertDoesNotThrow(() -> Files.writeString(primary, "not-a-serialized-surprise-me"));
        byte[] corruptPrimary = Files.readAllBytes(primary);
        byte[] goodBackup = Files.readAllBytes(SurpriseMeSerialization.backupFilePath());

        SurpriseMe recovered = SurpriseMeSerialization.load();
        assertTrue(recovered.login("alice@example.com", "password123"));
        assertEquals("Alice", recovered.getUserDetails().getName());
        assertArrayEquals(corruptPrimary, Files.readAllBytes(primary));
        assertArrayEquals(goodBackup, Files.readAllBytes(SurpriseMeSerialization.backupFilePath()));
    }

    @Test
    void saveAfterBackupRecoveryKeepsUsableBackup() throws IOException {
        SurpriseMe stored = new SurpriseMe();
        assertTrue(stored.register("Alice", "alice@example.com", "password123"));
        SurpriseMeSerialization.save(stored);
        stored.addEnjoyer(validEnjoyer("AliceFriend"));
        SurpriseMeSerialization.save(stored);

        Path primary = SurpriseMeSerialization.dataFilePath();
        Path backup = SurpriseMeSerialization.backupFilePath();
        byte[] goodBackup = Files.readAllBytes(backup);
        Files.writeString(primary, "not-a-serialized-surprise-me");
        byte[] corruptPrimary = Files.readAllBytes(primary);

        SurpriseMe recovered = SurpriseMeSerialization.load();
        assertTrue(recovered.login("alice@example.com", "password123"));
        recovered.addEnjoyer(validEnjoyer("RecoveredFriend"));
        SurpriseMeSerialization.save(recovered);

        assertFalse(Arrays.equals(corruptPrimary, Files.readAllBytes(backup)));
        assertArrayEquals(goodBackup, Files.readAllBytes(backup));

        Files.writeString(primary, "corrupt-again");
        SurpriseMe fromBackup = SurpriseMeSerialization.load();
        assertTrue(fromBackup.login("alice@example.com", "password123"));
        assertEquals("Alice", fromBackup.getUserDetails().getName());
    }

    @Test
    void loadRecoversFromLeftoverTmpWhenPrimaryIsMissing() throws IOException {
        SurpriseMe stored = new SurpriseMe();
        assertTrue(stored.register("Alice", "alice@example.com", "password123"));
        SurpriseMeSerialization.save(stored);

        Path primary = SurpriseMeSerialization.dataFilePath();
        Files.copy(primary, SurpriseMeSerialization.tempFilePath());
        try {
            Files.deleteIfExists(primary);
        } catch (IOException ex) {
            Files.writeString(primary, "unreadable-primary");
        }

        SurpriseMe recovered = SurpriseMeSerialization.load();
        assertTrue(recovered.login("alice@example.com", "password123"));
        assertEquals("Alice", recovered.getUserDetails().getName());
        assertTrue(Files.exists(primary));

        deleteQuietly(SurpriseMeSerialization.tempFilePath());
        SurpriseMe fromPrimary = SurpriseMeSerialization.load();
        assertTrue(fromPrimary.login("alice@example.com", "password123"));
        assertEquals("Alice", fromPrimary.getUserDetails().getName());
    }

    @Test
    void loadRecoversFromLeftoverNewWhenPrimaryIsMissing() throws IOException {
        SurpriseMe stored = new SurpriseMe();
        assertTrue(stored.register("Alice", "alice@example.com", "password123"));
        stored.addEnjoyer(validEnjoyer("AliceFriend"));
        SurpriseMeSerialization.save(stored);

        Path primary = SurpriseMeSerialization.dataFilePath();
        Path staging = SurpriseMeSerialization.newFilePath();
        Files.copy(primary, staging);
        try {
            Files.deleteIfExists(primary);
        } catch (IOException ex) {
            Files.writeString(primary, "unreadable-primary");
        }

        SurpriseMe recovered = SurpriseMeSerialization.load();
        assertTrue(recovered.login("alice@example.com", "password123"));
        assertEquals("Alice", recovered.getUserDetails().getName());
        assertTrue(Files.exists(primary));

        deleteQuietly(staging);
        recovered.addEnjoyer(validEnjoyer("RecoveredFriend"));
        SurpriseMeSerialization.save(recovered);

        assertTrue(SurpriseMeSerialization.load().login("alice@example.com", "password123"));

        Files.writeString(primary, "corrupt-after-save");
        SurpriseMe afterSave = SurpriseMeSerialization.load();
        assertTrue(afterSave.login("alice@example.com", "password123"));
        assertEquals("Alice", afterSave.getUserDetails().getName());
    }

    @Test
    void deserializationFilterRejectsUnexpectedClasses() throws Exception {
        Path unexpectedFile = SurpriseMeSerialization.dataDirectory().resolve("unexpected.ser");
        try (ObjectOutputStream oos = new ObjectOutputStream(Files.newOutputStream(unexpectedFile))) {
            oos.writeObject(new File("secrets.txt"));
        }
        assertFilterRejects(unexpectedFile);

        try (ObjectOutputStream oos = new ObjectOutputStream(Files.newOutputStream(unexpectedFile))) {
            oos.writeObject(new Hashtable<String, String>());
        }
        assertFilterRejects(unexpectedFile);
    }

    private static void assertFilterRejects(Path file) throws Exception {
        try (ObjectInputStream ois = new ObjectInputStream(Files.newInputStream(file))) {
            ois.setObjectInputFilter(SurpriseMeSerialization.objectInputFilter());
            assertThrows(InvalidClassException.class, ois::readObject);
        } finally {
            Files.deleteIfExists(file);
        }
    }

    @Test
    void loadRejectsUnexpectedRootClassAndDoesNotUseCorruptPrimary() throws Exception {
        SurpriseMe stored = new SurpriseMe();
        assertTrue(stored.register("Alice", "alice@example.com", "password123"));
        SurpriseMeSerialization.save(stored);

        Path primary = SurpriseMeSerialization.dataFilePath();
        Path backup = SurpriseMeSerialization.backupFilePath();
        Files.copy(primary, backup);
        try (ObjectOutputStream oos = new ObjectOutputStream(Files.newOutputStream(primary))) {
            oos.writeObject(new File("secrets.txt"));
        }

        SurpriseMe recovered = SurpriseMeSerialization.load();
        assertTrue(recovered.login("alice@example.com", "password123"));
    }

    @Test
    void avatarPathTraversalIsRejected() {
        Path dataDirectory = SurpriseMeSerialization.dataDirectory().toAbsolutePath().normalize();

        assertNull(SurpriseMeSerialization.resolveAvatarPath("../../some-file"));
        assertNull(SurpriseMeSerialization.resolveAvatarPath("../secrets.txt"));
        assertNull(SurpriseMeSerialization.resolveAvatarPath("avatars/../../outside.png"));
        assertNull(SurpriseMeSerialization.resolveAvatarPath(dataDirectory.resolve("avatars").resolve("ok.png").toString()));
        assertNull(SurpriseMeSerialization.resolveAvatarPath(null));
        assertNull(SurpriseMeSerialization.resolveAvatarPath("   "));
        assertNull(SurpriseMeSerialization.resolveAvatarPath("avatars/foo\0.png"));

        Path allowed = SurpriseMeSerialization.resolveAvatarPath("avatars/ok.png");
        assertNotNull(allowed);
        assertTrue(allowed.startsWith(dataDirectory));
        assertEquals(dataDirectory.resolve("avatars").resolve("ok.png").normalize(), allowed);
    }

    private static EnjoyerDetails validEnjoyer(String name) {
        EnjoyerDetails details = new EnjoyerDetails();
        details.setName(name);
        return details;
    }

    private static void deletePersistenceFiles() {
        deleteQuietly(SurpriseMeSerialization.dataFilePath());
        deleteQuietly(SurpriseMeSerialization.backupFilePath());
        deleteQuietly(SurpriseMeSerialization.tempFilePath());
        deleteQuietly(SurpriseMeSerialization.newFilePath());
    }

    private static void deleteQuietly(Path path) {
        try {
            Files.deleteIfExists(path);
        } catch (IOException ignored) {
            path.toFile().deleteOnExit();
        }
    }
}
