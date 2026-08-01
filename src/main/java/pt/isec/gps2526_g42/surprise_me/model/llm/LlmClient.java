package pt.isec.gps2526_g42.surprise_me.model.llm;

@FunctionalInterface
public interface LlmClient {
    String generateGiftSuggestions(String prompt) throws Exception;
}
