package mvc.map.model;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import common.entity.Character;
import common.item.Item;
import common.map.Exit;
import common.map.Room;
import mvc.mvc.Model;

public class RoomModel implements Model {

    private Room room;

    private final Map<Room, RoomPlacement> visitedRooms = new LinkedHashMap<>();

    private int currentGridX = 0;
    private int currentGridY = 0;

    public RoomModel(Room room) {
        this.room = room;

        if (room != null) {
            visitedRooms.put(room, new RoomPlacement(room, 0, 0));
        }
    }

    @Override
    public void run() {
    }

    public String getName() {
        return room.getName();
    }

    public String getDescription() {
        return room.getDescription();
    }

    public Map<String, Exit> getExits() {
        return room.getExits();
    }

    public List<Item> getItems() {
        return room.getItems();
    }

    public List<Character> getCharacters() {
        return room.getCharacters();
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;

        if (room != null && !visitedRooms.containsKey(room)) {
            visitedRooms.put(room, new RoomPlacement(room, currentGridX, currentGridY));
        }
    }

    public void moveTo(Room nextRoom, String direction) {
        if (nextRoom == null) {
            return;
        }

        RoomPlacement existingPlacement = visitedRooms.get(nextRoom);

        if (existingPlacement != null) {
            currentGridX = existingPlacement.getGridX();
            currentGridY = existingPlacement.getGridY();
            room = nextRoom;
            return;
        }

        int nextX = currentGridX;
        int nextY = currentGridY;

        switch (direction) {
            case "north" -> nextY--;
            case "south" -> nextY++;
            case "east" -> nextX++;
            case "west" -> nextX--;
            default -> {
                room = nextRoom;
                visitedRooms.put(nextRoom, new RoomPlacement(nextRoom, currentGridX, currentGridY));
                return;
            }
        }

        currentGridX = nextX;
        currentGridY = nextY;
        room = nextRoom;

        visitedRooms.put(nextRoom, new RoomPlacement(nextRoom, currentGridX, currentGridY));
    }

    public int getCurrentGridX() {
        return currentGridX;
    }

    public int getCurrentGridY() {
        return currentGridY;
    }

    public List<RoomPlacement> getVisitedRoomPlacements() {
        return List.copyOf(visitedRooms.values());
    }
}