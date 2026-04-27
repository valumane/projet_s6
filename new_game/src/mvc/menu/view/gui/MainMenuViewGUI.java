package mvc.menu.view.gui;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import mvc.GameConfig;
import mvc.PlayerControls;
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

    private BiConsumer<String, String> onApplySettings;
    private BiConsumer<Integer, PlayerControls[]> onStartConfiguredGame;

    private KeyCell waitingKeyCell;

    public MainMenuViewGUI(Stage stage) {
        this.stage = stage;

        Label titleLabel = new Label("JeuxQuiJeux");
        titleLabel.setStyle("-fx-font-size: 34px; -fx-font-weight: bold;");

        Label authorsLabel = new Label("Auteurs : lucas, mathis, tom, leonard");
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

    public void showNewGameWindow() {
        Stage newGameStage = new Stage();
        newGameStage.initOwner(stage);
        newGameStage.initModality(Modality.APPLICATION_MODAL);
        newGameStage.setTitle("Nouvelle partie");

        PlayerControls initialP1 = GameConfig.getPlayer1Controls();
        PlayerControls initialP2 = GameConfig.getPlayer2Controls();

        ToggleGroup playerCountGroup = new ToggleGroup();
        RadioButton onePlayerRadio = new RadioButton("1 joueur");
        RadioButton twoPlayersRadio = new RadioButton("2 joueurs");
        onePlayerRadio.setToggleGroup(playerCountGroup);
        twoPlayersRadio.setToggleGroup(playerCountGroup);

        if (GameConfig.getPlayerCount() == 2) {
            twoPlayersRadio.setSelected(true);
        } else {
            onePlayerRadio.setSelected(true);
        }

        HBox playerChoiceBox = new HBox(18, onePlayerRadio, twoPlayersRadio);
        playerChoiceBox.setAlignment(Pos.CENTER_LEFT);

        Label hintLabel = new Label("Clique sur une touche pour la modifier, puis appuie sur la nouvelle touche.");
        hintLabel.setWrapText(true);
        Label errorLabel = new Label("");
        errorLabel.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");

        KeyCell[] p1Inventory = createInventoryCells(initialP1);
        KeyCell[] p2Inventory = createInventoryCells(initialP2);

        KeyCell p1Forward = new KeyCell(initialP1.getMoveUp());
        KeyCell p1Backward = new KeyCell(initialP1.getMoveDown());
        KeyCell p1Left = new KeyCell(initialP1.getMoveLeft());
        KeyCell p1Right = new KeyCell(initialP1.getMoveRight());
        KeyCell p1Interact = new KeyCell(initialP1.getInteract());

        KeyCell p2Forward = new KeyCell(initialP2.getMoveUp());
        KeyCell p2Backward = new KeyCell(initialP2.getMoveDown());
        KeyCell p2Left = new KeyCell(initialP2.getMoveLeft());
        KeyCell p2Right = new KeyCell(initialP2.getMoveRight());
        KeyCell p2Interact = new KeyCell(initialP2.getInteract());

        List<Region> player2Rows = new ArrayList<>();

        GridPane inventoryGrid = buildInventoryGrid(p1Inventory, p2Inventory, player2Rows);
        GridPane movementGrid = buildMovementGrid(
                p1Forward, p1Backward, p1Right, p1Left,
                p2Forward, p2Backward, p2Right, p2Left,
                player2Rows);
        GridPane interactGrid = buildInteractGrid(p1Interact, p2Interact, player2Rows);

        registerCapture(p1Inventory);
        registerCapture(p2Inventory);
        registerCapture(p1Forward, p1Backward, p1Left, p1Right, p1Interact);
        registerCapture(p2Forward, p2Backward, p2Left, p2Right, p2Interact);

        Runnable refreshPlayer2Visibility = () -> {
            boolean twoPlayers = twoPlayersRadio.isSelected();
            for (Region node : player2Rows) {
                node.setVisible(twoPlayers);
                node.setManaged(twoPlayers);
            }
        };

        playerCountGroup.selectedToggleProperty().addListener((obs, oldValue, newValue) -> refreshPlayer2Visibility.run());
        refreshPlayer2Visibility.run();

        Button resetDefaultButton = new Button("Touches par défaut");
        resetDefaultButton.setOnAction(e -> {
            PlayerControls defaultP1 = GameConfig.createDefaultPlayer1Controls();
            PlayerControls defaultP2 = GameConfig.createDefaultPlayer2Controls();

            applyControlsToCells(defaultP1, p1Forward, p1Backward, p1Left, p1Right, p1Interact, p1Inventory);
            applyControlsToCells(defaultP2, p2Forward, p2Backward, p2Left, p2Right, p2Interact, p2Inventory);
            errorLabel.setText("");
            hintLabel.setText("Touches par défaut rétablies.");
        });

        Button startButton = new Button("Lancer la partie");
        startButton.setDefaultButton(true);
        startButton.setOnAction(e -> {
            int playerCount = twoPlayersRadio.isSelected() ? 2 : 1;

            PlayerControls p1 = buildControls(p1Forward, p1Backward, p1Left, p1Right, p1Interact, p1Inventory);
            PlayerControls p2 = buildControls(p2Forward, p2Backward, p2Left, p2Right, p2Interact, p2Inventory);

            String error = validateControls(playerCount, p1, p2);
            if (error != null) {
                errorLabel.setText(error);
                return;
            }

            if (onStartConfiguredGame != null) {
                onStartConfiguredGame.accept(playerCount, new PlayerControls[] { p1, p2 });
            }

            newGameStage.close();
        });

        Button cancelButton = new Button("Annuler");
        cancelButton.setCancelButton(true);
        cancelButton.setOnAction(e -> newGameStage.close());

        HBox buttonsBox = new HBox(12, resetDefaultButton, startButton, cancelButton);
        buttonsBox.setAlignment(Pos.CENTER_RIGHT);

        Label title = new Label("Nouvelle partie");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        VBox box = new VBox(14,
                title,
                playerChoiceBox,
                hintLabel,
                sectionTitle("Inventaire"),
                inventoryGrid,
                sectionTitle("Mouvement"),
                movementGrid,
                sectionTitle("Interagir"),
                interactGrid,
                errorLabel,
                buttonsBox);

        box.setPadding(new Insets(22));
        box.setAlignment(Pos.CENTER_LEFT);
        box.setStyle("-fx-background-color: #f4f4f4;");

        Scene newGameScene = new Scene(box, 900, 620);
        newGameScene.setOnKeyPressed(event -> {
            if (waitingKeyCell == null) {
                return;
            }

            KeyCode code = event.getCode();

            if (code == KeyCode.ESCAPE) {
                waitingKeyCell.refreshText();
                waitingKeyCell = null;
                hintLabel.setText("Modification annulée.");
                event.consume();
                return;
            }

            if (isForbiddenKey(code)) {
                hintLabel.setText("Touche non acceptée pour une action de jeu.");
                event.consume();
                return;
            }

            waitingKeyCell.setKeyCode(code);
            waitingKeyCell = null;
            hintLabel.setText("Touche modifiée. Tu peux en modifier une autre ou lancer la partie.");
            errorLabel.setText("");
            event.consume();
        });

        newGameStage.setScene(newGameScene);
        newGameStage.showAndWait();
    }

    public void showSettingsWindow() {
        Stage settingsStage = new Stage();
        settingsStage.initOwner(stage);
        settingsStage.initModality(Modality.APPLICATION_MODAL);
        settingsStage.setTitle("Paramètres");

        Label resolutionLabel = new Label("Résolution");

        ComboBox<String> resolutionCombo = new ComboBox<>();
        resolutionCombo.getItems().addAll("1200x800", "1600x900");
        resolutionCombo.setValue(GameConfig.getResolution());

        Button applyButton = new Button("Appliquer");
        applyButton.setOnAction(e -> {
            if (onApplySettings != null) {
                onApplySettings.accept(GameConfig.getControlScheme(), resolutionCombo.getValue());
            }

            settingsStage.close();
        });

        Button closeButton = new Button("Fermer");
        closeButton.setOnAction(e -> settingsStage.close());

        VBox box = new VBox(12,
                resolutionLabel,
                resolutionCombo,
                applyButton,
                closeButton);

        box.setPadding(new Insets(20));
        box.setAlignment(Pos.CENTER_LEFT);

        Scene settingsScene = new Scene(box, 320, 180);
        settingsStage.setScene(settingsScene);
        settingsStage.showAndWait();
    }

    private Label sectionTitle(String text) {
        Label label = new Label(text);
        label.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        return label;
    }

    private KeyCell[] createInventoryCells(PlayerControls controls) {
        KeyCode[] keys = controls.getInventoryKeys();
        KeyCell[] cells = new KeyCell[9];

        for (int i = 0; i < cells.length; i++) {
            cells[i] = new KeyCell(keys[i]);
        }

        return cells;
    }

    private GridPane buildInventoryGrid(KeyCell[] p1Inventory, KeyCell[] p2Inventory, List<Region> player2Rows) {
        GridPane grid = createGrid();
        grid.add(createHeaderLabel(""), 0, 0);

        for (int i = 0; i < 9; i++) {
            grid.add(createHeaderLabel("Slot " + (i + 1)), i + 1, 0);
        }

        grid.add(createRowLabel("Joueur 1"), 0, 1);
        for (int i = 0; i < 9; i++) {
            grid.add(p1Inventory[i], i + 1, 1);
        }

        Label p2Label = createRowLabel("Joueur 2");
        player2Rows.add(p2Label);
        grid.add(p2Label, 0, 2);

        for (int i = 0; i < 9; i++) {
            player2Rows.add(p2Inventory[i]);
            grid.add(p2Inventory[i], i + 1, 2);
        }

        return grid;
    }

    private GridPane buildMovementGrid(
            KeyCell p1Forward,
            KeyCell p1Backward,
            KeyCell p1Right,
            KeyCell p1Left,
            KeyCell p2Forward,
            KeyCell p2Backward,
            KeyCell p2Right,
            KeyCell p2Left,
            List<Region> player2Rows
    ) {
        GridPane grid = createGrid();
        grid.add(createHeaderLabel(""), 0, 0);
        grid.add(createHeaderLabel("Avancer"), 1, 0);
        grid.add(createHeaderLabel("Reculer"), 2, 0);
        grid.add(createHeaderLabel("Aller à droite"), 3, 0);
        grid.add(createHeaderLabel("Aller à gauche"), 4, 0);

        grid.add(createRowLabel("Joueur 1"), 0, 1);
        grid.add(p1Forward, 1, 1);
        grid.add(p1Backward, 2, 1);
        grid.add(p1Right, 3, 1);
        grid.add(p1Left, 4, 1);

        Label p2Label = createRowLabel("Joueur 2");
        player2Rows.add(p2Label);
        grid.add(p2Label, 0, 2);

        player2Rows.add(p2Forward);
        player2Rows.add(p2Backward);
        player2Rows.add(p2Right);
        player2Rows.add(p2Left);
        grid.add(p2Forward, 1, 2);
        grid.add(p2Backward, 2, 2);
        grid.add(p2Right, 3, 2);
        grid.add(p2Left, 4, 2);

        return grid;
    }

    private GridPane buildInteractGrid(KeyCell p1Interact, KeyCell p2Interact, List<Region> player2Rows) {
        GridPane grid = createGrid();
        grid.add(createHeaderLabel(""), 0, 0);
        grid.add(createHeaderLabel("Interagir"), 1, 0);

        grid.add(createRowLabel("Joueur 1"), 0, 1);
        grid.add(p1Interact, 1, 1);

        Label p2Label = createRowLabel("Joueur 2");
        player2Rows.add(p2Label);
        player2Rows.add(p2Interact);
        grid.add(p2Label, 0, 2);
        grid.add(p2Interact, 1, 2);

        return grid;
    }

    private GridPane createGrid() {
        GridPane grid = new GridPane();
        grid.setHgap(8);
        grid.setVgap(8);
        grid.setAlignment(Pos.CENTER_LEFT);
        return grid;
    }

    private Label createHeaderLabel(String text) {
        Label label = new Label(text);
        label.setMinWidth(78);
        label.setAlignment(Pos.CENTER);
        label.setStyle("-fx-font-weight: bold;");
        return label;
    }

    private Label createRowLabel(String text) {
        Label label = new Label(text);
        label.setMinWidth(78);
        label.setAlignment(Pos.CENTER_LEFT);
        label.setStyle("-fx-font-weight: bold;");
        return label;
    }

    private void registerCapture(KeyCell... cells) {
        for (KeyCell cell : cells) {
            cell.setOnAction(e -> {
                if (waitingKeyCell != null) {
                    waitingKeyCell.refreshText();
                }

                waitingKeyCell = cell;
                waitingKeyCell.armCapture();
            });
        }
    }

    private PlayerControls buildControls(
            KeyCell forward,
            KeyCell backward,
            KeyCell left,
            KeyCell right,
            KeyCell interact,
            KeyCell[] inventory
    ) {
        KeyCode[] inventoryKeys = new KeyCode[inventory.length];

        for (int i = 0; i < inventory.length; i++) {
            inventoryKeys[i] = inventory[i].getKeyCode();
        }

        return new PlayerControls(
                forward.getKeyCode(),
                backward.getKeyCode(),
                left.getKeyCode(),
                right.getKeyCode(),
                interact.getKeyCode(),
                inventoryKeys);
    }

    private void applyControlsToCells(
            PlayerControls controls,
            KeyCell forward,
            KeyCell backward,
            KeyCell left,
            KeyCell right,
            KeyCell interact,
            KeyCell[] inventory
    ) {
        forward.setKeyCode(controls.getMoveUp());
        backward.setKeyCode(controls.getMoveDown());
        left.setKeyCode(controls.getMoveLeft());
        right.setKeyCode(controls.getMoveRight());
        interact.setKeyCode(controls.getInteract());

        KeyCode[] inventoryKeys = controls.getInventoryKeys();
        for (int i = 0; i < inventory.length; i++) {
            inventory[i].setKeyCode(inventoryKeys[i]);
        }
    }

    private String validateControls(int playerCount, PlayerControls p1, PlayerControls p2) {
        if (p1.hasInternalConflict()) {
            return "Conflit dans les touches du joueur 1.";
        }

        if (playerCount == 2) {
            if (p2.hasInternalConflict()) {
                return "Conflit dans les touches du joueur 2.";
            }

            if (PlayerControls.hasConflict(p1, p2)) {
                return "Conflit entre les touches du joueur 1 et du joueur 2.";
            }
        }

        return null;
    }

    private boolean isForbiddenKey(KeyCode code) {
        return code == null
                || code == KeyCode.UNDEFINED
                || code == KeyCode.SHIFT
                || code == KeyCode.CONTROL
                || code == KeyCode.ALT
                || code == KeyCode.META;
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
            case NUMPAD1 -> "Pavé 1";
            case NUMPAD2 -> "Pavé 2";
            case NUMPAD3 -> "Pavé 3";
            case NUMPAD4 -> "Pavé 4";
            case NUMPAD5 -> "Pavé 5";
            case NUMPAD6 -> "Pavé 6";
            case NUMPAD7 -> "Pavé 7";
            case NUMPAD8 -> "Pavé 8";
            case NUMPAD9 -> "Pavé 9";
            case UP -> "↑";
            case DOWN -> "↓";
            case LEFT -> "←";
            case RIGHT -> "→";
            case ENTER -> "Entrée";
            case SPACE -> "Espace";
            default -> code.getName();
        };
    }

    private static class KeyCell extends Button {
        private KeyCode keyCode;

        KeyCell(KeyCode keyCode) {
            super();
            setKeyCode(keyCode);
            setMinWidth(78);
            setMaxWidth(Double.MAX_VALUE);
            setFocusTraversable(false);
        }

        KeyCode getKeyCode() {
            return keyCode;
        }

        void setKeyCode(KeyCode keyCode) {
            this.keyCode = keyCode;
            refreshText();
            setStyle("-fx-background-color: white; -fx-border-color: #555; -fx-border-width: 1;");
        }

        void armCapture() {
            setText("...");
            setStyle("-fx-background-color: #fff2aa; -fx-border-color: #cc8800; -fx-border-width: 2;");
        }

        void refreshText() {
            setText(formatKey(keyCode));
            setStyle("-fx-background-color: white; -fx-border-color: #555; -fx-border-width: 1;");
        }
    }
}