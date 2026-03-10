package entity.model;

import java.io.Serializable;

public class Item implements Serializable {
    private final String name;

    public Item(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}