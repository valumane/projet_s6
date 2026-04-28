package mvc.menu.controller;

import java.util.List;

import application.GameLauncher;
import common.langage.Langage;
import javafx.application.Platform;
import javafx.stage.Stage;
import mvc.GameConfig;
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

        view.setScores(List.of(""));

        view.setOnNewGame(view::showNewGameWindow);

        view.setOnStartConfiguredGame((playerCount, controls) -> {
            if (controls == null || controls.length < 1 || controls[0] == null) {
                return;
            }

            int safePlayerCount = playerCount == 2 ? 2 : 1;
            boolean controlsAccepted;

            if (safePlayerCount == 2) {
                if (controls.length < 2 || controls[1] == null) {
                    return;
                }
                controlsAccepted = GameConfig.setPlayerControls(controls[0], controls[1]);
            } else {
                controlsAccepted = GameConfig.setPlayer1Controls(controls[0]);
            }

            if (!controlsAccepted) {
                return;
            }

            GameConfig.setPlayerCount(safePlayerCount);
            GameLauncher.startRandomGame(stage, safePlayerCount);
        });

        view.setOnContinue(() -> {
            System.out.println(Langage.t("game.continueNotReady"));
        });

        view.setOnCreateLevel(() -> {
            System.out.println(Langage.t("game.createLevelNotReady"));
        });

        view.setOnSettings(view::showSettingsWindow);

        view.setOnApplySettings((controlScheme, resolution) -> {
            GameConfig.setControlScheme(controlScheme);
            GameConfig.setResolution(resolution);
        });

        view.setOnQuit(Platform::exit);
    }
}