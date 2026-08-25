package pt.isec.gps2526_g42.surprise_me.application;

import org.junit.jupiter.api.Test;
import pt.isec.gps2526_g42.surprise_me.config.AppConfig;
import pt.isec.gps2526_g42.surprise_me.integration.llm.GroqLlmClient;
import pt.isec.gps2526_g42.surprise_me.integration.llm.LlmClient;
import pt.isec.gps2526_g42.surprise_me.model.data.EnjoyerDetails;

import java.lang.reflect.Constructor;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;

class GiftSuggestionServiceTest {
    private static final String UNAVAILABLE = "The suggestion service is currently unavailable.";

    @Test
    void managerConstructsWithoutAnApiKey() {
        assertDoesNotThrow(() -> { new SurpriseMeManager(new GroqLlmClient()); });
        assertDoesNotThrow(() -> { new SurpriseMeManager(); });
    }

    @Test
    void missingApiKeyReturnsUnavailableMessageWithoutLeakingProviderDetails() throws Exception {
        GroqLlmClient client = new GroqLlmClient(appConfigWithoutSecrets());
        IllegalStateException thrown = assertThrows(IllegalStateException.class,
                () -> client.generateGiftSuggestions("prompt"));
        assertFalse(thrown.getMessage().contains("{"));
        assertFalse(thrown.getMessage().contains("HTTP"));

        AtomicBoolean invoked = new AtomicBoolean(false);
        LlmClient missingKey = prompt -> {
            invoked.set(true);
            throw new IllegalStateException(
                    "Configure SURPRISEME_LLM_API_KEY before using gift suggestions. body={raw-provider-secret}");
        };

        SurpriseMeManager manager = new SurpriseMeManager(missingKey);
        String result = manager.generateGiftSuggestions(new EnjoyerDetails(), new GiftCriteria(), true);

        assertTrue(invoked.get());
        assertEquals(UNAVAILABLE, result);
        assertFalse(result.contains("raw-provider-secret"));
        assertFalse(result.contains("SURPRISEME_LLM_API_KEY"));

        GiftSuggestionService service = new GiftSuggestionService(client);
        assertEquals(UNAVAILABLE, service.generateGiftSuggestions(new EnjoyerDetails(), new GiftCriteria(), true));
    }

    @Test
    void providerHttpFailuresReturnUnavailableMessageWithoutLeakingBodies() {
        GiftSuggestionService service = new GiftSuggestionService(prompt -> {
            throw new RuntimeException("The LLM provider returned HTTP 401 {\"error\":\"secret-api-leak\"}");
        });

        String result = service.generateSpontaneousGifts(
                "private recipient details", new GiftCriteria(), null, null, true);

        assertEquals(UNAVAILABLE, result);
        assertFalse(result.contains("secret-api-leak"));
        assertFalse(result.contains("HTTP 401"));
    }

    @SuppressWarnings("unchecked")
    private static AppConfig appConfigWithoutSecrets() throws Exception {
        Constructor<AppConfig> constructor = AppConfig.class.getDeclaredConstructor(Function.class, Map.class);
        constructor.setAccessible(true);
        return constructor.newInstance((Function<String, String>) name -> null, Map.of());
    }
}
