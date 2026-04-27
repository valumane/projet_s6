package mvc.menu.view.gui;

import java.util.List;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import mvc.GameConfig;
import mvc.menu.view.base.MainMenuView;

public class MainMenuViewGUI extends MainMenuView {

    private final Stage stage;
    private final BorderPane root = new BorderPane();
    private final Scene scene;

    private final Button newGameButton = new Button("New Game");
    private final Button continueButton = new Button("Continue");
    private final Button createLevelButton = new Button("Create Niveau");
    private final Button settingsButton = new Button("Paramètres");
    private final Button quitButton = new Button("Quitter le jeu");

    private final VBox scoresBox = new VBox(8);

    private Runnable onNewGame;
    private Runnable onContinue;
    private Runnable onCreateLevel;
    private Runnable onSettings;
    private Runnable onQuit;

    public MainMenuViewGUI(Stage stage) {
        this.stage = stage;

        Label titleLabel = new Label("JeuxQuiJeux");
        titleLabel.setStyle("-fx-font-size: 34px; -fx-font-weight: bold;");

        Label authorsLabel = new Label("Auteurs : prénom1, prénom2, prénom3, prénom4");
        authorsLabel.setStyle("-fx-font-size: 13px;");

        newGameButton.setMaxWidth(Double.MAX_VALUE);
        continueButton.setMaxWidth(Double.MAX_VALUE);
        createLevelButton.setMaxWidth(Double.MAX_VALUE);
        settingsButton.setMaxWidth(Double.MAX_VALUE);
        quitButton.setMaxWidth(Double.MAX_VALUE);

        continueButton.setDisable(true);
        createLevelButton.setDisable(true);

        newGameButton.setOnAction(e -> {
            if (onNewGame != null)
                onNewGame.run();
        });

        continueButton.setOnAction(e -> {
            if (onContinue != null)
                onContinue.run();
        });

        createLevelButton.setOnAction(e -> {
            if (onCreateLevel != null)
                onCreateLevel.run();
        });

        settingsButton.setOnAction(e -> {
            if (onSettings != null)
                onSettings.run();
        });

        quitButton.setOnAction(e -> {
            if (onQuit != null)
                onQuit.run();
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

        Label scoreTitle = new Label("Meilleurs scores");
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
        stage.setTitle("JeuxQuiJeux - Menu principal");
        stage.setScene(scene);
    }

    public void showSettingsWindow() {
        Stage settingsStage = new Stage();
        settingsStage.initOwner(stage);
        settingsStage.initModality(Modality.APPLICATION_MODAL);
        settingsStage.setTitle("Paramètres");

        Label touchesLabel = new Label("Schéma de touches");

        ComboBox<String> touchesCombo = new ComboBox<>();
        touchesCombo.getItems().addAll("ZQSD + E", "WASD + E");
        touchesCombo.setValue(GameConfig.getControlScheme());

        Label resolutionLabel = new Label("Résolution");

        ComboBox<String> resolutionCombo = new ComboBox<>();
        resolutionCombo.getItems().addAll("1200x800", "1600x900");
        resolutionCombo.setValue(GameConfig.getResolution());

        Button applyButton = new Button("Appliquer");
        applyButton.setOnAction(e -> {
            GameConfig.setControlScheme(touchesCombo.getValue());
            GameConfig.setResolution(resolutionCombo.getValue());
            settingsStage.close();
        });

        Button closeButton = new Button("Fermer");
        closeButton.setOnAction(e -> settingsStage.close());

        VBox box = new VBox(12,
                touchesLabel,
                touchesCombo,
                resolutionLabel,
                resolutionCombo,
                applyButton,
                closeButton);

        box.setPadding(new Insets(20));
        box.setAlignment(Pos.CENTER_LEFT);

        Scene settingsScene = new Scene(box, 320, 260);
        settingsStage.setScene(settingsScene);
        settingsStage.showAndWait();
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
            scoresBox.getChildren().add(new Label("Aucun score pour le moment"));
            return;
        }

        for (String score : scores) {
            scoresBox.getChildren().add(new Label(score));
        }
    }
}