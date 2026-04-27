package mvc.menu.controller;

import java.util.List;

import application.GameLauncher;
import javafx.application.Platform;
import javafx.stage.Stage;
import mvc.menu.model.MainMenuModel;
import mvc.menu.view.gui.MainMenuViewGUI;
import mvc.mvc.Controller;

public class MainMenuController extends Controller {

    private final MainMenuModel model;
    private final MainMenuViewGUI view;
    private final Stage stage;

    public MainMenuController(MainMenuModel model, MainMenuViewGUI view, Stage stage) {
        super(model, view, view);
        this.model = model;
        this.view = view;
        this.stage = stage;

        view.setScores(List.of(
                ""));

        view.setOnNewGame(this::startNewGame);

        view.setOnContinue(() -> {
            System.out.println("Continue sera ajouté plus tard.");
        });

        view.setOnCreateLevel(() -> {
            System.out.println("Create Niveau sera ajouté plus tard.");
        });

        view.setOnSettings(view::showSettingsWindow);
        view.setOnQuit(Platform::exit);
    }

    private void startNewGame() {
        GameLauncher.startRandomGame(stage);
    }
}