package pt.isec.gps2526_g42.surprise_me.model.llm;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LlmApi {
    private static final String API_URL = "https://api.groq.com/openai/v1/chat/completions";
    private static final String API_KEY_ENVIRONMENT_VARIABLE = "SURPRISEME_LLM_API_KEY";
    private static final String LOCAL_SECRETS_FILE = "secrets.properties";

    private static final int DESCRIPTION_MAX = 1400;
    private static final Pattern ITEM_START_PATTERN = Pattern.compile("^[1-4]\\..+");

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final String apiKey;

    public LlmApi() {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(30))
                .build();
        this.objectMapper = new ObjectMapper();
        this.apiKey = loadApiKey();
    }

    public String generateGiftSuggestions(String prompt) throws Exception {
        String requestBody = buildRequestBody(prompt);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_URL))
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
            throw new RuntimeException("API Error: " + response.statusCode() + " - " + response.body());
        }

        String rawResponse = parseResponse(response.body());
        return cleanResponse(rawResponse);
    }

    private String buildRequestBody(String prompt) throws Exception {
        var requestData = objectMapper.createObjectNode();

        // requestData.put("model", "gpt-3.5-turbo");
        // requestData.put("max_tokens", 1000);
        // requestData.put("temperature", 0.7);

        requestData.put("model", "openai/gpt-oss-120b");
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

    private static String loadApiKey() {
        String environmentKey = System.getenv(API_KEY_ENVIRONMENT_VARIABLE);
        if (environmentKey != null && !environmentKey.isBlank()) {
            return environmentKey.trim();
        }

        Path secretsPath = Path.of(LOCAL_SECRETS_FILE);
        if (Files.isRegularFile(secretsPath)) {
            Properties properties = new Properties();
            try (InputStream input = Files.newInputStream(secretsPath)) {
                properties.load(input);
            } catch (IOException exception) {
                throw new IllegalStateException("Could not read " + LOCAL_SECRETS_FILE, exception);
            }

            String localKey = properties.getProperty("LLM_API_KEY");
            if (localKey != null && !localKey.isBlank()) {
                return localKey.trim();
            }
        }

        throw new IllegalStateException(
                "Configure " + API_KEY_ENVIRONMENT_VARIABLE + " or " + LOCAL_SECRETS_FILE + " before using gift suggestions."
        );
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
