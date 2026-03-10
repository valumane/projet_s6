package src.entity.model;

import java.io.Serializable;

import src.mvc.Model;
import src.map.Room; 	// Importer Room

public class HeroModel extends Character implements Serializable, Model {
    private final Bag backpack;
    private Spell healSpell; // compétence Heal
    private Room location;

    public HeroModel(String name, int hp, Bag backpack, Room location) {
        super(name, hp);
        this.backpack = backpack;
        this.location = location;
    }

    public Bag getBackpack() {
        return backpack;
    }

    // DROP : enlève l'item de l'inventaire et le renvoie
    public Item drop(String itemName) {
        for (Item item : getInventory()) {
            if (item.getName().equals(itemName)) {
                removeFromInventory(item);
                return item; 
            }
        }
        return null;
    }

    public boolean learnSpell(Spell s) {
        if (s instanceof HealSpell) {
            this.healSpell = s;
            return true;
        } else {
        	return false;
        }
    }

    public boolean hasHealSpell() {
        return healSpell != null;
    }

    public boolean useHealSpell() {
        if (healSpell == null) {
            return false;
        } else {
        	healSpell.cast(this);
        	return true;
        }
        
    }
    
    public Room getLocation() {
    	return location;
    }
}
