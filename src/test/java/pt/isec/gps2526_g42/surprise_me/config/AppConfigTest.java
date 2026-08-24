package pt.isec.gps2526_g42.surprise_me.config;

import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class AppConfigTest {
    @Test
    void environmentVariableTakesPrecedenceOverDotEnv() {
        AppConfig config = new AppConfig(
                name -> switch (name) {
                    case AppConfig.LLM_API_KEY_ENV -> " env-key ";
                    case AppConfig.LLM_ENDPOINT_ENV -> "https://env.example/v1";
                    case AppConfig.LLM_MODEL_ENV -> "env-model";
                    default -> null;
                },
                Map.of(
                        AppConfig.LLM_API_KEY_ENV, "file-key",
                        AppConfig.LLM_ENDPOINT_ENV, "https://file.example/v1",
                        AppConfig.LLM_MODEL_ENV, "file-model"
                )
        );

        assertEquals("env-key", config.getLlmApiKey());
        assertEquals("https://env.example/v1", config.getLlmEndpoint());
        assertEquals("env-model", config.getLlmModel());
    }

    @Test
    void dotEnvIsUsedWhenEnvironmentVariableIsMissing() {
        AppConfig config = new AppConfig(
                name -> null,
                Map.of(
                        AppConfig.LLM_API_KEY_ENV, "file-key",
                        AppConfig.LLM_ENDPOINT_ENV, "https://file.example/v1",
                        AppConfig.LLM_MODEL_ENV, "file-model"
                )
        );

        assertEquals("file-key", config.getLlmApiKey());
        assertEquals("https://file.example/v1", config.getLlmEndpoint());
        assertEquals("file-model", config.getLlmModel());
    }

    @Test
    void nonSecretValuesFallBackToDefaultsAndSecretsDoNot() {
        AppConfig config = new AppConfig(name -> null, Map.of());

        assertNull(config.getLlmApiKey());
        assertEquals(AppConfig.DEFAULT_LLM_ENDPOINT, config.getLlmEndpoint());
        assertEquals(AppConfig.DEFAULT_LLM_MODEL, config.getLlmModel());
        assertEquals("https", config.getLlmEndpointUri().getScheme());
    }

    @Test
    void blankValuesAreTreatedAsMissing() {
        AppConfig config = new AppConfig(
                name -> "  ",
                Map.of(
                        AppConfig.LLM_API_KEY_ENV, "file-key",
                        AppConfig.LLM_ENDPOINT_ENV, "https://file.example/v1"
                )
        );

        assertEquals("file-key", config.getLlmApiKey());
        assertEquals("https://file.example/v1", config.getLlmEndpoint());
    }

    @Test
    void httpEndpointIsRejected() {
        AppConfig config = new AppConfig(
                name -> AppConfig.LLM_ENDPOINT_ENV.equals(name) ? "http://example.com/v1" : null,
                Map.of()
        );

        IllegalStateException exception = assertThrows(IllegalStateException.class, config::getLlmEndpointUri);
        assertEquals("The LLM endpoint must use HTTPS", exception.getMessage());
    }

    @Test
    void systemPropertyOverridesDataDirectory() {
        AppConfig config = new AppConfig(
                name -> AppConfig.DATA_DIRECTORY_ENV.equals(name) ? "from-env" : null,
                Map.of(AppConfig.DATA_DIRECTORY_ENV, "from-file"),
                name -> AppConfig.DATA_DIRECTORY_PROPERTY.equals(name) ? "from-property" : null
        );

        Path directory = config.getDataDirectory();
        assertTrue(directory.endsWith("from-property"));
        assertTrue(directory.isAbsolute());
    }
}
