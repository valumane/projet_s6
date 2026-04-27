package common.item;

import java.util.ArrayList;
import java.util.List;

public class Bag extends Item {
    private final int capacity;
    private final List<Item> content;

    public Bag(String name, int capacity) {
        super(name);
        this.capacity = capacity;
        this.content = new ArrayList<>();
    }

    public int getCapacity() {
        return capacity;
    }

    public boolean addItem(Item i) {
        if (content.size() >= capacity) {
            return false;
        }
        return content.add(i);
    }

    public boolean removeItem(Item i) {
        return content.remove(i);
    }

    public List<Item> getContent() {
        return content;
    }

    @Override
    public String getDescription() {
        return getName() + " (bag, capacity " + capacity + ")";
    }
}
