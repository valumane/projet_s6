package entity;

import java.io.Serializable;

public class Hero extends Character implements Serializable {
    private final Bag backpack;
    private Spell healSpell; // compétence Heal

    public Hero(String name, int hp, Bag backpack) {
        super(name, hp);
        this.backpack = backpack;
    }

    public Bag getBackpack() {
        return backpack;
    }

    // DROP : enlève l'item de l'inventaire et le renvoie
    public Item drop(String itemName) {
        for (Item item : getInventory()) {
            if (item.getName().equals(itemName)) {
                removeFromInventory(item);
                System.out.println(getName() + " a laché " + itemName);
                return item; // pas sur d'avoir compris ce que tu voulais dire pas renvoie donc j'ai juste
                             // return lol
            }
        }
        System.out.println(itemName + " pas dans l'inventaire");
        return null;
    }

    // BIN : jette l'item (supprimé pour toujours)
    public boolean bin(String itemName) {
        for (Item item : getInventory()) {
            if (item.getName().equals(itemName)) {
                removeFromInventory(item);
                System.out.println(getName() + " a perdu " + itemName);
                return true;
            }
        }
        System.out.println(itemName + "not in the inventory");
        return false;
    }

    public void learnSpell(Spell s) {
        if (s instanceof HealSpell) {
            this.healSpell = s;
            System.out.println("You receive a healing power.");
        } else {
            System.out.println("You don't know how to use this spell yet.");
        }
    }

    public boolean hasHealSpell() {
        return healSpell != null;
    }

    public void useHealSpell() {
        if (healSpell == null) {
            System.out.println("You don't know any healing spell.");
            return;
        }
        healSpell.cast(this);
    }
}
