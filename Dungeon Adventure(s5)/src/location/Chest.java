package location;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import entity.Hero;
import entity.Item;

public class Chest extends Item implements Serializable {

    private boolean locked;
    private final List<Item> content;

    public Chest(String name, boolean locked) {
        super(name);
        this.locked = locked;
        this.content = new ArrayList<>();
    }

    public void addItem(Item i) {
        content.add(i);
    }

    public void open(Hero h) {
        if (locked) {
            System.out.println("The chest is locked.");
            return;
        }
        if (content.isEmpty()) {
            System.out.println("The chest is empty.");
            return;
        }
        System.out.println("You open the chest and take:");
        for (Item i : content) {
            System.out.println(" - " + i.getName());
            h.take(i);
        }
        content.clear();
    }

    @Override
    public void use(Hero h) {
        open(h);
    }

    public List<Item> getContent() {
        return content;
    }
    
    @Override
    public String getDescription() {
        return getName() + " (chest, " + (locked ? "locked" : "unlocked")
                + ", " + content.size() + " item(s))";
    }
}
