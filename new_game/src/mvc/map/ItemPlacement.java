package mvc.map;

import common.item.Item;

public class ItemPlacement {
    private final Item item;
    private final double x;
    private final double y;

    public ItemPlacement(Item item, double x, double y) {
        this.item = item;
        this.x = x;
        this.y = y;
    }

    public Item getItem() {
        return item;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }
}