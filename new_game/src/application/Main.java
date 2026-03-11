package application;

import common.item.Item;
import entity.model.Bag;
import entity.model.HeroModel;
import map.controller.RoomController;
import map.model.Room;
import map.view.cli.RoomView;

public class Main {
    public static void main(String[] args) {

        Room entrance = new Room("Entrance","desc room");
        Room corridor = new Room("Dark Corridor","desc room");
        Room treasureRoom = new Room("Treasure Room","desc room");

        entrance.addExit("north", corridor);
        corridor.addExit("south", entrance);
        corridor.addExit("east", treasureRoom);
        treasureRoom.addExit("west", corridor);


        entrance.addItem(new Item("Sword", "A rusty but sharp sword."));
        corridor.addItem(new Item("Potion", "A small healing potion."));
        treasureRoom.addItem(new Item("Key", "An old iron key."));


        HeroModel hero = new HeroModel("Hero", 100, new Bag(), entrance);

        
        RoomController game = new RoomController(hero, new RoomView());
        game.run();
    }
}