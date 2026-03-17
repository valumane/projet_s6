package application;

import entity.view.gui.HeroViewGUI;
import javafx.application.Application;
import javafx.stage.Stage;

public class GuiMain extends Application {
    @Override
    public void start(Stage stage) {
        HeroViewGUI gui = new HeroViewGUI(stage);

        gui.setHeroName("Hero");
        gui.showLocation("Entrance");
        gui.show();
        
        gui.showDropObject("Hero", "Sword");
    }

    public static void main(String[] args) {
        launch(args);
    }
}