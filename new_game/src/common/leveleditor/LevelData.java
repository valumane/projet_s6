package common.leveleditor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class LevelData implements Serializable {
    private static final long serialVersionUID = 1L;

    private String levelName;
    private String levelDescription;
    private final List<RoomEditorData> rooms = new ArrayList<>();
    private int startRoomIndex = 0;
    private long creationDate;
    private long lastModifiedDate;

    public LevelData() {
        this.creationDate = System.currentTimeMillis();
        this.lastModifiedDate = this.creationDate;
        this.levelName = "Nouveau niveau";
        this.levelDescription = "";
    }

    public LevelData(String name, String description) {
        this();
        this.levelName = name;
        this.levelDescription = description;
    }

    // rooms -----------

    public void addRoom(RoomEditorData room) {
        rooms.add(room);
    }

    public void removeRoom(int index) {
        if (index < 0 || index >= rooms.size()) return;

        for (RoomEditorData r : rooms) {
            r.removeExitsTo(index);
        }

        for (RoomEditorData r : rooms) {
            r.shiftIndicesAfterDeletion(index);
        }

        rooms.remove(index);

        if (startRoomIndex == index) {
            startRoomIndex = 0;
        } else if (startRoomIndex > index) {
            startRoomIndex--;
        }
    }

    public void connectRooms(int indexA, String directionAtoB, int indexB) {
        String directionBtoA = reverseDirection(directionAtoB);
        rooms.get(indexA).addExit(directionAtoB, indexB, false);
        rooms.get(indexB).addExit(directionBtoA, indexA, false);
    }

    public static String reverseDirection(String dir) {
        return switch (dir) {
            case "north" -> "south";
            case "south" -> "north";
            case "east"  -> "west";
            case "west"  -> "east";
            default -> throw new IllegalArgumentException("Direction inconnue : " + dir);
        };
    }


    // getter setters par eclipse
    public String getLevelName() { return levelName; }
    public void setLevelName(String levelName) {
        this.levelName = levelName;
        this.lastModifiedDate = System.currentTimeMillis();
    }
    public String getLevelDescription() { return levelDescription; }
    public void setLevelDescription(String levelDescription) {
        this.levelDescription = levelDescription;
        this.lastModifiedDate = System.currentTimeMillis();
    }
    public List<RoomEditorData> getRooms() { return rooms; }
    public int getStartRoomIndex() { return startRoomIndex; }
    public void setStartRoomIndex(int startRoomIndex) { this.startRoomIndex = startRoomIndex; }
    public RoomEditorData getStartRoom() { return rooms.isEmpty() ? null : rooms.get(startRoomIndex); }
    public long getCreationDate() { return creationDate; }
    public long getLastModifiedDate() { return lastModifiedDate; }
    public void touch() { this.lastModifiedDate = System.currentTimeMillis(); }
}