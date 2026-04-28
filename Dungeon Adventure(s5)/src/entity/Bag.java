package entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Bag extends Item implements Serializable {
    private final int capacity;
    private final List<Item> content;

    public Bag(String name,int capacity) {
        super(name);
        this.capacity = capacity;
        this.content = new ArrayList<>();
    }

    public int getCapacity() {
        return capacity;
    }

    public boolean addItem(Item i) {
        return content.add(i);
    }

    public boolean removeItem(Item i) {
        return content.remove(i);
    }

    public List<Item> getContent() {
        return content;
    }

    @Override
    public void use(Hero h) {
        System.out.println("ton sac.");
    }

    @Override
    public String getDescription() {
        return getName() + " (bag, capacity " + capacity + ")";
    }
}
