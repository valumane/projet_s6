package common.dungeon;

import common.item.Key;
import common.map.Room;
import java.util.List;

public class DungeonData {
    private final Room startRoom;
    private final List<Room> rooms;
    private final Key goldenKey;

    public DungeonData(Room startRoom, List<Room> rooms, Key goldenKey) {
        this.startRoom = startRoom;
        this.rooms = rooms;
        this.goldenKey = goldenKey;
    }

    public Room getStartRoom() {
        return startRoom;
    }

    public List<Room> getRooms() {
        return rooms;
    }

    public Key getGoldenKey() {
        return goldenKey;
    }
}