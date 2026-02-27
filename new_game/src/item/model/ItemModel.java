package item.model;

import java.io.Serializable;
import mvc.Model;

public abstract class ItemModel implements Model, Serializable {
    private final String name;

    protected ItemModel(String name) {
        this.name = name;
    }
    
    @Override
    public void run() { // From [Model]
    	System.out.println("Item#run");
    }

    public String getName() {
        return name;
    }

    // Description utilisée par LOOK
    public String getDescription() {
        return name;
    }

    // public abstract void use(Hero h);
}
