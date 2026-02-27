package item.model;

import java.util.ArrayList;
import java.util.List;

public class BagModel extends ItemModel {
    private final int capacity;
    private final List<ItemModel> content;

    public BagModel(String name,int capacity) {
        super(name);
        this.capacity = capacity;
        this.content = new ArrayList<>();
    }
    
    @Override
    public void run() { // From [Model]
    	System.out.println("Bag#run");
    }

    public int getCapacity() {
        return capacity;
    }

    public boolean addItem(ItemModel i) {
        return content.add(i);
    }

    public boolean removeItem(ItemModel i) {
        return content.remove(i);
    }

    public List<ItemModel> getContent() {
        return content;
    }

    /*
    @Override
    public void use(Hero h) {
        System.out.println("ton sac.");
    }
    */

    @Override
    public String getDescription() {
        return getName() + " (bag, capacity " + capacity + ")";
    }
}
