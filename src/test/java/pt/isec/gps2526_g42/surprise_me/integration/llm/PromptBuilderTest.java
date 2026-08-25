package pt.isec.gps2526_g42.surprise_me.integration.llm;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pt.isec.gps2526_g42.surprise_me.application.GiftCriteria;
import pt.isec.gps2526_g42.surprise_me.model.data.EnjoyerDetails;

import java.time.LocalDate;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("PromptBuilder Tests")
class PromptBuilderTest {

    private PromptBuilder promptBuilder;
    private EnjoyerDetails enjoyerDetails;
    private GiftCriteria giftCriteria;

    @BeforeEach
    void setUp() {
        promptBuilder = new PromptBuilder();

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

        enjoyerDetails.setNotes("Prefers thoughtful gifts");
        enjoyerDetails.setRelationship("Friend");

        giftCriteria = new GiftCriteria();
        giftCriteria.setMinBudget(50.0);
        giftCriteria.setMaxBudget(100.0);
        giftCriteria.setOccasion("Birthday");
    }

    @Test
    @DisplayName("Test buildGiftSuggestionPrompt with valid parameters")
    void testBuildGiftSuggestionPromptValid() {
        String prompt = promptBuilder.buildGiftSuggestionPrompt(enjoyerDetails, giftCriteria);

        assertNotNull(prompt);
        assertFalse(prompt.isEmpty());
        assertTrue(prompt.contains("Alice"));
    }

    @Test
    @DisplayName("Test buildGiftSuggestionPrompt with null enjoyer")
    void testBuildGiftSuggestionPromptNullEnjoyer() {
        String prompt = promptBuilder.buildGiftSuggestionPrompt(null, giftCriteria);

        assertNotNull(prompt);
        assertFalse(prompt.isEmpty());
    }

    @Test
    @DisplayName("Test buildGiftSuggestionPrompt with null criteria")
    void testBuildGiftSuggestionPromptNullCriteria() {
        String prompt = promptBuilder.buildGiftSuggestionPrompt(enjoyerDetails, null);

        assertNotNull(prompt);
        assertFalse(prompt.isEmpty());
    }

    @Test
    @DisplayName("Test buildSpontaneousPrompt with description only")
    void testBuildSpontaneousPromptBasic() {
        String prompt = promptBuilder.buildSpontaneousPrompt("A book lover", giftCriteria, null, null);

        assertNotNull(prompt);
        assertFalse(prompt.isEmpty());
    }

    @Test
    @DisplayName("Test buildSpontaneousPrompt with location")
    void testBuildSpontaneousPromptWithLocation() {
        String prompt = promptBuilder.buildSpontaneousPrompt("A book lover", giftCriteria, "Lisbon", "Portugal");

        assertNotNull(prompt);
        assertFalse(prompt.isEmpty());
    }

    @Test
    @DisplayName("Test buildGiftMessagePrompt with all parameters")
    void testBuildGiftMessagePromptComplete() {
        String prompt = promptBuilder.buildGiftMessagePrompt(
            "Book",
            "A great novel about adventure",
            "Alice",
            "Friend",
            "Birthday"
        );

        assertNotNull(prompt);
        assertFalse(prompt.isEmpty());
    }

    @Test
    @DisplayName("Test buildSpontaneousGiftMessagePrompt with valid parameters")
    void testBuildSpontaneousGiftMessagePromptValid() {
        String prompt = promptBuilder.buildSpontaneousGiftMessagePrompt(
            "Book",
            "A great novel",
            "Just because"
        );

        assertNotNull(prompt);
        assertFalse(prompt.isEmpty());
    }
}

