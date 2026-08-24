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
    void loadRecoversFromBackupWhenPrimaryIsCorrupt() {
        SurpriseMe stored = new SurpriseMe();
        assertTrue(stored.register("Alice", "alice@example.com", "password123"));
        SurpriseMeSerialization.save(stored);

        stored.addEnjoyer(validEnjoyer("AliceFriend"));
        SurpriseMeSerialization.save(stored);
        assertTrue(Files.exists(SurpriseMeSerialization.backupFilePath()));

        Path primary = SurpriseMeSerialization.dataFilePath();
        assertDoesNotThrow(() -> Files.writeString(primary, "not-a-serialized-surprise-me"));

        SurpriseMe recovered = SurpriseMeSerialization.load();
        assertTrue(recovered.login("alice@example.com", "password123"));
        assertEquals("Alice", recovered.getUserDetails().getName());
    }

    @Test
    void deserializationFilterRejectsUnexpectedClasses() throws Exception {
        Path unexpectedFile = SurpriseMeSerialization.dataDirectory().resolve("unexpected.ser");
        try (ObjectOutputStream oos = new ObjectOutputStream(Files.newOutputStream(unexpectedFile))) {
            oos.writeObject(new File("secrets.txt"));
        }

        try (ObjectInputStream ois = new ObjectInputStream(Files.newInputStream(unexpectedFile))) {
            ois.setObjectInputFilter(SurpriseMeSerialization.objectInputFilter());
            assertThrows(InvalidClassException.class, ois::readObject);
        } finally {
            Files.deleteIfExists(unexpectedFile);
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
    }

    private static void deleteQuietly(Path path) {
        try {
            Files.deleteIfExists(path);
        } catch (IOException ignored) {
            path.toFile().deleteOnExit();
        }
    }
}
