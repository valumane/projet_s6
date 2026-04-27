package application;

import javafx.application.Application;
import javafx.stage.Stage;

public class RandomGuiMain extends Application {

    @Override
    public void start(Stage stage) {
        GameLauncher.startRandomGame(stage);
    }

    public static void main(String[] args) {
        launch(args);
    }
}