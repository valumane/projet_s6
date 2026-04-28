package game;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import location.*;
import entity.*;

public class GameMap implements Serializable {
    private static final long serialVersionUID = 1L;

    private Map<String, Room> rooms;
    private Room startingRoom;

    public GameMap() {
        this.rooms = new HashMap<>();
        initialize();
    }

    private void initialize() {
        // Define rooms
        Room entrance = new BasicRoom("entrance", "You are at the entrance of a dark dungeon. You can go west and east.");
        Room corridor1 = new BasicRoom("dark and silent corridor", "You are in a dark and silent corridor. You can go west and east.");
        Room corridor2 = new BasicRoom("narrow corridor", "You are in a narrow corridor leading deeper into the dungeon. You can go north and east. There is also a locked door to the south.");
        Room corridor3 = new BasicRoom("damp corridor", "You are in a damp corridor with cobwebs on the walls. You can go south and to the north.");
        Room corridor4 = new BasicRoom("swampy corridor", "You are in a swampy corridor with a musty smell. You can go north, east and west.");
        Room corridor5 = new BasicRoom("cul-de-sac", "Thats a cul-de-sac, you need to go back. You can go south.");
        Room corridor6 = new BasicRoom("goblin corridor", "You are in a corridor where goblin noises can be heard from. You can go west and there is a huge locked door to the south.");
        Room runicRoom = new BasicRoom("runic room", "You are in a room filled with ancient runes and an altar at its center.");
        Room treasureRoom = new TreasureRoom("treasure room", "A room filled with sparkling treasures.");
        Room bossRoom = new BossRoom("boss room", "The final boss room.");

        // Define exits
        entrance.addExit("west", new SimpleExit(corridor1));
        entrance.addExit("east", new SimpleExit(corridor4));
        corridor1.addExit("east", new SimpleExit(entrance));
        corridor1.addExit("west", new SimpleExit(corridor2));
        corridor2.addExit("east", new SimpleExit(corridor1));
        corridor2.addExit("north", new SimpleExit(corridor3));
        corridor2.addExit("south", new LockedExit(treasureRoom, true));
        corridor3.addExit("south", new SimpleExit(corridor2));
        corridor3.addExit("north", new SimpleExit(runicRoom));
        runicRoom.addExit("south", new SimpleExit(corridor3));
        treasureRoom.addExit("north", new SimpleExit(corridor2));
        corridor4.addExit("west", new SimpleExit(entrance));
        corridor4.addExit("north", new SimpleExit(corridor5));
        corridor4.addExit("east", new SimpleExit(corridor6));
        corridor5.addExit("south", new SimpleExit(corridor4));
        corridor6.addExit("west", new SimpleExit(corridor4));
        corridor6.addExit("south", new LockedExit(bossRoom, true));
        bossRoom.addExit("north", new SimpleExit(corridor6));

        // Set starting room
        this.startingRoom = entrance;

        // Add rooms to the map
        addRoom(entrance);
        addRoom(corridor1);
        addRoom(corridor2);
        addRoom(corridor3);
        addRoom(corridor4);
        addRoom(corridor5);
        addRoom(corridor6);
        addRoom(runicRoom);
        addRoom(treasureRoom);
        addRoom(bossRoom);

        // corridor1 has a bat
        Monster bat = new Monster("Bat", 20);
        corridor1.addCharacter(bat);
        Key treasureKey = new Key("Treasure Key");
        bat.take(treasureKey);

        // treasureRoom has a potion and the boss key
        Potion mediumPotion = new Potion("Medium Potion", 30);
        treasureRoom.addItem(mediumPotion);
        Key bossKey = new Key("Boss Key");
        treasureRoom.addItem(bossKey);
        Weapon katana = new Weapon("katana", 8);
        treasureRoom.addItem(katana);

        // corridor3 has a spider
        Monster spider = new Monster("Spider", 30);
        corridor3.addCharacter(spider);
        Weapon spiderFang = new Weapon("Spider Fang", 4);
        spider.take(spiderFang);
        spider.setWeapon(spiderFang);

        // runicRoom has a healing scroll
        HealSpell healSpell = new HealSpell(40);
        Scroll healingScroll = new Scroll("Healing Scroll", healSpell);
        runicRoom.addItem(healingScroll);

        // corridor4 has a rat
        Monster rat = new Monster("Rat", 35);
        corridor4.addCharacter(rat);
        Potion smallPotion = new Potion("Small Potion", 20);
        rat.take(smallPotion);

        // corridor6 has a goblin
        Monster goblin = new Monster("Goblin", 40);
        corridor6.addCharacter(goblin);
        Weapon rustySword = new Weapon("Rusty Sword", 3);
        goblin.take(rustySword);
        goblin.setWeapon(rustySword);

        // bossRoom has the final boss, a basilisk
        Monster basilisk = new Boss("Basilisk", 120);
        bossRoom.addCharacter(basilisk);
        Weapon basiliskFang = new Weapon("Basilisk Fang", 10);
        basilisk.take(basiliskFang);
        basilisk.setWeapon(basiliskFang);
    }

    public void addRoom(Room r) {
        rooms.put(r.getName(), r);
    }

    public Room getStartingRoom() {
        return startingRoom;
    }

    public Room getRoom(String name) {
        return rooms.get(name);
    }

}
