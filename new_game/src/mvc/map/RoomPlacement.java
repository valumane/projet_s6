package mvc.map;

import common.map.Room;

public class RoomPlacement {

    private final Room room;
    private final int gridX;
    private final int gridY;

    public RoomPlacement(Room room, int gridX, int gridY) {
        this.room = room;
        this.gridX = gridX;
        this.gridY = gridY;
    }

    public Room getRoom() {
        return room;
    }

    public int getGridX() {
        return gridX;
    }

    public int getGridY() {
        return gridY;
    }
}