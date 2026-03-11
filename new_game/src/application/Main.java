package application;

import common.entity.Hero;
import common.item.Bag;
import common.item.Item;
import common.item.Key;
import common.item.Weapon;
import common.map.Room;

public class Main {
	// TODO
	// Identifier les constantes (comme celles ci-dessous) et les mettres dans des fichiers a part
	private final static int DEFAULT_HERO_DAMAGE = 10;
	private final static int DEFAULT_HERO_BAG_CAPACITY = 5;
	
    public static void main(String[] args) {

        Room entrance = new Room("Entrance");
        Room corridor = new Room("Dark Corridor");
        Room treasureRoom = new Room("Treasure Room");
        
        Key key = new Key("Key", "A key to a special door");

        entrance.addExit("north", corridor);
        corridor.addExit("south", entrance);
        corridor.addLockedExit("east", treasureRoom, key);
        treasureRoom.addExit("west", corridor);


        entrance.addItem(key);
        corridor.addItem(new Item("Potion"));
        treasureRoom.addItem(new Weapon("Sword", 18));

        // TODO
        // Instancier les commandes ici pour les ajouter ensuite au héro.
        
        
        Hero hero = new Hero(
        		"Hero",
        		100,
        		new Bag(
        				"nom du sac",
        				DEFAULT_HERO_BAG_CAPACITY),
        		entrance,
        		DEFAULT_HERO_DAMAGE);

        System.out.println(hero);
        
        // TODO
        // ajouter les controllers, etc 
        // RoomController game = new RoomController(hero, new RoomView());
        // game.run();
    }
}