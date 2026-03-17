package application;

import entity.view.gui.HeroViewGUI;
import javafx.application.Application;
import javafx.stage.Stage;
import common.entity.Hero;
import common.item.Bag;
import common.map.Room;

public class GuiMain extends Application {

    private final static int DEFAULT_HERO_DAMAGE = 10;
    private final static int DEFAULT_HERO_BAG_CAPACITY = 5;

    @Override
    public void start(Stage stage) {
        HeroViewGUI gui = new HeroViewGUI(stage);

        Room entrance = new Room("Entrance");

        Hero hero = new Hero(
                "Hero",
                100,
                new Bag(
                        "nom du sac",
                        DEFAULT_HERO_BAG_CAPACITY),
                entrance,
                DEFAULT_HERO_DAMAGE);

        gui.setHeroName("Hero");
        gui.showLocation("Entrance");
        gui.show();
        gui.showHealth();
        gui.showDropObject("Hero", "Sword");

    }

    public static void main(String[] args) {
        launch(args);
    }
}