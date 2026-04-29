package common.leveleditor;

import java.io.*;
import java.nio.file.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class LevelRegistry {

    private static final Path SAVE_DIR = Path.of(
		System.getProperty("user.dir"), "levels"
    );
    private static final String EXTENSION = ".lvl";


    public static void ensureSaveDir() throws IOException {
        Files.createDirectories(SAVE_DIR);
    }

    public static void saveLevel(LevelData level) throws IOException {
        ensureSaveDir();
        level.touch();
        String filename = sanitizeFilename(level.getLevelName()) + EXTENSION;
        Path path = SAVE_DIR.resolve(filename);
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new BufferedOutputStream(Files.newOutputStream(path)))) {
            oos.writeObject(level);
        }
    }

    public static LevelData loadLevel(Path path) throws IOException, ClassNotFoundException {
        try (ObjectInputStream ois = new ObjectInputStream(
                new BufferedInputStream(Files.newInputStream(path)))) {
            return (LevelData) ois.readObject();
        }
    }

    public static void deleteLevel(Path path) throws IOException {
        Files.deleteIfExists(path);
    }

    public static List<Path> listCustomLevelPaths() throws IOException {
        ensureSaveDir();
        try (var stream = Files.list(SAVE_DIR)) {
            return stream
                .filter(p -> p.toString().endsWith(EXTENSION))
                .sorted()
                .toList();
        }
    }

    // record c'est comme une classe de stockage de données et java genere tt seul
    // les constructeur getter et setters
    public record LevelSummary(String name, String description, Path path, String dateStr) {}

    public static List<LevelSummary> loadSummaries() {
        List<LevelSummary> summaries = new ArrayList<>();
        try {
            for (Path path : listCustomLevelPaths()) {
                try {
                    LevelData level = loadLevel(path);
                    String date = new SimpleDateFormat("dd/MM/yyyy HH:mm")
                        .format(new Date(level.getLastModifiedDate()));
                    summaries.add(new LevelSummary(
                        level.getLevelName(), level.getLevelDescription(), path, date));
                } catch (Exception e) {
                    summaries.add(new LevelSummary(
                        path.getFileName().toString(), "Fichier corrompu", path, "?"));
                }
            }
        } catch (IOException e) {
        	// return vide quand fichier inexistant
        }
        return summaries;
    }

    public static String sanitizeFilename(String name) {
        return name.trim().replaceAll("[^a-zA-Z0-9_\\-àâäéèêëîïôùûüç]", "_");
    }
}