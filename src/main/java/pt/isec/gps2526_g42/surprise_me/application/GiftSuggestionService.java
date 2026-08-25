package pt.isec.gps2526_g42.surprise_me.application;

import pt.isec.gps2526_g42.surprise_me.integration.llm.LlmClient;
import pt.isec.gps2526_g42.surprise_me.integration.llm.PromptBuilder;
import pt.isec.gps2526_g42.surprise_me.model.data.EnjoyerDetails;

import java.util.Objects;

public class GiftSuggestionService {
    private static final String CONSENT_REQUIRED =
            "Consent is required before recipient data is sent to the external LLM provider.";
    private static final String SERVICE_UNAVAILABLE =
            "The suggestion service is currently unavailable.";

    private final LlmClient llmClient;
    private final PromptBuilder promptBuilder;

    public GiftSuggestionService(LlmClient llmClient) {
        this.llmClient = Objects.requireNonNull(llmClient);
        this.promptBuilder = new PromptBuilder();
    }

    public String generateGiftSuggestions(EnjoyerDetails enjoyerDetails, GiftCriteria giftCriteria,
                                          boolean consentGranted) {
        return invokeLlm(consentGranted, () -> promptBuilder.buildGiftSuggestionPrompt(enjoyerDetails, giftCriteria));
    }

    public String generateSpontaneousGifts(String enjoyerDescription, GiftCriteria giftCriteria, String userCity,
                                           String userCountry, boolean consentGranted) {
        return invokeLlm(consentGranted,
                () -> promptBuilder.buildSpontaneousPrompt(enjoyerDescription, giftCriteria, userCity, userCountry));
    }

    public String generateGiftMessage(String giftTitle, String giftDescription, String recipientName,
                                      String relationship, String occasion, boolean consentGranted) {
        return invokeLlm(consentGranted, () -> promptBuilder.buildGiftMessagePrompt(
                giftTitle, giftDescription, recipientName, relationship, occasion));
    }

    public String generateSpontaneousGiftMessage(String giftTitle, String giftDescription, String occasion,
                                                 boolean consentGranted) {
        return invokeLlm(consentGranted, () -> promptBuilder.buildSpontaneousGiftMessagePrompt(
                giftTitle, giftDescription, occasion));
    }

    private String invokeLlm(boolean consentGranted, PromptSupplier promptSupplier) {
        if (!consentGranted) {
            return CONSENT_REQUIRED;
        }
        try {
            return llmClient.generateGiftSuggestions(promptSupplier.get());
        } catch (Exception e) {
            return SERVICE_UNAVAILABLE;
        }
    }

    @FunctionalInterface
    private interface PromptSupplier {
        String get() throws Exception;
    }
}
