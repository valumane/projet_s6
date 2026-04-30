package application;

import java.util.Scanner;

import common.entity.Hero;
import common.item.Bag;
import common.item.Chest;
import common.item.HealSpell;
import common.item.Item;
import common.item.Key;
import common.item.Scroll;
import common.item.Weapon;
import common.map.Room;
import mvc.entity.controller.HeroController;
import mvc.entity.model.HeroModel;
import mvc.entity.view.base.HeroView;
import mvc.entity.view.cli.HeroViewCLI;
import mvc.map.controller.RoomController;
import mvc.map.model.RoomModel;
import mvc.map.view.base.RoomView;
import mvc.map.view.cli.RoomViewCLI;

public class Main {

    private static final int DEFAULT_HERO_DAMAGE = 10;
    private static final int DEFAULT_HERO_BAG_CAPACITY = 5;

    public static void main(String[] args) {

        Room entrance = new Room("Entrance", "The beginning of the dungeon.");
        Room corridor = new Room("Dark Corridor", "A cold corridor with old stones.");
        Room treasureRoom = new Room("Treasure Room", "A room that seems to hide something valuable.");

        Key key = new Key("Golden Key", "A key to a special door");

        entrance.addExit("north", corridor);
        corridor.addExit("south", entrance);
        corridor.addLockedExit("east", treasureRoom, key);
        treasureRoom.addExit("west", corridor);

        Hero hero = new Hero(
                "Hero",
                100,
                new Bag("Backpack", DEFAULT_HERO_BAG_CAPACITY),
                entrance,
                DEFAULT_HERO_DAMAGE
        );

        Weapon sword = new Weapon("Sword", 18);
        Scroll healingScroll = new Scroll("Healing Scroll", new HealSpell(25));
        Chest chest = new Chest("Wooden Chest", false, "A small chest full of loot");
        chest.addItem(new Item("Ruby", "A shiny red gem"));
        chest.addItem(new Item("Coin", "An old gold coin"));

        entrance.addItem(sword);
        entrance.addItem(healingScroll);
        entrance.addItem(chest);

        hero.addItem(key);

        HeroModel heroModel = new HeroModel(hero);
        RoomModel roomModel = new RoomModel(heroModel.getRoom());

        HeroViewCLI heroViewCLI = new HeroViewCLI();
        RoomViewCLI roomViewCLI = new RoomViewCLI();

        new HeroController(heroModel, heroViewCLI, new NullHeroView());
        RoomController roomController = new RoomController(roomModel, heroModel, roomViewCLI, new NullRoomView());

        heroModel.syncState();
        roomController.onEnterRoom();
        printHelp();

        try (Scanner scanner = new Scanner(System.in)) {
            boolean running = true;

            while (running) {
                System.out.print("> ");

                if (!scanner.hasNextLine()) {
                    break;
                }

                String command = scanner.nextLine().trim().toLowerCase();

                if (command.isEmpty()) {
                    continue;
                }

                running = handleCommand(command, heroModel, roomController);
            }
        }
    }

    private static boolean handleCommand(String command, HeroModel heroModel, RoomController roomController) {
        return switch (command) {
            case "z", "n", "north" -> {
                roomController.goTo("north");
                yield true;
            }
            case "s", "south" -> {
                roomController.goTo("south");
                yield true;
            }
            case "q", "w", "west" -> {
                roomController.goTo("west");
                yield true;
            }
            case "d", "east" -> {
                roomController.goTo("east");
                yield true;
            }
            case "e", "interact", "take", "use", "open" -> {
                roomController.interactCli();
                yield true;
            }
            case "look", "l" -> {
                roomController.onEnterRoom();
                yield true;
            }
            case "inventory", "inv", "i" -> {
                printInventory(heroModel);
                yield true;
            }
            case "health", "hp" -> {
                System.out.println("Health: " + heroModel.getHealth());
                yield true;
            }
            case "help", "h" -> {
                printHelp();
                yield true;
            }
            case "quit", "exit" -> {
                System.out.println("Goodbye.");
                yield false;
            }
            default -> {
                System.out.println("Unknown command. Type help.");
                yield true;
            }
        };
    }

    private static void printInventory(HeroModel heroModel) {
        if (heroModel.getHero().getInventory().isEmpty()) {
            System.out.println("Inventory: empty");
            return;
        }

        System.out.println("Inventory:");
        heroModel.getHero().getInventory().forEach(item ->
                System.out.println("- " + item.getName())
        );
    }

    private static void printHelp() {
        System.out.println();
        System.out.println("Commands:");
        System.out.println("- z / north    : go north");
        System.out.println("- s / south    : go south");
        System.out.println("- q / west     : go west");
        System.out.println("- d / east     : go east");
        System.out.println("- e / interact : interact with room");
        System.out.println("- look         : show current room again");
        System.out.println("- inventory    : show inventory");
        System.out.println("- health       : show hero health");
        System.out.println("- help         : show commands");
        System.out.println("- quit         : leave the game");
        System.out.println();
    }

    private static final class NullRoomView extends RoomView {
        @Override
        public void displayRoom(Room room) {
        }

        @Override
        public void displayMove(String direction, String roomName) {
        }

        @Override
        public void displayNoExit(String direction) {
        }

        @Override
        public void displayMessage(String message) {
        }
    }

    private static final class NullHeroView extends HeroView {
        @Override
        public void showDropObject(String character, String item) {
        }

        @Override
        public void showObjectNotFindInInventory(String item) {
        }

        @Override
        public void showLocation(String loc) {
        }

        @Override
        public void showNoHealSpell() {
        }

        @Override
        public void receiveHealingPower() {
        }

        @Override
        public void showDontKnowHealingSpell() {
        }

        @Override
        public void useHealingPower() {
        }

        @Override
        public void showHealth(int hpHero) {
        }
    }
}