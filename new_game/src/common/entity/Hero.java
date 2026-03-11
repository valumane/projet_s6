package common.entity;

import common.item.Bag;
import common.item.Item;
import common.item.Weapon;
import common.map.Room;

public class Hero extends Character {
    
    private final Bag backpack;
    private Room room;
    private int damage;
	
    public Hero (String name, int hp, Bag backpack, Room room, int baseDamage) {
        super(name, hp);
        this.backpack = backpack;
        this.room = room;
        this.damage = baseDamage;
    }

    public Bag getBackpack(){
        return this.backpack;
    }

    public void addItem(Item item) { 
        if (item instanceof Weapon) {
            Weapon weapon = (Weapon) item;
            this.damage += weapon.getDamage();
        }
    	addToInventory(item); 
    }
    
    public Item dropItem(String itemName) {
        for (Item item : getInventory()) {
            if (item.getName().equals(itemName)) {
                if (item instanceof Weapon) {
                    Weapon weapon = (Weapon) item;
                    this.damage -= weapon.getDamage();
                }
                removeFromInventory(item);
                return item; 
            }
        }
        return null;
    }

    // Getter and setters
    public Room getRoom() { 
    	return room; 
    }
    
    public void setCurrentRoom(Room r) { 
    	this.room = r; 
    }
}
