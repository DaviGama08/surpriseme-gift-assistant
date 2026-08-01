package pt.isec.gps2526_g42.surprise_me.model.data;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class SurpriseMeSerialization {
    private static final String DIRECTORY = System.getProperty("user.home") + File.separator + ".surprise_me";
    private static final String FILE_NAME = "data.spm";

    private SurpriseMeSerialization() {
    }

    // Saves the data in the folder specified
    public static void save(SurpriseMe obj) {
        createDirectory();
        Path filePath = dataFilePath();
        System.out.println("[SM Serialization] Saving data to: " + filePath);
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath.toFile()))) {
            oos.writeObject(obj);
            System.out.println("[SM Serialization] Data saved successfully");
        } catch (Exception ex) {
            System.err.println("[SM Serialization] Could not save data file " + ex.getMessage());
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
            System.err.println("[SM Serialization] Could not open data file " + ex.getMessage());
            return new SurpriseMe();
        }
    }

    private static Path dataFilePath() {
        return Paths.get(DIRECTORY, FILE_NAME);
    }

    private static void createDirectory() {
        try {
            Path directory = Paths.get(DIRECTORY);
            if (Files.notExists(directory)) {
                Files.createDirectories(directory);
            }
        } catch (IOException e) {
            System.err.println("[SM Serialization] Error while creating directory: " + e.getMessage());
        }
    }
}
