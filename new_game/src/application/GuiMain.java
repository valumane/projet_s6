package application;

import common.entity.Hero;
import common.item.Bag;
import common.item.Chest;
import common.item.HealSpell;
import common.item.Item;
import common.item.Key;
import common.item.Scroll;
import common.item.Weapon;
import common.map.Room;
import javafx.application.Application;
import javafx.stage.Stage;
import mvc.entity.controller.HeroController;
import mvc.entity.model.HeroModel;
import mvc.entity.view.cli.HeroViewCLI;
import mvc.entity.view.gui.HeroViewGUI;
import mvc.game.controller.GameController;
import mvc.game.model.GameModel;
import mvc.game.view.gui.GameViewGUI;
import mvc.map.controller.RoomController;
import mvc.map.model.RoomModel;
import mvc.map.view.cli.RoomViewCLI;
import mvc.map.view.gui.RoomViewGUI;

public class GuiMain extends Application {

    private static final int DEFAULT_HERO_DAMAGE = 10;
    private static final int DEFAULT_HERO_BAG_CAPACITY = 5;

    @Override
    public void start(Stage stage) {
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

        HeroViewGUI heroViewGUI = new HeroViewGUI();
        RoomViewGUI roomViewGUI = new RoomViewGUI();
        GameViewGUI gameViewGUI = new GameViewGUI(stage, heroViewGUI, roomViewGUI);

        new HeroController(heroModel, heroViewCLI, heroViewGUI);
        RoomController roomController = new RoomController(roomModel, heroModel, roomViewCLI, roomViewGUI);
        new GameController(new GameModel(), gameViewGUI, roomController);

        heroViewGUI.setHeroName(hero.getName());
        heroModel.syncState();
        roomController.onEnterRoom();

        gameViewGUI.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}