package pt.isec.gps2526_g42.surprise_me.model.data;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class SurpriseMeSerialization {
    public static final String DATA_DIRECTORY_PROPERTY = "surpriseme.data.dir";
    public static final String DATA_DIRECTORY_ENVIRONMENT_VARIABLE = "SURPRISEME_DATA_DIR";
    private static final String FILE_NAME = "data.spm";

    private SurpriseMeSerialization() {
    }

    // Saves the data in the folder specified
    public static void save(SurpriseMe obj) {
        createDirectory();
        Path filePath = dataFilePath();
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath.toFile()))) {
            oos.writeObject(obj);
        } catch (Exception ex) {
            System.err.println("[SM Serialization] Could not save the local data file");
        }
    }

    // Opens the folder and loads its data
    public static SurpriseMe load() {
        Path filePath = dataFilePath();
        if (Files.notExists(filePath)) {
            return new SurpriseMe();
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filePath.toFile()))) {
            return (SurpriseMe) ois.readObject();
        } catch (Exception ex) {
            System.err.println("[SM Serialization] Could not open the local data file");
            return new SurpriseMe();
        }
    }

    static Path dataFilePath() {
        return dataDirectory().resolve(FILE_NAME);
    }

    public static Path dataDirectory() {
        String configuredDirectory = System.getProperty(DATA_DIRECTORY_PROPERTY);
        if (configuredDirectory == null || configuredDirectory.isBlank()) {
            configuredDirectory = System.getenv(DATA_DIRECTORY_ENVIRONMENT_VARIABLE);
        }
        if (configuredDirectory == null || configuredDirectory.isBlank()) {
            configuredDirectory = Paths.get(System.getProperty("user.home"), ".surprise_me").toString();
        }
        return Paths.get(configuredDirectory).toAbsolutePath().normalize();
    }

    private static void createDirectory() {
        try {
            Path directory = dataDirectory();
            if (Files.notExists(directory)) {
                Files.createDirectories(directory);
            }
        } catch (IOException e) {
            System.err.println("[SM Serialization] Error while creating directory: " + e.getMessage());
        }
    }
}
