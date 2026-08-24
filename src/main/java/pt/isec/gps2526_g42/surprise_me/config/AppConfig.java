package pt.isec.gps2526_g42.surprise_me.config;

import io.github.cdimascio.dotenv.Dotenv;
import io.github.cdimascio.dotenv.DotenvEntry;

import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public final class AppConfig {
    public static final String LLM_API_KEY_ENV = "SURPRISEME_LLM_API_KEY";
    public static final String LLM_ENDPOINT_ENV = "SURPRISEME_LLM_ENDPOINT";
    public static final String LLM_MODEL_ENV = "SURPRISEME_LLM_MODEL";
    public static final String DATA_DIRECTORY_ENV = "SURPRISEME_DATA_DIR";
    public static final String DATA_DIRECTORY_PROPERTY = "surpriseme.data.dir";

    public static final String DEFAULT_LLM_ENDPOINT = "https://api.groq.com/openai/v1/chat/completions";
    public static final String DEFAULT_LLM_MODEL = "openai/gpt-oss-120b";

    private static final class Holder {
        private static final AppConfig INSTANCE = loadDefault();
    }

    private final Function<String, String> environment;
    private final Map<String, String> fileValues;
    private final Function<String, String> systemProperties;

    public static AppConfig getInstance() {
        return Holder.INSTANCE;
    }

    static AppConfig loadDefault() {
        Dotenv dotenv = Dotenv.configure()
                .ignoreIfMissing()
                .ignoreIfMalformed()
                .load();

        Map<String, String> fileValues = new HashMap<>();
        for (DotenvEntry entry : dotenv.entries(Dotenv.Filter.DECLARED_IN_ENV_FILE)) {
            fileValues.put(entry.getKey(), entry.getValue());
        }
        return new AppConfig(System::getenv, fileValues, System::getProperty);
    }

    AppConfig(Function<String, String> environment, Map<String, String> fileValues) {
        this(environment, fileValues, name -> null);
    }

    AppConfig(
            Function<String, String> environment,
            Map<String, String> fileValues,
            Function<String, String> systemProperties
    ) {
        this.environment = environment != null ? environment : name -> null;
        this.fileValues = fileValues != null ? Map.copyOf(fileValues) : Map.of();
        this.systemProperties = systemProperties != null ? systemProperties : name -> null;
    }

    public String getLlmApiKey() {
        return resolve(LLM_API_KEY_ENV);
    }

    public String getLlmEndpoint() {
        return resolve(LLM_ENDPOINT_ENV, DEFAULT_LLM_ENDPOINT);
    }

    public URI getLlmEndpointUri() {
        URI uri = URI.create(getLlmEndpoint());
        if (!"https".equalsIgnoreCase(uri.getScheme())) {
            throw new IllegalStateException("The LLM endpoint must use HTTPS");
        }
        return uri;
    }

    public String getLlmModel() {
        return resolve(LLM_MODEL_ENV, DEFAULT_LLM_MODEL);
    }

    public Path getDataDirectory() {
        String property = systemProperties.apply(DATA_DIRECTORY_PROPERTY);
        if (isPresent(property)) {
            return Paths.get(property.trim()).toAbsolutePath().normalize();
        }

        String configured = resolve(DATA_DIRECTORY_ENV);
        if (configured != null) {
            return Paths.get(configured).toAbsolutePath().normalize();
        }

        return Paths.get(System.getProperty("user.home"), ".surprise_me").toAbsolutePath().normalize();
    }

    private String resolve(String name, String defaultValue) {
        String value = resolve(name);
        return value != null ? value : defaultValue;
    }

    private String resolve(String name) {
        String env = environment.apply(name);
        if (isPresent(env)) {
            return env.trim();
        }

        String file = fileValues.get(name);
        if (isPresent(file)) {
            return file.trim();
        }

        return null;
    }

    private static boolean isPresent(String value) {
        return value != null && !value.isBlank();
    }
}
