package common.item;

import java.io.Serializable;

public abstract class Item implements Serializable {
    private final String name;
    private final String description;

    public Item(String name) {
        this(name, "");
    }

    public Item(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
}