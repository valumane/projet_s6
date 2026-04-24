package mvc.map.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import common.entity.Character;
import common.item.Item;
import common.map.Exit;
import common.map.Room;
import mvc.map.ItemPlacement;
import mvc.mvc.Model;

public class RoomModel implements Model {

    private static final double START_X = 105;
    private static final double START_Y = 110;
    private static final double GAP_X = 140;
    private static final double GAP_Y = 35;

    private Room room;
    private final List<ItemPlacement> itemPlacements = new ArrayList<>();

    public RoomModel(Room room) {
        this.room = room;
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
        rebuildItemPlacements();
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