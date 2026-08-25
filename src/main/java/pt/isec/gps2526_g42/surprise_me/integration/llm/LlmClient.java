package pt.isec.gps2526_g42.surprise_me.integration.llm;

@FunctionalInterface
public interface LlmClient {
    String generateGiftSuggestions(String prompt) throws Exception;
}
