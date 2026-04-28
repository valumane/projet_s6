package mvc.menu.controller;

import java.util.List;

import application.GameLauncher;
import application.LevelEditorLauncher;
import javafx.application.Platform;
import javafx.stage.Stage;
import mvc.GameConfig;
import mvc.PlayerControls;
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
            
            String mapPath = GameConfig.getSelectedMap();
            if (mapPath == null) {
                // random
                GameLauncher.startRandomGame(stage);
            } else {
                // custom
                try {
                    var levelData = common.leveleditor.LevelRegistry.loadLevel(java.nio.file.Path.of(mapPath));
                    var dungeon = common.leveleditor.LevelDataConverter.toDungeonData(levelData);
                    GameLauncher.startWithDungeon(stage, dungeon);
                } catch (Exception e) {
                    System.err.println("Erreur chargement niveau : " + e.getMessage());
                    GameLauncher.startRandomGame(stage);
                }
            }
        });

        view.setOnContinue(() -> {
            System.out.println("Continue sera ajouté plus tard.");
        });

        view.setOnCreateLevel(() -> {
            LevelEditorLauncher.show(stage);
        });
        
        view.setOnSettings(view::showSettingsWindow);

        view.setOnApplySettings((controlScheme, resolution) -> {
            GameConfig.setControlScheme(controlScheme);
            GameConfig.setResolution(resolution);
        });

        view.setOnQuit(Platform::exit);
    }
}