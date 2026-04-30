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

    private final Label titleLabel = new Label();
    private final Label authorsLabel = new Label();
    private final Label scoreTitle = new Label();

    private final Button newGameButton = new Button();
    private final Button continueButton = new Button();
    private final Button createLevelButton = new Button();
    private final Button settingsButton = new Button();
    private final Button quitButton = new Button();

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

        titleLabel.setStyle("-fx-font-size: 34px; -fx-font-weight: bold;");
        authorsLabel.setStyle("-fx-font-size: 13px;");
        scoreTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        newGameButton.setMaxWidth(Double.MAX_VALUE);
        continueButton.setMaxWidth(Double.MAX_VALUE);
        createLevelButton.setMaxWidth(Double.MAX_VALUE);
        settingsButton.setMaxWidth(Double.MAX_VALUE);
        quitButton.setMaxWidth(Double.MAX_VALUE);

        continueButton.setDisable(true);
        //createLevelButton.setDisable(true);

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
        stage.setScene(scene);

        refreshTexts();
    }

    private void refreshTexts() {
        stage.setTitle(Languages.t("menu.windowTitle"));
        titleLabel.setText(Languages.t("menu.title"));
        authorsLabel.setText(Languages.t("menu.authors"));
        newGameButton.setText(Languages.t("menu.newGame"));
        continueButton.setText(Languages.t("menu.continue"));
        createLevelButton.setText(Languages.t("menu.createLevel"));
        settingsButton.setText(Languages.t("menu.settings"));
        quitButton.setText(Languages.t("menu.quit"));
        scoreTitle.setText(Languages.t("menu.highScores"));
    }

    public void showNewGameWindow() {
        GameConfigurationDialog.show(stage, Languages.t("menu.newGame"), true, result -> {
            GameConfig.setLanguage(result.getLanguage());
            GameConfig.setResolution(result.getResolution());
            GameConfig.setPlayerCount(result.getPlayerCount());

            if (result.getPlayerCount() == 2) {
                GameConfig.setPlayerControls(result.getPlayer1Controls(), result.getPlayer2Controls());
            } else {
                GameConfig.setPlayer1Controls(result.getPlayer1Controls());
            }

            refreshTexts();

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
        GameConfigurationDialog.show(stage, Languages.t("menu.settings"), true, result -> {
            GameConfig.setLanguage(result.getLanguage());
            GameConfig.setResolution(result.getResolution());
            GameConfig.setPlayerCount(result.getPlayerCount());

            if (result.getPlayerCount() == 2) {
                GameConfig.setPlayerControls(result.getPlayer1Controls(), result.getPlayer2Controls());
            } else {
                GameConfig.setPlayer1Controls(result.getPlayer1Controls());
            }

            // null = donjon aléatoire sinon chemin absolu du fichier .lvl
            GameConfig.setSelectedMap(result.getSelectedMapPath());

            refreshTexts();

            if (onApplySettings != null) {
                onApplySettings.accept(
                        String.valueOf(result.getPlayerCount()),
                        result.getResolution());
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
        scoresBox.getChildren().removeIf(node -> node instanceof Label && node != scoreTitle);

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
}