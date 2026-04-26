package application;

import common.entity.Hero;
import common.item.Bag;
import common.item.Chest;
import common.item.HealSpell;
import common.item.Item;
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
import mvc.game.view.gui.LogWindowGUI;
import mvc.map.controller.RoomController;
import mvc.map.model.RoomModel;
import mvc.map.view.cli.RoomViewCLI;
import mvc.map.view.gui.RoomViewGUI;

public class RandomGuiMain extends Application {

    private static final int DEFAULT_HERO_DAMAGE = 10;
    private static final int DEFAULT_HERO_BAG_CAPACITY = 5;

    @Override
    public void start(Stage stage) {
        DungeonGenerator generator = new DungeonGenerator(System.currentTimeMillis());
        DungeonData dungeon = generator.generate(12);

        Room startRoom = dungeon.getStartRoom();

        Hero hero = new Hero(
                "Hero",
                100,
                new Bag("Backpack", DEFAULT_HERO_BAG_CAPACITY),
                startRoom,
                DEFAULT_HERO_DAMAGE
        );

        Weapon sword = new Weapon("Sword", 18);
        Scroll healingScroll = new Scroll("Healing Scroll", new HealSpell(25));
        Chest chest = new Chest("Wooden Chest", false, "A small chest full of loot");
        chest.addItem(new Item("Ruby", "A shiny red gem"));
        chest.addItem(new Item("Coin", "An old gold coin"));

        startRoom.addItem(sword);
        startRoom.addItem(healingScroll);
        startRoom.addItem(chest);

        HeroModel heroModel = new HeroModel(hero);
        RoomModel roomModel = new RoomModel(heroModel.getRoom());

        HeroViewCLI heroViewCLI = new HeroViewCLI();
        RoomViewCLI roomViewCLI = new RoomViewCLI();

        HeroViewGUI heroViewGUI = new HeroViewGUI();
        LogWindowGUI logWindowGUI = new LogWindowGUI();
        RoomViewGUI roomViewGUI = new RoomViewGUI(logWindowGUI::append);
        GameViewGUI gameViewGUI = new GameViewGUI(stage, heroViewGUI, roomViewGUI);
        gameViewGUI.setOnShowLogs(logWindowGUI::showWindow);

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