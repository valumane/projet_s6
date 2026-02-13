package game;

import entity.*;
import location.*;

public class Main {
    public static void main(String[] args) {

        GameMap map = new GameMap();
        Room start = map.getStartingRoom();

        // Add a basic weapon to the hero
        Hero hero = new Hero("Hero", 100, new Bag("Backpack", 10));
        Weapon sword = new Weapon("Sword", 5);
        hero.take(sword);
        hero.setWeapon(sword);

        Game game = new Game(map, hero, start);
        game.addCommand(new GoCommand());
        game.addCommand(new HelpCommand());
        game.addCommand(new LookCommand());
        game.addCommand(new AttackCommand());
        game.addCommand(new TakeCommand());
        game.addCommand(new UseCommand());
        game.addCommand(new InventoryCommand());
        game.addCommand(new DropCommand());
        game.addCommand(new StatusCommand());
        game.addCommand(new QuitCommand());
        game.addCommand(new SaveCommand());
        game.addCommand(new LoadCommand());
        game.addCommand(new HealCommand());
        game.executeCommand("HELP");
        game.play();
    }
}
