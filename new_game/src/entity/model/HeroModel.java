package entity.model;


import java.io.Serializable;

import mvc.Model;

import map.model.Room;

public class HeroModel extends Character implements Serializable, Model {
    private final Bag backpack;
    private Room location;
    private Hero hero

    public HeroModel(String name, int hp, Bag backpack, Room location) {
        super(name, hp);
        this.backpack = backpack;
        this.location = location;
    }

    @Override
    public void run() {
    }


    public Bag getBackpack() {
        return this.hero.getBackpack
    }

    public Item drop(String itemName) {
        for (Item item : getInventory()) {
            if (item.getName().equals(itemName)) {
                removeFromInventory(item);
                return item; 
            }
        }
        return null;
    }

    
    public Room getLocation() {
    	return hero.getLocation;
    }

    public Room getCurrentRoom() { return location; }
    public void setCurrentRoom(Room r) { this.location = r; }
    
    public void addItem(Item item) { addToInventory(item); }
    
    public Item dropItem(String name) { return drop(name); }

}
