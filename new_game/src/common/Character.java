package src.common;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Character implements Serializable {
    private final String name;
    private int hp;

    private final List<Item> inventory = new ArrayList<>();

    public Character(String name, int hp) {
        this.name = name;
        this.hp = hp;
    }

    public String getName() {
        return name;
    }

    public int getHp() {
        return hp;
    }

    public void setHp(int hp) {
        this.hp = hp;
    }

    public List<Item> getInventory() {
        return inventory;
    }

    public void addToInventory(Item item) {
        inventory.add(item);
    }

    public void removeFromInventory(Item item) {
        inventory.remove(item);
    }
}