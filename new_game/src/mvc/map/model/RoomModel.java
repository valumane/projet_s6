package mvc.map.model;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import common.entity.Character;
import common.item.Item;
import common.map.Exit;
import common.map.Room;
import mvc.map.ItemPlacement;
import mvc.map.RoomPlacement;
import mvc.mvc.Model;

public class RoomModel implements Model {

    private static final double START_X = 180;
    private static final double START_Y = 175;
    private static final double GAP_X = 220;
    private static final double GAP_Y = 60;

    private Room room;
    private final List<ItemPlacement> itemPlacements = new ArrayList<>();

    private final Map<Room, RoomPlacement> visitedRooms = new LinkedHashMap<>();
    private int currentGridX = 0;
    private int currentGridY = 0;

    public RoomModel(Room room) {
        this.room = room;
        if (room != null) {
            visitedRooms.put(room, new RoomPlacement(room, 0, 0));
        }
        rebuildItemPlacements();
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
        rebuildItemPlacements();
    }

    public void moveTo(Room nextRoom, String direction) {
        if (nextRoom == null) {
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
                this.room = nextRoom;
                rebuildItemPlacements();
                return;
            }
        }

        currentGridX = nextX;
        currentGridY = nextY;
        room = nextRoom;

        if (!visitedRooms.containsKey(nextRoom)) {
            visitedRooms.put(nextRoom, new RoomPlacement(nextRoom, currentGridX, currentGridY));
        }

        rebuildItemPlacements();
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

    public void rebuildItemPlacements() {
        itemPlacements.clear();

        if (room == null) {
            return;
        }

        List<Item> items = room.getItems();
        for (int i = 0; i < items.size(); i++) {
            double x = START_X + (i % 2) * GAP_X;
            double y = START_Y + (i / 2) * GAP_Y;
            itemPlacements.add(new ItemPlacement(items.get(i), x, y));
        }
    }

    public List<ItemPlacement> getItemPlacements() {
        return List.copyOf(itemPlacements);
    }
}