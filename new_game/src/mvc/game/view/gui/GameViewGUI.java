package mvc.game.view.gui;

import java.util.List;
import java.util.function.IntConsumer;
import java.util.function.LongConsumer;

import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import mvc.GameConfig;
import mvc.PlayerControls;
import mvc.entity.view.gui.HeroViewGUI;
import mvc.game.view.base.GameView;
import mvc.map.MapLayout;
import mvc.map.view.gui.RoomViewGUI;
import javafx.scene.input.KeyEvent;

public class GameViewGUI extends GameView {

    private final Stage stage;
    private final BorderPane root = new BorderPane();
    private final Scene scene;

    private final Button logsButton = new Button("Logs");

    private Runnable onMoveNorth;
    private Runnable onMoveSouth;
    private Runnable onMoveEast;
    private Runnable onMoveWest;
    private Runnable onTakeItem;

    private Runnable onPlayer2MoveNorth;
    private Runnable onPlayer2MoveSouth;
    private Runnable onPlayer2MoveEast;
    private Runnable onPlayer2MoveWest;
    private Runnable onPlayer2Interact;

    private Runnable onShowLogs;
    private Runnable onToggleInventory;
    private Runnable onAttack;

    private Runnable onResetGame;
    private Runnable onSaveGame;
    private Runnable onQuitToMenu;
    private Runnable onQuitToDesktop;
    private Runnable onSettings;

    private final StackPane sceneRoot = new StackPane();
    private final VBox pauseOverlay = new VBox(14);
    private final VBox gameOverOverlay = new VBox(14);

    private boolean paused = false;
    private boolean gameOver = false;

    private boolean p1NorthPressed;
    private boolean p1SouthPressed;
    private boolean p1EastPressed;
    private boolean p1WestPressed;

    private boolean p2NorthPressed;
    private boolean p2SouthPressed;
    private boolean p2EastPressed;
    private boolean p2WestPressed;

    private final VBox inventoryBox1 = new VBox(8);
    private final Label inventoryTitle1 = new Label("Inventaire J1");
    private final VBox inventoryItemsBox1 = new VBox(6);

    private final VBox inventoryBox2 = new VBox(8);
    private final Label inventoryTitle2 = new Label("Inventaire J2");
    private final VBox inventoryItemsBox2 = new VBox(6);

    private LongConsumer onGameTick;

    private IntConsumer onUseInventorySlot;
    private IntConsumer onPlayer2UseInventorySlot;

    private final Label infoLabel = new Label("J1 | Arme équipée : aucune");
    private final Label infoLabel2 = new Label("J2 | Arme équipée : aucune");

    private final AnimationTimer timer = new AnimationTimer() {
        @Override
        public void handle(long now) {
            if (p1NorthPressed && onMoveNorth != null)
                onMoveNorth.run();
            if (p1SouthPressed && onMoveSouth != null)
                onMoveSouth.run();
            if (p1EastPressed && onMoveEast != null)
                onMoveEast.run();
            if (p1WestPressed && onMoveWest != null)
                onMoveWest.run();

            if (p2NorthPressed && onPlayer2MoveNorth != null)
                onPlayer2MoveNorth.run();
            if (p2SouthPressed && onPlayer2MoveSouth != null)
                onPlayer2MoveSouth.run();
            if (p2EastPressed && onPlayer2MoveEast != null)
                onPlayer2MoveEast.run();
            if (p2WestPressed && onPlayer2MoveWest != null)
                onPlayer2MoveWest.run();

            if (onGameTick != null) {
                onGameTick.accept(now);
            }
        }
    };

    public GameViewGUI(Stage stage, HeroViewGUI heroViewGUI, RoomViewGUI roomViewGUI) {
        this(stage, heroViewGUI, null, roomViewGUI);
    }

    public GameViewGUI(Stage stage, HeroViewGUI heroViewGUI, HeroViewGUI secondHeroViewGUI, RoomViewGUI roomViewGUI) {
        this.stage = stage;

        logsButton.setFocusTraversable(false);

        infoLabel.setFocusTraversable(false);
        infoLabel.setStyle("-fx-font-size: 13px; -fx-font-weight: bold;");

        infoLabel2.setFocusTraversable(false);
        infoLabel2.setStyle("-fx-font-size: 13px; -fx-font-weight: bold;");

        logsButton.setOnAction(e -> {
            if (onShowLogs != null) {
                onShowLogs.run();
            }
        });

        buildInventoryBox(inventoryBox1, inventoryTitle1, inventoryItemsBox1);
        buildInventoryBox(inventoryBox2, inventoryTitle2, inventoryItemsBox2);

        VBox leftColumn = buildPlayerColumn(heroViewGUI, inventoryBox1);
        HBox topBar = buildTopBar(roomViewGUI);
        StackPane centerWrapper = buildCenterWrapper(roomViewGUI);
        HBox bottomBar = buildBottomBar(roomViewGUI);

        root.setTop(topBar);
        root.setLeft(leftColumn);
        root.setCenter(centerWrapper);
        root.setBottom(bottomBar);
        root.setPadding(new Insets(10));

        if (secondHeroViewGUI != null) {
            VBox rightColumn = buildPlayerColumn(secondHeroViewGUI, inventoryBox2);
            root.setRight(rightColumn);
        }

        buildPauseOverlay();
        buildGameOverOverlay();

        sceneRoot.getChildren().addAll(root, pauseOverlay, gameOverOverlay);

        pauseOverlay.setVisible(false);
        pauseOverlay.setManaged(false);

        gameOverOverlay.setVisible(false);
        gameOverOverlay.setManaged(false);

        scene = new Scene(sceneRoot, GameConfig.getWindowWidth(), GameConfig.getWindowHeight());

        stage.setTitle("JeuxQuiJeux");
        stage.setScene(scene);

        stage.setMinWidth(900);
        stage.setMinHeight(650);
        stage.setMaximized(false);

        installKeyboardHandling();
        scene.setOnMouseClicked(event -> scene.getRoot().requestFocus());
    }

    @Override
    public void setOnGameTick(LongConsumer action) {
        this.onGameTick = action;
    }

    private void buildInventoryBox(VBox inventoryBox, Label inventoryTitle, VBox inventoryItemsBox) {
        inventoryTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        inventoryBox.getChildren().addAll(inventoryTitle, inventoryItemsBox);
        inventoryBox.setPadding(new Insets(12));
        inventoryBox.setSpacing(8);
        inventoryBox.setPrefWidth(220);
        inventoryBox.setMinWidth(220);
        inventoryBox.setBorder(new Border(
                new BorderStroke(
                        Color.BLACK,
                        BorderStrokeStyle.SOLID,
                        CornerRadii.EMPTY,
                        BorderWidths.DEFAULT)));
        inventoryBox.setStyle(
                "-fx-background-color: white;" +
                        "-fx-border-color: black;" +
                        "-fx-border-width: 1;");
    }

    private VBox buildPlayerColumn(HeroViewGUI heroViewGUI, VBox inventoryBox) {
        VBox column = new VBox(18);
        column.setPadding(new Insets(10, 14, 10, 10));
        column.setAlignment(Pos.TOP_LEFT);

        Region heroStatsBox = heroViewGUI.getRoot();
        heroStatsBox.setStyle(
                "-fx-background-color: white;" +
                        "-fx-border-color: black;" +
                        "-fx-border-width: 1;");

        column.getChildren().addAll(heroStatsBox, inventoryBox);
        return column;
    }

    private HBox buildTopBar(RoomViewGUI roomViewGUI) {
        HBox topBar = new HBox(16);
        topBar.setAlignment(Pos.TOP_LEFT);
        topBar.setPadding(new Insets(10));

        VBox roomInfoContainer = new VBox(roomViewGUI.getInfoBox());
        roomInfoContainer.setAlignment(Pos.TOP_LEFT);
        roomInfoContainer.setPrefWidth(Region.USE_COMPUTED_SIZE);
        roomInfoContainer.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(roomInfoContainer, Priority.ALWAYS);
        roomInfoContainer.setMaxHeight(100);
        roomInfoContainer.setStyle("-fx-background-color: white;");

        VBox minimapContainer = new VBox(roomViewGUI.getMiniMapBox());
        minimapContainer.setAlignment(Pos.TOP_CENTER);
        minimapContainer.setStyle("-fx-background-color: white;");
        minimapContainer.setPrefWidth(210);

        topBar.getChildren().addAll(logsButton, roomInfoContainer, minimapContainer);
        return topBar;
    }

    private StackPane buildCenterWrapper(RoomViewGUI roomViewGUI) {
        StackPane gameContent = roomViewGUI.getGameAreaBox();

        Group scaledGame = new Group(gameContent);

        StackPane centerWrapper = new StackPane(scaledGame);
        centerWrapper.setAlignment(Pos.TOP_CENTER);
        centerWrapper.setPadding(new Insets(0));

        scaledGame.scaleXProperty().bind(Bindings.createDoubleBinding(() -> {
            double availableW = centerWrapper.getWidth();
            double availableH = centerWrapper.getHeight();

            if (availableW <= 0 || availableH <= 0) {
                return 1.0;
            }

            double scaleX = availableW / MapLayout.MAP_WIDTH;
            double scaleY = availableH / MapLayout.MAP_HEIGHT;

            return Math.min(scaleX, scaleY);
        }, centerWrapper.widthProperty(), centerWrapper.heightProperty()));

        scaledGame.scaleYProperty().bind(scaledGame.scaleXProperty());

        return centerWrapper;
    }

    private HBox buildBottomBar(RoomViewGUI roomViewGUI) {
        HBox bottomBar = new HBox(12);
        bottomBar.setPadding(new Insets(0, 10, 10, 10));
        bottomBar.setAlignment(Pos.CENTER_LEFT);

        VBox helpContainer = new VBox(roomViewGUI.getHelpBox());
        helpContainer.setStyle("-fx-background-color: white;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        bottomBar.getChildren().addAll(infoLabel, spacer, helpContainer, infoLabel2);
        return bottomBar;
    }

    private void installKeyboardHandling() {
        scene.setOnKeyPressed(event -> {
            KeyCode code = event.getCode();

            if (gameOver) {
                event.consume();
                return;
            }

            if (code == KeyCode.ESCAPE) {
                togglePauseMenu();
                event.consume();
                return;
            }

            if (paused) {
                event.consume();
                return;
            }

            PlayerControls p1 = GameConfig.getPlayer1Controls();
            PlayerControls p2 = GameConfig.getPlayer2Controls();

            int p1InventorySlot = getInventorySlot(p1, event);
            if (p1InventorySlot != -1) {
                if (onUseInventorySlot != null) {
                    onUseInventorySlot.accept(p1InventorySlot);
                }
                event.consume();
                return;
            }

            int p2InventorySlot = getInventorySlot(p2, event);
            if (p2InventorySlot != -1) {
                if (onPlayer2UseInventorySlot != null) {
                    onPlayer2UseInventorySlot.accept(p2InventorySlot);
                }
                event.consume();
                return;
            }

            if (code == p1.getMoveUp()) {
                p1NorthPressed = true;
                event.consume();
            } else if (code == p1.getMoveDown()) {
                p1SouthPressed = true;
                event.consume();
            } else if (code == p1.getMoveLeft()) {
                p1WestPressed = true;
                event.consume();
            } else if (code == p1.getMoveRight()) {
                p1EastPressed = true;
                event.consume();
            } else if (code == p1.getInteract()) {
                if (onTakeItem != null) {
                    onTakeItem.run();
                }
                event.consume();
            } else if (code == p2.getMoveUp()) {
                p2NorthPressed = true;
                event.consume();
            } else if (code == p2.getMoveDown()) {
                p2SouthPressed = true;
                event.consume();
            } else if (code == p2.getMoveLeft()) {
                p2WestPressed = true;
                event.consume();
            } else if (code == p2.getMoveRight()) {
                p2EastPressed = true;
                event.consume();
            } else if (code == p2.getInteract()) {
                if (onPlayer2Interact != null) {
                    onPlayer2Interact.run();
                }
                event.consume();
            } else if (code == KeyCode.SPACE) {
                if (onAttack != null) {
                    onAttack.run();
                }
                event.consume();
            } else if (code == KeyCode.I) {
                if (onToggleInventory != null) {
                    onToggleInventory.run();
                }
                event.consume();
            }
        });

        scene.setOnKeyReleased(event -> {
            KeyCode code = event.getCode();

            PlayerControls p1 = GameConfig.getPlayer1Controls();
            PlayerControls p2 = GameConfig.getPlayer2Controls();

            if (code == p1.getMoveUp()) {
                p1NorthPressed = false;
            } else if (code == p1.getMoveDown()) {
                p1SouthPressed = false;
            } else if (code == p1.getMoveLeft()) {
                p1WestPressed = false;
            } else if (code == p1.getMoveRight()) {
                p1EastPressed = false;
            } else if (code == p2.getMoveUp()) {
                p2NorthPressed = false;
            } else if (code == p2.getMoveDown()) {
                p2SouthPressed = false;
            } else if (code == p2.getMoveLeft()) {
                p2WestPressed = false;
            } else if (code == p2.getMoveRight()) {
                p2EastPressed = false;
            }
        });
    }

    private int getInventorySlot(PlayerControls controls, KeyEvent event) {
        if (controls == null || event == null || event.getCode() == null) {
            return -1;
        }

        KeyCode code = event.getCode();

        int slot = controls.getInventorySlot(code);

        if (slot != -1) {
            return slot;
        }

        // Le fallback AZERTY ne doit s'appliquer qu'au joueur 1.
        if (controls != GameConfig.getPlayer1Controls()) {
            return -1;
        }

        // Très important :
        // si c'est une touche du pavé numérique, on ne la convertit PAS en touche
        // AZERTY.
        // Sinon NUMPAD1 devient "1", puis J1 vole la sélection de J2.
        if (isNumpadInventoryKey(code)) {
            return -1;
        }

        slot = getAzertyTopRowSlot(event.getText());

        if (slot != -1) {
            return slot;
        }

        String keyName = code.getName();
        return getAzertyTopRowSlot(keyName);
    }

    private boolean isNumpadInventoryKey(KeyCode code) {
        return switch (code) {
            case NUMPAD1, NUMPAD2, NUMPAD3,
                    NUMPAD4, NUMPAD5, NUMPAD6,
                    NUMPAD7, NUMPAD8, NUMPAD9 ->
                true;
            default -> false;
        };
    }

    private int getAzertyTopRowSlot(String text) {
        if (text == null || text.isEmpty()) {
            return -1;
        }

        return switch (text) {
            case "&", "1", "Digit 1", "Ampersand" -> 0;
            case "é", "2", "Digit 2" -> 1;
            case "\"", "3", "Digit 3", "Quote Double" -> 2;
            case "'", "4", "Digit 4", "Quote" -> 3;
            case "(", "5", "Digit 5", "Left Parenthesis" -> 4;
            case "-", "6", "Digit 6", "Minus" -> 5;
            case "è", "7", "Digit 7" -> 6;
            case "_", "8", "Digit 8", "Underscore" -> 7;
            case "ç", "9", "Digit 9" -> 8;
            default -> -1;
        };
    }

    private void clearPressedKeys() {
        p1NorthPressed = false;
        p1SouthPressed = false;
        p1EastPressed = false;
        p1WestPressed = false;

        p2NorthPressed = false;
        p2SouthPressed = false;
        p2EastPressed = false;
        p2WestPressed = false;
    }

    private void togglePauseMenu() {
        if (paused) {
            hidePauseMenu();
        } else {
            showPauseMenu();
        }
    }

    private void showPauseMenu() {
        paused = true;
        clearPressedKeys();

        timer.stop();

        pauseOverlay.setVisible(true);
        pauseOverlay.setManaged(true);
        pauseOverlay.toFront();
    }

    private void hidePauseMenu() {
        if (gameOver) {
            return;
        }

        paused = false;

        pauseOverlay.setVisible(false);
        pauseOverlay.setManaged(false);

        scene.getRoot().requestFocus();
        timer.start();
    }

    private void buildPauseOverlay() {
        Label title = new Label("PAUSE");
        title.setStyle("-fx-font-size: 32px; -fx-font-weight: bold;");

        Button continueButton = new Button("Continue");
        Button resetButton = new Button("Reset");
        Button saveButton = new Button("Save");
        Button quitButton = new Button("Quit");
        Button quitDesktopButton = new Button("Quit to desktop");
        Button settingsButton = new Button("Settings");

        Button[] buttons = {
                continueButton,
                resetButton,
                saveButton,
                quitButton,
                quitDesktopButton,
                settingsButton
        };

        for (Button button : buttons) {
            button.setMinWidth(220);
            button.setFocusTraversable(false);
            button.setStyle("-fx-font-size: 16px;");
        }

        continueButton.setOnAction(e -> hidePauseMenu());

        resetButton.setOnAction(e -> {
            if (onResetGame != null) {
                onResetGame.run();
            }
        });

        saveButton.setOnAction(e -> {
            if (onSaveGame != null) {
                onSaveGame.run();
            }
        });

        quitButton.setOnAction(e -> {
            if (onQuitToMenu != null) {
                onQuitToMenu.run();
            }
        });

        quitDesktopButton.setOnAction(e -> {
            if (onQuitToDesktop != null) {
                onQuitToDesktop.run();
            }
        });

        settingsButton.setOnAction(e -> {
            if (onSettings != null) {
                onSettings.run();
            }
        });

        pauseOverlay.getChildren().addAll(
                title,
                continueButton,
                resetButton,
                saveButton,
                quitButton,
                quitDesktopButton,
                settingsButton);

        pauseOverlay.setAlignment(Pos.CENTER);
        pauseOverlay.setPadding(new Insets(30));
        pauseOverlay.setBackground(new Background(
                new BackgroundFill(
                        Color.rgb(0, 0, 0, 0.70),
                        CornerRadii.EMPTY,
                        Insets.EMPTY)));

        title.setTextFill(Color.WHITE);
    }

    private void buildGameOverOverlay() {
        Label title = new Label("GAME OVER");
        title.setStyle("-fx-font-size: 42px; -fx-font-weight: bold;");
        title.setTextFill(Color.RED);

        Label subtitle = new Label("A hero is dead.");
        subtitle.setStyle("-fx-font-size: 18px;");
        subtitle.setTextFill(Color.WHITE);

        Button restartButton = new Button("Restart");
        Button menuButton = new Button("Quit to menu");
        Button quitDesktopButton = new Button("Quit to desktop");

        Button[] buttons = {
                restartButton,
                menuButton,
                quitDesktopButton
        };

        for (Button button : buttons) {
            button.setMinWidth(220);
            button.setFocusTraversable(false);
            button.setStyle("-fx-font-size: 16px;");
        }

        restartButton.setOnAction(e -> {
            if (onResetGame != null) {
                onResetGame.run();
            }
        });

        menuButton.setOnAction(e -> {
            if (onQuitToMenu != null) {
                onQuitToMenu.run();
            }
        });

        quitDesktopButton.setOnAction(e -> {
            if (onQuitToDesktop != null) {
                onQuitToDesktop.run();
            }
        });

        gameOverOverlay.getChildren().addAll(
                title,
                subtitle,
                restartButton,
                menuButton,
                quitDesktopButton);

        gameOverOverlay.setAlignment(Pos.CENTER);
        gameOverOverlay.setPadding(new Insets(30));
        gameOverOverlay.setBackground(new Background(
                new BackgroundFill(
                        Color.rgb(0, 0, 0, 0.82),
                        CornerRadii.EMPTY,
                        Insets.EMPTY)));
    }

    @Override
    public void setOnUseInventorySlot(IntConsumer action) {
        this.onUseInventorySlot = action;
    }

    @Override
    public void setOnPlayer2UseInventorySlot(IntConsumer action) {
        this.onPlayer2UseInventorySlot = action;
    }

    @Override
    public void displayInfo(String message) {
        Platform.runLater(() -> infoLabel.setText(message));
    }

    @Override
    public void displayPlayer2Info(String message) {
        Platform.runLater(() -> infoLabel2.setText(message));
    }

    @Override
    public void setOnResetGame(Runnable action) {
        this.onResetGame = action;
    }

    @Override
    public void setOnSaveGame(Runnable action) {
        this.onSaveGame = action;
    }

    @Override
    public void setOnQuitToMenu(Runnable action) {
        this.onQuitToMenu = action;
    }

    @Override
    public void setOnQuitToDesktop(Runnable action) {
        this.onQuitToDesktop = action;
    }

    @Override
    public void setOnSettings(Runnable action) {
        this.onSettings = action;
    }

    @Override
    public void setOnMoveNorth(Runnable action) {
        this.onMoveNorth = action;
    }

    @Override
    public void setOnMoveSouth(Runnable action) {
        this.onMoveSouth = action;
    }

    @Override
    public void setOnMoveEast(Runnable action) {
        this.onMoveEast = action;
    }

    @Override
    public void setOnMoveWest(Runnable action) {
        this.onMoveWest = action;
    }

    @Override
    public void setOnPlayer2MoveNorth(Runnable action) {
        this.onPlayer2MoveNorth = action;
    }

    @Override
    public void setOnPlayer2MoveSouth(Runnable action) {
        this.onPlayer2MoveSouth = action;
    }

    @Override
    public void setOnPlayer2MoveEast(Runnable action) {
        this.onPlayer2MoveEast = action;
    }

    @Override
    public void setOnPlayer2MoveWest(Runnable action) {
        this.onPlayer2MoveWest = action;
    }

    @Override
    public void setOnInteract(Runnable action) {
        this.onTakeItem = action;
    }

    @Override
    public void setOnPlayer2Interact(Runnable action) {
        this.onPlayer2Interact = action;
    }

    @Override
    public void setOnShowLogs(Runnable action) {
        this.onShowLogs = action;
    }

    @Override
    public void setOnToggleInventory(Runnable action) {
        this.onToggleInventory = action;
    }

    @Override
    public void show() {
        Platform.runLater(() -> {
            stage.show();
            scene.getRoot().requestFocus();

            if (!gameOver) {
                timer.start();
            }
        });
    }

    @Override
    public void hide() {
        Platform.runLater(() -> {
            timer.stop();
            stage.hide();
        });
    }

    @Override
    public void displayInventory(List<String> items) {
        displayInventoryInBox(inventoryItemsBox1, items);
    }

    @Override
    public void displayPlayer2Inventory(List<String> items) {
        displayInventoryInBox(inventoryItemsBox2, items);
    }

    private void displayInventoryInBox(VBox targetBox, List<String> items) {
        Platform.runLater(() -> {
            targetBox.getChildren().clear();

            if (items == null || items.isEmpty()) {
                Label emptyLabel = new Label("Inventaire vide");
                targetBox.getChildren().add(emptyLabel);
                return;
            }

            for (String itemName : items) {
                Label itemLabel = new Label("- " + itemName);
                targetBox.getChildren().add(itemLabel);
            }
        });
    }

    @Override
    public void toggleInventoryOverlay() {
    }

    @Override
    public void setOnAttack(Runnable action) {
        this.onAttack = action;
    }

    @Override
    public void displayGameOver() {
        Platform.runLater(() -> {
            gameOver = true;
            paused = false;

            clearPressedKeys();

            timer.stop();

            pauseOverlay.setVisible(false);
            pauseOverlay.setManaged(false);

            gameOverOverlay.setVisible(true);
            gameOverOverlay.setManaged(true);
            gameOverOverlay.toFront();
        });
    }

    public void applyCurrentResolution() {
        Platform.runLater(() -> {
            stage.setWidth(GameConfig.getWindowWidth());
            stage.setHeight(GameConfig.getWindowHeight());
            stage.centerOnScreen();
            scene.getRoot().requestFocus();
        });
    }
}