package src.common;

public class Hero extends Character {
    
    private final Bag backpack;
    private Room room;
	
    public Hero (String name, int hp, Bag backpack, Room room) {
        super(name, hp);
        this.backpack = backpack;
        this.room = room;
    }

    public Bag getBackpack(){
        return this.backpack;
    }

    public void addItem(Item item) { 
    	addToInventory(item); 
    }
    
    public Item dropItem(String itemName) {
        for (Item item : getInventory()) {
            if (item.getName().equals(itemName)) {
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
