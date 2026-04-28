package mvc.menu.view.gui;

import java.util.List;
import java.util.function.BiConsumer;

import common.languages.Languages;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import mvc.GameConfig;
import mvc.PlayerControls;
import mvc.menu.view.base.MainMenuView;

public class MainMenuViewGUI extends MainMenuView {

    private final Stage stage;
    private final BorderPane root = new BorderPane();
    private final Scene scene;

    private final Button newGameButton = new Button(Languages.t("menu.newGame"));
    private final Button continueButton = new Button(Languages.t("menu.continue"));
    private final Button createLevelButton = new Button(Languages.t("menu.createLevel"));
    private final Button settingsButton = new Button(Languages.t("menu.settings"));
    private final Button quitButton = new Button(Languages.t("menu.quit"));

    private final VBox scoresBox = new VBox(8);

    private Runnable onNewGame;
    private Runnable onContinue;
    private Runnable onCreateLevel;
    private Runnable onSettings;
    private Runnable onQuit;

    private BiConsumer<String, String> onApplySettings;
    private BiConsumer<Integer, PlayerControls[]> onStartConfiguredGame;

    public MainMenuViewGUI(Stage stage) {
        this.stage = stage;

        Label titleLabel = new Label(Languages.t("menu.title"));
        titleLabel.setStyle("-fx-font-size: 34px; -fx-font-weight: bold;");

        Label authorsLabel = new Label(Languages.t("menu.authors"));
        authorsLabel.setStyle("-fx-font-size: 13px;");

        newGameButton.setMaxWidth(Double.MAX_VALUE);
        continueButton.setMaxWidth(Double.MAX_VALUE);
        createLevelButton.setMaxWidth(Double.MAX_VALUE);
        settingsButton.setMaxWidth(Double.MAX_VALUE);
        quitButton.setMaxWidth(Double.MAX_VALUE);

        continueButton.setDisable(true);
        createLevelButton.setDisable(true);

        newGameButton.setOnAction(e -> {
            if (onNewGame != null) {
                onNewGame.run();
            }
        });

        continueButton.setOnAction(e -> {
            if (onContinue != null) {
                onContinue.run();
            }
        });

        createLevelButton.setOnAction(e -> {
            if (onCreateLevel != null) {
                onCreateLevel.run();
            }
        });

        settingsButton.setOnAction(e -> {
            if (onSettings != null) {
                onSettings.run();
            }
        });

        quitButton.setOnAction(e -> {
            if (onQuit != null) {
                onQuit.run();
            }
        });

        VBox centerBox = new VBox(14,
                titleLabel,
                newGameButton,
                continueButton,
                createLevelButton,
                settingsButton,
                quitButton,
                authorsLabel);
        centerBox.setAlignment(Pos.CENTER);
        centerBox.setPadding(new Insets(30));
        centerBox.setPrefWidth(400);

        Label scoreTitle = new Label(Languages.t("menu.highScores"));
        scoreTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        scoresBox.getChildren().add(scoreTitle);
        scoresBox.setPadding(new Insets(20));
        scoresBox.setMinWidth(220);
        scoresBox.setStyle(
                "-fx-background-color: white;" +
                        "-fx-border-color: black;" +
                        "-fx-border-width: 1;");

        BorderPane.setMargin(centerBox, new Insets(20));
        BorderPane.setMargin(scoresBox, new Insets(20));

        root.setCenter(centerBox);
        root.setRight(scoresBox);
        root.setStyle("-fx-background-color: #f4f4f4;");

        scene = new Scene(root, 1100, 700);
        stage.setTitle(Languages.t("menu.windowTitle"));
        stage.setScene(scene);
    }

    public void showNewGameWindow() {
        GameConfigurationDialog.show(stage, "Nouvelle partie", true, result -> {
            if (onStartConfiguredGame != null) {
                onStartConfiguredGame.accept(
                        result.getPlayerCount(),
                        new PlayerControls[] {
                                result.getPlayer1Controls(),
                                result.getPlayer2Controls()
                        });
            }
        });
    }

    public void showSettingsWindow() {
        GameConfigurationDialog.show(stage, "Paramètres", true, result -> {
            if (onApplySettings != null) {
                onApplySettings.accept(
                        result.getPlayerCount() == 2 ? "2 joueurs" : "1 joueur",
                        result.getResolution());
            }

            if (onStartConfiguredGame != null) {
                // ici on ne lance pas une partie, donc on ne fait rien
                // on garde juste la cohérence de config globale
            }

            GameConfig.setPlayerCount(result.getPlayerCount());

            if (result.getPlayerCount() == 2) {
                GameConfig.setPlayerControls(result.getPlayer1Controls(), result.getPlayer2Controls());
            } else {
                GameConfig.setPlayer1Controls(result.getPlayer1Controls());
            }
        });
    }

    @Override
    public void hide() {
        Platform.runLater(stage::hide);
    }

    @Override
    public void show() {
        Platform.runLater(stage::show);
    }

    @Override
    public void setOnNewGame(Runnable action) {
        this.onNewGame = action;
    }

    @Override
    public void setOnContinue(Runnable action) {
        this.onContinue = action;
    }

    @Override
    public void setOnCreateLevel(Runnable action) {
        this.onCreateLevel = action;
    }

    @Override
    public void setOnSettings(Runnable action) {
        this.onSettings = action;
    }

    @Override
    public void setOnQuit(Runnable action) {
        this.onQuit = action;
    }

    @Override
    public void setScores(List<String> scores) {
        scoresBox.getChildren().removeIf(node -> node instanceof Label && node != scoresBox.getChildren().get(0));

        if (scores == null || scores.isEmpty()) {
            scoresBox.getChildren().add(new Label(Languages.t("menu.noScores")));
            return;
        }

        for (String score : scores) {
            scoresBox.getChildren().add(new Label(score));
        }
    }

    @Override
    public void setOnApplySettings(BiConsumer<String, String> action) {
        this.onApplySettings = action;
    }

    @Override
    public void setOnStartConfiguredGame(BiConsumer<Integer, PlayerControls[]> action) {
        this.onStartConfiguredGame = action;
    }

    private static String formatKey(KeyCode code) {
        if (code == null) {
            return "?";
        }

        return switch (code) {
            case DIGIT1 -> "& / 1";
            case DIGIT2 -> "é / 2";
            case DIGIT3 -> "\" / 3";
            case DIGIT4 -> "' / 4";
            case DIGIT5 -> "( / 5";
            case DIGIT6 -> "- / 6";
            case DIGIT7 -> "è / 7";
            case DIGIT8 -> "_ / 8";
            case DIGIT9 -> "ç / 9";
            case NUMPAD1 -> Languages.t("key.numpad") + " 1";
            case NUMPAD2 -> Languages.t("key.numpad") + " 2";
            case NUMPAD3 -> Languages.t("key.numpad") + " 3";
            case NUMPAD4 -> Languages.t("key.numpad") + " 4";
            case NUMPAD5 -> Languages.t("key.numpad") + " 5";
            case NUMPAD6 -> Languages.t("key.numpad") + " 6";
            case NUMPAD7 -> Languages.t("key.numpad") + " 7";
            case NUMPAD8 -> Languages.t("key.numpad") + " 8";
            case NUMPAD9 -> Languages.t("key.numpad") + " 9";
            case UP -> "↑";
            case DOWN -> "↓";
            case LEFT -> "←";
            case RIGHT -> "→";
            case ENTER -> Languages.t("key.enter");
            case SPACE -> Languages.t("key.space");
            default -> code.getName();
        };
    }

}
