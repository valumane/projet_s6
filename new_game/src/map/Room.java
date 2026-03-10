package map;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import entity.model.Item;

public class Room implements Serializable {
    private final String name;
    private final List<Item> items = new ArrayList<>();

    public Room(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void addItem(Item item) {
        items.add(item);
    }

    public List<Item> getItems() {
        return items;
    }
}