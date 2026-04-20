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
import mvc.item.controller.ItemController;
import mvc.item.model.ItemModel;
import mvc.item.view.cli.ItemViewCLI;
import mvc.item.view.gui.ItemViewGUI;

public class GuiMain extends Application {

    private static final int DEFAULT_HERO_DAMAGE = 10;
    private static final int DEFAULT_HERO_BAG_CAPACITY = 5;

    @Override
    public void start(Stage stage) {
        // --- domain ---
        Room entrance = new Room("Entrance");
        Room corridor = new Room("Dark Corridor");
        Room treasureRoom = new Room("Treasure Room");

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

        // --- hero mvc ---
        HeroModel heroModel = new HeroModel(hero);
        HeroViewGUI heroViewGUI = new HeroViewGUI(stage);
        HeroViewCLI heroViewCLI = new HeroViewCLI();

        new HeroController(heroModel, heroViewCLI, heroViewGUI);

        // --- item mvc : sword ---
        ItemModel swordModel = new ItemModel(sword, heroModel);
        ItemViewGUI swordViewGUI = new ItemViewGUI(new Stage(), "Sword MVC");
        ItemViewCLI swordViewCLI = new ItemViewCLI("SWORD");
        new ItemController(swordModel, swordViewCLI, swordViewGUI);

        // --- item mvc : key ---
        ItemModel keyModel = new ItemModel(key, heroModel);
        ItemViewGUI keyViewGUI = new ItemViewGUI(new Stage(), "Key MVC");
        ItemViewCLI keyViewCLI = new ItemViewCLI("KEY");
        new ItemController(keyModel, keyViewCLI, keyViewGUI);

        // --- item mvc : scroll ---
        ItemModel scrollModel = new ItemModel(healingScroll, heroModel);
        ItemViewGUI scrollViewGUI = new ItemViewGUI(new Stage(), "Scroll MVC");
        ItemViewCLI scrollViewCLI = new ItemViewCLI("SCROLL");
        new ItemController(scrollModel, scrollViewCLI, scrollViewGUI);

        // --- item mvc : chest ---
        ItemModel chestModel = new ItemModel(chest, heroModel);
        ItemViewGUI chestViewGUI = new ItemViewGUI(new Stage(), "Chest MVC");
        ItemViewCLI chestViewCLI = new ItemViewCLI("CHEST");
        new ItemController(chestModel, chestViewCLI, chestViewGUI);

        // --- init affichage ---
        heroModel.syncState();

        heroViewGUI.setHeroName(hero.getName());
        heroViewGUI.show();

        swordViewGUI.show();
        keyViewGUI.show();
        scrollViewGUI.show();
        chestViewGUI.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}