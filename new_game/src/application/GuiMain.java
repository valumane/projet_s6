package application;

import entity.controller.HeroController;
import entity.model.HeroModel;
import entity.view.cli.HeroViewCLI;
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
        // --- domain ---
        Room entrance = new Room("Entrance");
        Hero hero = new Hero(
                "Hero",
                100,
                new Bag("nom du sac", DEFAULT_HERO_BAG_CAPACITY),
                entrance,
                DEFAULT_HERO_DAMAGE);

        // --- mvc ---
        HeroModel model = new HeroModel(hero);
        HeroViewGUI viewGUI = new HeroViewGUI(stage);
        HeroViewCLI viewCLI = new HeroViewCLI();

        new HeroController(model, viewGUI, viewCLI); // <- branche le bouton ici

        // --- init affichage ---
        viewGUI.setHeroName(model.getName());
        viewGUI.showLocation(model.getRoom().getName());
        viewGUI.showHealth(model.getHealth());
        viewGUI.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}