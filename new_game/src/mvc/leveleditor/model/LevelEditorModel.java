package mvc.leveleditor.model;

import common.leveleditor.LevelData;
import common.leveleditor.LevelRegistry;
import common.leveleditor.LevelRegistry.LevelSummary;
import common.leveleditor.RoomEditorData;
import mvc.mvc.Model;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public class LevelEditorModel implements Model {

    private LevelData currentLevel;

    @Override
    public void run() {}

    // on utilise le levelsummary décrit dans common.LevelRegistry dans le record
    public List<LevelSummary> loadSummaries() {
        return LevelRegistry.loadSummaries();
    }

    public LevelData loadLevel(Path path) {
        try {
            return LevelRegistry.loadLevel(path);
        } catch (Exception e) {
            throw new RuntimeException("Impossible de charger le niveau : " + e.getMessage(), e);
        }
    }

    public void saveLevel(LevelData level) {
        try {
            LevelRegistry.saveLevel(level);
        } catch (IOException e) {
            throw new RuntimeException("Impossible de sauvegarder le niveau : " + e.getMessage(), e);
        }
    }

    public void deleteLevel(Path path) {
        try {
            LevelRegistry.deleteLevel(path);
        } catch (IOException e) {
            throw new RuntimeException("Impossible de supprimer le niveau : " + e.getMessage(), e);
        }
    }

    public int addRoomInDirection(LevelData level, int fromIndex, String direction) {
        RoomEditorData from = level.getRooms().get(fromIndex);
        if (from.hasExitIn(direction)) {
            throw new IllegalStateException("Une sortie existe deja vers " + direction);
        }
        int dx = switch (direction) { case "east" -> 1; case "west" -> -1; default -> 0; };
        int dy = switch (direction) { case "south" -> 1; case "north" -> -1; default -> 0; };
        int newX = from.getGridX() + dx;
        int newY = from.getGridY() + dy;

        // verif aucune room est deja a cette position
        for (RoomEditorData r : level.getRooms()) {
            if (r.getGridX() == newX && r.getGridY() == newY) {

                int existingIndex = level.getRooms().indexOf(r);
                level.connectRooms(fromIndex, direction, existingIndex);
                return existingIndex;
            }
        }

        RoomEditorData newRoom = new RoomEditorData("Salle " + level.getRooms().size(), "", newX, newY);
        level.addRoom(newRoom);
        int newIndex = level.getRooms().size() - 1;
        level.connectRooms(fromIndex, direction, newIndex);
        return newIndex;
    }

    public LevelData getCurrentLevel() { return currentLevel; }
    public void setCurrentLevel(LevelData level) { this.currentLevel = level; }
}