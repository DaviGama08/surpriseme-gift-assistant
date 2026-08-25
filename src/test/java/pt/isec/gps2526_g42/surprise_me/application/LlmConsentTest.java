package pt.isec.gps2526_g42.surprise_me.application;

import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;

class LlmConsentTest {
    @Test
    void recipientDataIsNotSentWithoutExplicitConsent() {
        AtomicBoolean invoked = new AtomicBoolean(false);
        SurpriseMeManager manager = new SurpriseMeManager(prompt -> {
            invoked.set(true);
            return "unexpected";
        });

        String result = manager.generateSpontaneousGifts("private recipient details", new GiftCriteria(), false);

        assertFalse(invoked.get());
        assertTrue(result.startsWith("Consent is required"));
    }
}
