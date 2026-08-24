package pt.isec.gps2526_g42.surprise_me.model.llm;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import pt.isec.gps2526_g42.surprise_me.config.AppConfig;

public class LlmApi implements LlmClient {
    private static final int DESCRIPTION_MAX = 1400;
    private static final Pattern ITEM_START_PATTERN = Pattern.compile("^[1-4]\\..+");

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final AppConfig appConfig;
    private final URI apiUri;
    private final String model;

    public LlmApi() {
        this(AppConfig.getInstance());
    }

    public LlmApi(AppConfig appConfig) {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(30))
                .build();
        this.objectMapper = new ObjectMapper();
        this.appConfig = appConfig;
        this.apiUri = appConfig.getLlmEndpointUri();
        this.model = appConfig.getLlmModel();
    }

    @Override
    public String generateGiftSuggestions(String prompt) throws Exception {
        String apiKey = loadApiKey();
        String requestBody = buildRequestBody(prompt);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(apiUri)
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + apiKey)
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .timeout(Duration.ofSeconds(60))
                .build();

        HttpResponse<String> response = httpClient.send(
                request,
                HttpResponse.BodyHandlers.ofString()
        );

        if (response.statusCode() != 200) {
            throw new RuntimeException("The LLM provider returned HTTP " + response.statusCode());
        }

        String rawResponse = parseResponse(response.body());
        return cleanResponse(rawResponse);
    }

    private String buildRequestBody(String prompt) throws Exception {
        var requestData = objectMapper.createObjectNode();

        // requestData.put("model", "gpt-3.5-turbo");
        // requestData.put("max_tokens", 1000);
        // requestData.put("temperature", 0.7);

        requestData.put("model", model);
        requestData.put("max_tokens", 1024);
        requestData.put("temperature", 0.7);

        var messagesArray = requestData.putArray("messages");
        var messageObj = messagesArray.addObject();
        messageObj.put("role", "user");
        messageObj.put("content", prompt);

        return objectMapper.writeValueAsString(requestData);
    }

    private String parseResponse(String responseBody) throws Exception {
        JsonNode root = objectMapper.readTree(responseBody);
        return root.path("choices").get(0).path("message").path("content").asText();
    }

    private String cleanResponse(String rawResponse) {
        if (rawResponse == null || rawResponse.trim().isEmpty()) {
            return rawResponse;
        }

        String[] lines = rawResponse.split("\n");
        StringBuilder cleaned = new StringBuilder();

        boolean insideItems = false;
        StringBuilder currentItem = null;

        for (String rawLine : lines) {
            String line = rawLine == null ? "" : rawLine.trim();
            if (line.isEmpty()) {
                continue;
            }

            Matcher start = ITEM_START_PATTERN.matcher(line);

            if (start.matches()) {
                if (currentItem != null && currentItem.length() > 0) {
                    String processed = validateAndTruncateDescription(currentItem.toString());
                    cleaned.append(processed).append("\n");
                }
                currentItem = new StringBuilder(line);
                insideItems = true;
            } else if (insideItems && currentItem != null) {
                currentItem.append(' ').append(line);
            }
        }

        if (currentItem != null && currentItem.length() > 0) {
            String processed = validateAndTruncateDescription(currentItem.toString());
            cleaned.append(processed).append("\n");
        }

        String result = cleaned.toString().trim();

        if (result.isEmpty()) {
            String[] introPatterns = {
                    "Here are", "Here's", "I suggest", "I recommend",
                    "Based on", "For", "These are", "The following",
                    "Some ideas", "Gift suggestions", "creative gift ideas"
            };
            StringBuilder fallback = new StringBuilder();
            for (String rawLine : lines) {
                String line = rawLine == null ? "" : rawLine.trim();
                if (line.isEmpty()) {
                    continue;
                }
                boolean isIntro = false;
                for (String pattern : introPatterns) {
                    if (line.toLowerCase().contains(pattern.toLowerCase())) {
                        isIntro = true;
                        break;
                    }
                }
                if (!isIntro) {
                    fallback.append(line).append("\n");
                }
            }
            result = fallback.toString().trim();
        }

        return result.isEmpty() ? rawResponse : result;
    }

    private String loadApiKey() {
        String apiKey = appConfig.getLlmApiKey();
        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalStateException(
                    "Configure " + AppConfig.LLM_API_KEY_ENV + " before using gift suggestions."
            );
        }
        return apiKey;
    }

    private String validateAndTruncateDescription(String line) {
        if (line == null || line.length() <= DESCRIPTION_MAX) {
            return line;
        }

        String pricePattern = "\\(≈\\s*€\\d+\\)";
        Pattern pattern = Pattern.compile(pricePattern);
        Matcher matcher = pattern.matcher(line);

        String price = "";
        String contentWithoutPrice = line;

        if (matcher.find()) {
            price = matcher.group();
            contentWithoutPrice = line.substring(0, matcher.start()).trim();
        }

        int availableSpace = DESCRIPTION_MAX - price.length() - (price.isEmpty() ? 0 : 1);

        if (contentWithoutPrice.length() > availableSpace) {
            String truncated = contentWithoutPrice.substring(0, Math.max(0, availableSpace - 3));
            int lastSpace = Math.max(
                    Math.max(truncated.lastIndexOf(' '), truncated.lastIndexOf(',')),
                    truncated.lastIndexOf('.')
            );

            if (lastSpace > availableSpace / 2) {
                truncated = contentWithoutPrice.substring(0, lastSpace);
            }

            contentWithoutPrice = truncated.trim() + "...";
        }

        return price.isEmpty() ? contentWithoutPrice : contentWithoutPrice + " " + price;
    }
}
