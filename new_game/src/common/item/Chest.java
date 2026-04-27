package common.item;

import java.util.ArrayList;
import java.util.List;

import common.entity.Hero;

public class Chest extends UsableItem {

    private final List<Item> content = new ArrayList<>();
    private boolean locked;

    public Chest(String name, boolean locked) {
        this(name, locked, "A chest");
    }

    public Chest(String name, boolean locked, String description) {
        super(name, description);
        this.locked = locked;
    }

    public void addItem(Item item) {
        content.add(item);
    }

    public List<Item> getContent() {
        return content;
    }

    @Override
    public boolean canBeTaken() {
        return false;
    }

    @Override
    public boolean canBeDropped() {
        return false;
    }

    @Override
    public void use(Hero h) {
        if (locked) {
            return;
        }

        List<Item> movedItems = new ArrayList<>();

        for (Item item : new ArrayList<>(content)) {
            if (h.addItem(item)) {
                movedItems.add(item);
            }
        }

        content.removeAll(movedItems);
    }

    @Override
    public String getDescription() {
        return super.getDescription() + " (chest, " + (locked ? "locked" : "unlocked") + ", " + content.size()
                + " item(s))";
    }
}