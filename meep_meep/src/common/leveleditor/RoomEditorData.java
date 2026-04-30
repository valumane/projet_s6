package common.leveleditor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class RoomEditorData implements Serializable {
    private static final long serialVersionUID = 1L;

    private String name;
    private String description;
    private int gridX;
    private int gridY;
    private boolean startRoom;
    private boolean bossRoom;

    // sorties directions north, south, east, west
    private final List<String> exitDirections = new ArrayList<>();
    private final List<Integer> exitTargetIndices = new ArrayList<>();
    private final List<Boolean> exitLocked = new ArrayList<>();

    //items (ex "Weapon:epee basique:MELEE:10", "HealScroll:25"
    private final List<String> itemDescriptors = new ArrayList<>();

    // ennemis : "Berserker:2", "Archer:3"
    private final List<String> enemyDescriptors = new ArrayList<>();

    public RoomEditorData(String name, String description, int gridX, int gridY) {
        this.name = name;
        this.description = description;
        this.gridX = gridX;
        this.gridY = gridY;
    }

    public void addExit(String direction, int targetIndex, boolean locked) {
        exitDirections.add(direction);
        exitTargetIndices.add(targetIndex);
        exitLocked.add(locked);
    }

    public void removeExitsTo(int targetIndex) {
        for (int i = exitDirections.size() - 1; i >= 0; i--) {
            if (exitTargetIndices.get(i) == targetIndex) {
                exitDirections.remove(i);
                exitTargetIndices.remove(i);
                exitLocked.remove(i);
            }
        }
    }

    public void shiftIndicesAfterDeletion(int deletedIndex) {
        for (int i = 0; i < exitTargetIndices.size(); i++) {
            if (exitTargetIndices.get(i) > deletedIndex) {
                exitTargetIndices.set(i, exitTargetIndices.get(i) - 1);
            }
        }
    }

    public boolean hasExitIn(String direction) {
        return exitDirections.contains(direction);
    }

    // getter setters par eclipse
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public int getGridX() { return gridX; }
    public int getGridY() { return gridY; }
    public boolean isStartRoom() { return startRoom; }
    public void setStartRoom(boolean startRoom) { this.startRoom = startRoom; }
    public boolean isBossRoom() { return bossRoom; }
    public void setBossRoom(boolean bossRoom) { this.bossRoom = bossRoom; }
    public List<String> getExitDirections() { return exitDirections; }
    public List<Integer> getExitTargetIndices() { return exitTargetIndices; }
    public List<Boolean> getExitLocked() { return exitLocked; }
    public List<String> getItemDescriptors() { return itemDescriptors; }
    public List<String> getEnemyDescriptors() { return enemyDescriptors; }

    public void addItemDescriptor(String descriptor) { itemDescriptors.add(descriptor); }
    public void removeItemDescriptor(String descriptor) { itemDescriptors.remove(descriptor); }
    public void addEnemyDescriptor(String descriptor) { enemyDescriptors.add(descriptor); }
    public void removeEnemyDescriptor(String descriptor) { enemyDescriptors.remove(descriptor); }
}