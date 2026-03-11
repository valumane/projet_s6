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
        // Voir ici si il ne faudra pas retirer l'objet de la piece, au moment ou on le récupère dans l'inventaire.
    	this.addToInventory(item); 
    }
    
    // Si le héro a l'item sur lui : on le dépose dans la salle
    public void dropItem(Item itemToDrop) {
        for (Item item : this.getInventory()) {
            if (item.getName().equals(itemToDrop.getName())) {
                if (item instanceof Weapon) {
                    Weapon weapon = (Weapon) item;
                    this.damage -= weapon.getDamage();
                }
                this.removeFromInventory(item);
                this.room.addItem(item)
            }
        }
    }

    // Getter and setters
    public Room getRoom() { 
    	return this.room; 
    }
    
    public void setCurrentRoom(Room r) { 
    	this.room = r; 
    }

    // Les items du héro sont dans le bagpack
    @Override
    public List<Item> getInventory() {
        return this.backpack.getContent();
    }

    // On pourra utiliser la limite du sac pour limiter le nombre d'objets possible à avoir sur soi
    @Override
    public void addToInventory(Item item) {
        this.backpack.addItem(item);
    }

    @Override
    public void removeFromInventory(Item item) {
        this.backpack.removeItem(item);
    }
}
