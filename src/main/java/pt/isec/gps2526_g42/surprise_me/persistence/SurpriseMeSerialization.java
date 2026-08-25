package pt.isec.gps2526_g42.surprise_me.persistence;

import pt.isec.gps2526_g42.surprise_me.config.AppConfig;
import pt.isec.gps2526_g42.surprise_me.model.data.SurpriseMe;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputFilter;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Map;

public class SurpriseMeSerialization {
    public static final String DATA_DIRECTORY_PROPERTY = AppConfig.DATA_DIRECTORY_PROPERTY;
    public static final String DATA_DIRECTORY_ENVIRONMENT_VARIABLE = AppConfig.DATA_DIRECTORY_ENV;
    private static final String FILE_NAME = "data.spm";
    private static final String TMP_SUFFIX = ".tmp";
    private static final String NEW_SUFFIX = ".new";
    private static final String BAK_SUFFIX = ".bak";
    private static final int MAX_GRAPH_DEPTH = 64;
    private static final int MAX_REFERENCES = 100_000;
    private static final int MAX_ARRAY_LENGTH = 100_000;
    private static final long MAX_STREAM_BYTES = 10L * 1024 * 1024;
    private static final ObjectInputFilter SERIALIZATION_FILTER = SurpriseMeSerialization::filterDeserializedClass;
    private static boolean primaryIsReadable;

    private SurpriseMeSerialization() {
    }

    // Saves the data in the folder specified
    public static void save(SurpriseMe obj) {
        createDirectory();
        Path primary = dataFilePath();
        Path staging = newFilePath();
        Path backup = backupFilePath();
        try {
            writeSerialized(obj, staging);
            // A primary that failed to deserialize must not be rotated onto .bak.
            if (primaryIsReadable && Files.exists(primary)) {
                moveReplacing(primary, backup);
            }
            moveReplacing(staging, primary);
            primaryIsReadable = true;
            deleteLeftoverTmp();
        } catch (Exception ex) {
            System.err.println("[SM Serialization] Could not save the local data file");
            try {
                if (Files.exists(staging) && primaryIsReadable && Files.exists(primary)) {
                    Files.deleteIfExists(staging);
                }
            } catch (IOException ignored) {
                // leftover staging is harmless because the primary is still intact
            }
        }
    }

    // Opens the folder and loads its data
    public static SurpriseMe load() {
        Path primary = dataFilePath();
        Path tmp = tempFilePath();
        Path backup = backupFilePath();

        SurpriseMe loaded = tryLoad(primary);
        if (loaded != null) {
            primaryIsReadable = true;
            return loaded;
        }
        primaryIsReadable = false;

        Path[] stagingCandidates = {newFilePath(), tmp};
        for (Path staging : stagingCandidates) {
            loaded = tryLoad(staging);
            if (loaded != null) {
                installRecoveredPrimary(staging, primary);
                return loaded;
            }
        }

        loaded = tryLoad(backup);
        if (loaded != null) {
            return loaded;
        }

        if (Files.exists(primary) || Files.exists(tmp) || Files.exists(newFilePath()) || Files.exists(backup)) {
            System.err.println("[SM Serialization] Could not recover local data; starting with empty state.");
        }
        return new SurpriseMe();
    }

    public static Path resolveAvatarPath(String relativePath) {
        if (relativePath == null || relativePath.isBlank()) {
            return null;
        }

        try {
            Path dataDirectory = dataDirectory().toAbsolutePath().normalize();
            Path stored = Path.of(relativePath);
            if (stored.isAbsolute()) {
                return null;
            }

            Path resolved = dataDirectory.resolve(stored).normalize();
            if (!resolved.startsWith(dataDirectory)) {
                return null;
            }
            return resolved;
        } catch (InvalidPathException ex) {
            return null;
        }
    }

    public static Path dataFilePath() {
        return dataDirectory().resolve(FILE_NAME);
    }

    public static Path backupFilePath() {
        return dataDirectory().resolve(FILE_NAME + BAK_SUFFIX);
    }

    public static Path tempFilePath() {
        return dataDirectory().resolve(FILE_NAME + TMP_SUFFIX);
    }

    public static Path newFilePath() {
        return dataDirectory().resolve(FILE_NAME + NEW_SUFFIX);
    }

    public static ObjectInputFilter objectInputFilter() {
        return SERIALIZATION_FILTER;
    }

    public static Path dataDirectory() {
        return AppConfig.getInstance().getDataDirectory();
    }

    private static SurpriseMe tryLoad(Path filePath) {
        if (filePath == null || Files.notExists(filePath)) {
            return null;
        }
        try (InputStream in = Files.newInputStream(filePath);
             ObjectInputStream ois = new ObjectInputStream(in)) {
            ois.setObjectInputFilter(SERIALIZATION_FILTER);
            Object loaded = ois.readObject();
            if (loaded instanceof SurpriseMe surpriseMe) {
                return surpriseMe;
            }
            return null;
        } catch (Exception ex) {
            return null;
        }
    }

    private static void installRecoveredPrimary(Path source, Path primary) {
        try {
            moveReplacing(source, primary);
            primaryIsReadable = true;
            deleteLeftoverTmp();
        } catch (IOException ignored) {
            // In-memory state is already recovered; source remains a load candidate.
        }
    }

    private static void deleteLeftoverTmp() {
        try {
            Files.deleteIfExists(tempFilePath());
        } catch (IOException ignored) {
        }
    }

    private static void writeSerialized(SurpriseMe obj, Path tmpPath) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(tmpPath.toFile());
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            oos.writeObject(obj);
            oos.flush();
            fos.getFD().sync();
        }
    }

    private static void moveReplacing(Path source, Path target) throws IOException {
        try {
            Files.move(source, target, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
        } catch (AtomicMoveNotSupportedException | FileAlreadyExistsException ex) {
            Files.move(source, target, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    private static ObjectInputFilter.Status filterDeserializedClass(ObjectInputFilter.FilterInfo filterInfo) {
        if (filterInfo.depth() > MAX_GRAPH_DEPTH
                || filterInfo.references() > MAX_REFERENCES
                || filterInfo.streamBytes() > MAX_STREAM_BYTES) {
            return ObjectInputFilter.Status.REJECTED;
        }
        if (filterInfo.arrayLength() > MAX_ARRAY_LENGTH) {
            return ObjectInputFilter.Status.REJECTED;
        }

        Class<?> serialClass = filterInfo.serialClass();
        if (serialClass == null) {
            return ObjectInputFilter.Status.UNDECIDED;
        }
        if (isAllowedClass(serialClass)) {
            return ObjectInputFilter.Status.ALLOWED;
        }
        return ObjectInputFilter.Status.REJECTED;
    }

    private static boolean isAllowedClass(Class<?> clazz) {
        Class<?> current = clazz;
        while (current.isArray()) {
            current = current.getComponentType();
        }
        if (current.isPrimitive()) {
            return true;
        }

        String name = current.getName();
        if (name.startsWith("pt.isec.gps2526_g42.surprise_me.model.data.")) {
            return true;
        }
        if (current.isEnum() && "pt.isec.gps2526_g42.surprise_me.model".equals(current.getPackageName())) {
            return true;
        }
        if (name.startsWith("java.time.")) {
            return true;
        }
        if (current == Map.Entry.class) {
            return true;
        }
        return switch (name) {
            case "java.util.HashMap",
                 "java.util.HashSet",
                 "java.util.ArrayList",
                 "java.lang.String",
                 "java.lang.Integer",
                 "java.lang.Long",
                 "java.lang.Boolean",
                 "java.lang.Number",
                 "java.lang.Enum",
                 "java.lang.Object" -> true;
            default -> false;
        };
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
