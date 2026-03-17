package entity.model;

import common.item.Item;
import mvc.Model;
import common.entity.Hero;
import common.map.Room;

public class HeroModel implements Model {

    private final Hero hero;

    public HeroModel(Hero hero) {
        this.hero = hero;
    }

    @Override
    public void run() {
    }

    public Item drop(String itemName) {
        for (Item item : hero.getInventory()) {
            if (item.getName().equalsIgnoreCase(itemName)) {
                hero.dropItem(item); // remove de l’inventaire + ajoute à la room
                return item; // on renvoie l’item pour l'affiché
            }
        }
        return null;
    }

    public Room getRoom() {
        return this.hero.getRoom();
    }

    public String getName() {
        return hero.getName();
    }
}
