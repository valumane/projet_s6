package entity;

import java.io.Serializable;

public abstract class Item implements Serializable {
    private final String name;

    protected Item(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    // Description utilisée par LOOK
    public String getDescription() {
        return name;
    }

    public abstract void use(Hero h);
}
