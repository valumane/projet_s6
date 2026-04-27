package common.leveleditor;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public class LevelRegistry {

    // dossier de sauvegarde
    private static final Path SAVE_DIR = Path.of(
        System.getProperty("user.home"), ".jeuxquijeux", "levels"
    );

    // niveaux par defaut
    private static final List<String> DEFAULT_LEVEL_IDS = List.of("default_dungeon");

    public static void ensureSaveDir() throws IOException {
        Files.createDirectories(SAVE_DIR);
    }

    public static void saveLevel(LevelData level) throws IOException {
        ensureSaveDir();
        String filename = sanitize(level.getLevelName()) + ".lvl";
        Path path = SAVE_DIR.resolve(filename);
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new BufferedOutputStream(new FileOutputStream(path.toFile())))) {
            oos.writeObject(level);
        }
    }

    public static LevelData loadLevel(Path path) throws IOException, ClassNotFoundException {
        try (ObjectInputStream ois = new ObjectInputStream(
                new BufferedInputStream(new FileInputStream(path.toFile())))) {
            return (LevelData) ois.readObject();
        }
    }

    public static List<Path> listCustomLevelPaths() throws IOException {
        ensureSaveDir();
        try (var stream = Files.list(SAVE_DIR)) {
            return stream
                .filter(p -> p.toString().endsWith(".lvl"))
                .sorted()
                .toList();
        }
    }

    public static void deleteLevel(Path path) throws IOException {
        Files.deleteIfExists(path);
    }

    private static String sanitize(String name) {
        return name.replaceAll("[^a-zA-Z0-9_\\-]", "_");
    }
}