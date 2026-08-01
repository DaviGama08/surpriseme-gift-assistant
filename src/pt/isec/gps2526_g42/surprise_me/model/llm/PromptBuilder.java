package pt.isec.gps2526_g42.surprise_me.model.llm;

import pt.isec.gps2526_g42.surprise_me.model.data.EnjoyerDetails;
import java.time.LocalDate;
import java.time.Period;

public class PromptBuilder {
    private static final int LONG_MIN_CHARS = 600;
    private static final int LONG_MAX_CHARS = 750;

    public String buildGiftSuggestionPrompt(EnjoyerDetails enjoyer, GiftCriteria criteria) {
        StringBuilder prompt = new StringBuilder();

        prompt.append("You are an expert gift advisor specializing in personalized recommendations. Generate 4 personalized gift suggestions based on the following information. ");
        prompt.append("RESPOND ONLY with the numbered list - NO introduction, explanations, or extra text.\n\n");

        if (enjoyer != null) {
            prompt.append("RECIPIENT PROFILE:\n");
            prompt.append("Person: ").append(enjoyer.getName());
            prompt.append(" (my ").append(enjoyer.getRelationship()).append(")\n");

            if (enjoyer.getBirthDate() != null) {
                int age = calculateAge(enjoyer.getBirthDate());
                prompt.append("- Age: ").append(age).append(" years old\n");
            }

            StringBuilder location = new StringBuilder();
            if (enjoyer.getCity() != null && !enjoyer.getCity().trim().isEmpty()) {
                location.append(enjoyer.getCity());
            }
            if (enjoyer.getCountry() != null && !enjoyer.getCountry().trim().isEmpty()) {
                if (!location.isEmpty()) {
                    location.append(", ");
                }
                location.append(enjoyer.getCountry());
            }
            if (!location.isEmpty()) {
                prompt.append("- Location: ").append(location).append("\n");
            }

            if (enjoyer.getLikes() != null && !enjoyer.getLikes().isEmpty()) {
                prompt.append("- Likes: ").append(String.join(", ", enjoyer.getLikes())).append("\n");
            }
            if (enjoyer.getDislikes() != null && !enjoyer.getDislikes().isEmpty()) {
                prompt.append("- Dislikes: ").append(String.join(", ", enjoyer.getDislikes())).append("\n");
            }
            if (enjoyer.getNotes() != null && !enjoyer.getNotes().trim().isEmpty()) {
                prompt.append("- Additional notes: ").append(enjoyer.getNotes()).append("\n");
            }
        }

        if (criteria != null) {
            prompt.append("\nREQUIREMENTS:\n");
            if (criteria.getMinBudget() > 0 || criteria.getMaxBudget() > 0) {
                prompt.append("Budget: €").append(criteria.getMinBudget())
                        .append(" to €").append(criteria.getMaxBudget()).append("\n");
            }
            if (criteria.getOccasion() != null) {
                prompt.append("- Occasion: ").append(criteria.getOccasion()).append("\n");
            }
            if (criteria.getGiftType() != null) {
                prompt.append("- Gift type: ").append(criteria.getGiftType()).append("\n");
            }
            if (criteria.getThingsToAvoid() != null && !criteria.getThingsToAvoid().trim().isEmpty()) {
                prompt.append("- Avoid: ").append(criteria.getThingsToAvoid()).append("\n");
            }
            if (criteria.isUseful()) {
                prompt.append("Must be practical and useful\n");
            }
            if (criteria.isSustainable()) {
                prompt.append("Must consider sustainability\n");
            }
            if (criteria.getAdditionalIdeas() != null && !criteria.getAdditionalIdeas().trim().isEmpty()) {
                prompt.append("- Additional ideas: ").append(criteria.getAdditionalIdeas()).append("\n");
            }
        }

        prompt.append("\nIMPORTANT: Provide ONLY the 4 gift suggestions in the exact format below. ");
        prompt.append("Do NOT include any introduction, explanations, or extra text.\n\n");

        prompt.append("Required format (one per line, no line breaks inside each item):\n");
        prompt.append("1. Title (MAX 3 WORDS) - Short: concise summary | Long: detailed description (≈ €XX)\n");
        prompt.append("2. Title (MAX 3 WORDS) - Short: concise summary | Long: detailed description (≈ €XX)\n");
        prompt.append("3. Title (MAX 3 WORDS) - Short: concise summary | Long: detailed description (≈ €XX)\n");
        prompt.append("4. Title (MAX 3 WORDS) - Short: concise summary | Long: detailed description (≈ €XX)\n\n");

        prompt.append("CRITICAL RULES:\n");
        prompt.append("- Title MUST be maximum 3 words\n");
        prompt.append("- Short description: maximum 240 characters\n");
        prompt.append("- Long description: between ").append(LONG_MIN_CHARS).append(" and ").append(LONG_MAX_CHARS).append(" characters including price\n");
        prompt.append("- Keep descriptions concise and impactful\n");
        prompt.append("- Do not insert extra line breaks inside a single item\n");

        String targetLocation = "Portugal";
        if (enjoyer != null) {
            StringBuilder locationBuilder = new StringBuilder();
            if (enjoyer.getCity() != null && !enjoyer.getCity().trim().isEmpty()) {
                locationBuilder.append(enjoyer.getCity());
            }
            if (enjoyer.getCountry() != null && !enjoyer.getCountry().trim().isEmpty()) {
                if (!locationBuilder.isEmpty()) {
                    locationBuilder.append(", ");
                }
                locationBuilder.append(enjoyer.getCountry());
            }
            if (!locationBuilder.isEmpty()) {
                targetLocation = locationBuilder.toString();
            }
        }

        prompt.append("Each suggestion must be available in ").append(targetLocation).append(", fit the criteria above and be in english.");

        return prompt.toString();
    }

    public String buildSpontaneousPrompt(String enjoyerDescription, GiftCriteria criteria) {
        return buildSpontaneousPrompt(enjoyerDescription, criteria, null, null);
    }

    public String buildSpontaneousPrompt(String enjoyerDescription, GiftCriteria criteria, String userCity, String userCountry) {
        StringBuilder prompt = new StringBuilder();

        prompt.append("You are an expert gift advisor specializing in personalized recommendations. Generate 4 creative gift suggestions based on the following information. ");
        prompt.append("RESPOND ONLY with the numbered list - NO introduction, explanations, or extra text.\n\n");

        prompt.append("TARGET PERSON:\n");
        prompt.append(enjoyerDescription).append("\n\n");

        if (criteria != null) {
            prompt.append("REQUIREMENTS:\n");
            if (criteria.getMinBudget() > 0 || criteria.getMaxBudget() > 0) {
                prompt.append("- Budget: €").append(criteria.getMinBudget())
                        .append(" to €").append(criteria.getMaxBudget()).append("\n");
            }
            if (criteria.getOccasion() != null) {
                prompt.append("- Occasion: ").append(criteria.getOccasion()).append("\n");
            }
            if (criteria.getGiftType() != null) {
                prompt.append("- Gift type: ").append(criteria.getGiftType()).append("\n");
            }
            if (criteria.getThingsToAvoid() != null && !criteria.getThingsToAvoid().trim().isEmpty()) {
                prompt.append("- Things to avoid: ").append(criteria.getThingsToAvoid()).append("\n");
            }
            if (criteria.isUseful()) {
                prompt.append("- Must be practical and useful\n");
            }
            if (criteria.isSustainable()) {
                prompt.append("- Must consider sustainability\n");
            }
            if (criteria.getAdditionalIdeas() != null && !criteria.getAdditionalIdeas().trim().isEmpty()) {
                prompt.append("- Additional ideas: ").append(criteria.getAdditionalIdeas()).append("\n");
            }
        }

        prompt.append("\nIMPORTANT: Provide ONLY the 4 gift suggestions in english, in the exact format below. ");
        prompt.append("Do NOT include any introduction, explanations, or extra text.\n\n");

        prompt.append("Required format (one per line, no line breaks inside each item):\n");
        prompt.append("1. Short Title (MAX 3 WORDS) - Short: concise summary | Long: detailed description (≈ €XX)\n");
        prompt.append("2. Short Title (MAX 3 WORDS) - Short: concise summary | Long: detailed description (≈ €XX)\n");
        prompt.append("3. Short Title (MAX 3 WORDS) - Short: concise summary | Long: detailed description (≈ €XX)\n");
        prompt.append("4. Short Title (MAX 3 WORDS) - Short: concise summary | Long: detailed description (≈ €XX)\n\n");

        prompt.append("CRITICAL RULES:\n");
        prompt.append("- Title MUST be maximum 3 words\n");
        prompt.append("- Short description: maximum 240 characters\n");
        prompt.append("- Long description: between ").append(LONG_MIN_CHARS).append(" and ").append(LONG_MAX_CHARS).append(" characters including price\n");
        prompt.append("- Keep descriptions concise and impactful\n");
        prompt.append("- Do not insert extra line breaks inside a single item\n");

        StringBuilder userLocation = new StringBuilder();
        userLocation.append(userCity != null && !userCity.trim().isEmpty() ? userCity.trim() : "Coimbra");
        userLocation.append(", ");
        userLocation.append(userCountry != null && !userCountry.trim().isEmpty() ? userCountry.trim() : "Portugal");

        prompt.append("\nEach suggestion must be available in ").append(userLocation).append(" and fit the criteria above.");

        return prompt.toString();
    }

    public String buildGiftMessagePrompt(String giftTitle, String giftDescription, String recipientName,
                                       String relationship, String occasion) {
        StringBuilder prompt = new StringBuilder();

        prompt.append("You are an expert at writing personalized gift messages. ");
        prompt.append("Create a short, heartfelt gift message for the following gift. ");
        prompt.append("RESPOND ONLY with the message itself - NO quotes, introduction, or explanations.\n\n");

        prompt.append("GIFT DETAILS:\n");
        prompt.append("- Gift: ").append(giftTitle != null ? giftTitle : "Special gift").append("\n");
        if (giftDescription != null && !giftDescription.trim().isEmpty()) {
            prompt.append("- Description: ").append(giftDescription).append("\n");
        }

        prompt.append("\nRECIPIENT:\n");
        if (recipientName != null && !recipientName.trim().isEmpty()) {
            prompt.append("- Name: ").append(recipientName).append("\n");
        }
        if (relationship != null && !relationship.trim().isEmpty()) {
            prompt.append("- Relationship: my ").append(relationship).append("\n");
        }
        if (occasion != null && !occasion.trim().isEmpty()) {
            prompt.append("- Occasion: ").append(occasion).append("\n");
        }

        prompt.append("\nREQUIREMENTS:\n");
        prompt.append("- Write in English\n");
        prompt.append("- Keep it to ONE sentence only\n");
        prompt.append("- Make it warm, personal, and appropriate for the occasion\n");
        prompt.append("- Maximum 150 characters\n");
        prompt.append("- NO quotes around the message\n");
        prompt.append("- Focus on the occasion and the thoughtfulness behind the gift\n");

        return prompt.toString();
    }

    public String buildSpontaneousGiftMessagePrompt(String giftTitle, String giftDescription, String occasion) {
        StringBuilder prompt = new StringBuilder();

        prompt.append("You are an expert at writing gift messages. ");
        prompt.append("Create a short, formal gift message for the following gift. ");
        prompt.append("RESPOND ONLY with the message itself - NO quotes, introduction, or explanations.\n\n");

        prompt.append("GIFT DETAILS:\n");
        prompt.append("- Gift: ").append(giftTitle != null ? giftTitle : "Special gift").append("\n");
        if (giftDescription != null && !giftDescription.trim().isEmpty()) {
            prompt.append("- Description: ").append(giftDescription).append("\n");
        }

        if (occasion != null && !occasion.trim().isEmpty()) {
            prompt.append("\nOCCASION:\n");
            prompt.append("- Type: ").append(occasion).append("\n");
        }

        prompt.append("\nREQUIREMENTS:\n");
        prompt.append("- Write in English\n");
        prompt.append("- Keep it to ONE sentence only\n");
        prompt.append("- Make it generic and formal but warm\n");
        prompt.append("- Maximum 150 characters\n");
        prompt.append("- NO quotes around the message\n");
        prompt.append("- Focus on the occasion and the thoughtfulness behind the gift\n");
        prompt.append("- Since this is for an unknown recipient, keep it universal and appropriate\n");
        prompt.append("- Use phrases like 'Hope this brings you joy' or 'Wishing you happiness' etc.\n");

        return prompt.toString();
    }

    private int calculateAge(LocalDate birthDate) {
        if (birthDate == null) {
            return 0;
        }
        return Period.between(birthDate, LocalDate.now()).getYears();
    }
}
