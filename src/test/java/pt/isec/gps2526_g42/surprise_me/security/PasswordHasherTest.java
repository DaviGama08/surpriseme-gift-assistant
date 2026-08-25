package pt.isec.gps2526_g42.surprise_me.security;

import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

import static org.junit.jupiter.api.Assertions.*;

class PasswordHasherTest {
    @Test
    void hashUsesUniqueSaltsAndVerifiesTheOriginalPassword() {
        String first = PasswordHasher.hash("correct horse battery staple");
        String second = PasswordHasher.hash("correct horse battery staple");

        assertNotEquals(first, second);
        assertTrue(first.startsWith("pbkdf2-sha256$210000$"));
        assertTrue(PasswordHasher.verify("correct horse battery staple", first));
        assertFalse(PasswordHasher.verify("wrong password", first));
    }

    @Test
    void verifyRejectsMalformedHashes() {
        assertFalse(PasswordHasher.verify("password", "pbkdf2-sha256$broken"));
        assertFalse(PasswordHasher.verify(null, "value"));
        assertFalse(PasswordHasher.verify("password", null));
    }

    @Test
    void legacySha3HashesCanBeVerifiedForAutomaticMigration() throws Exception {
        byte[] digest = MessageDigest.getInstance("SHA3-256")
                .digest("legacy-password".getBytes(StandardCharsets.UTF_8));
        StringBuilder legacyHash = new StringBuilder();
        for (byte item : digest) {
            legacyHash.append(String.format("%02x", item));
        }

        assertTrue(PasswordHasher.verify("legacy-password", legacyHash.toString()));
        assertTrue(PasswordHasher.needsRehash(legacyHash.toString()));
    }

    @Test
    void plaintextLegacyPasswordsCanBeVerifiedForAutomaticMigration() {
        assertTrue(PasswordHasher.verify("legacy-plain", "legacy-plain"));
        assertTrue(PasswordHasher.needsRehash("legacy-plain"));
        assertFalse(PasswordHasher.verify("wrong-password", "legacy-plain"));
    }
}
