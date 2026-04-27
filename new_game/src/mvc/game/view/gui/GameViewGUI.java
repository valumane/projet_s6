package mvc.game.view.gui;

import java.util.List;

import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import mvc.GameConfig;
import mvc.entity.view.gui.HeroViewGUI;
import mvc.game.view.base.GameView;
import mvc.map.MapLayout;
import mvc.map.view.gui.RoomViewGUI;
import javafx.beans.binding.Bindings;
import javafx.scene.Group;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import java.util.function.LongConsumer;
import java.util.function.IntConsumer;

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

    private boolean northPressed;
    private boolean southPressed;
    private boolean eastPressed;
    private boolean westPressed;

    private final VBox inventoryBox = new VBox(8);
    private final Label inventoryTitle = new Label("Inventaire");
    private final VBox inventoryItemsBox = new VBox(6);

    private LongConsumer onGameTick;

    private IntConsumer onUseInventorySlot;
    private final Label infoLabel = new Label("Arme équipée : aucune");

    private final AnimationTimer timer = new AnimationTimer() {
        @Override
        public void handle(long now) {
            if (northPressed && onMoveNorth != null)
                onMoveNorth.run();
            if (southPressed && onMoveSouth != null)
                onMoveSouth.run();
            if (eastPressed && onMoveEast != null)
                onMoveEast.run();
            if (westPressed && onMoveWest != null)
                onMoveWest.run();

            if (onGameTick != null) {
                onGameTick.accept(now);
            }
        }
    };

    @Override
    public void setOnGameTick(LongConsumer action) {
        this.onGameTick = action;
    }

    public GameViewGUI(Stage stage, HeroViewGUI heroViewGUI, RoomViewGUI roomViewGUI) {
        this.stage = stage;

        logsButton.setFocusTraversable(false);
        infoLabel.setFocusTraversable(false);
        infoLabel.setStyle("-fx-font-size: 13px; -fx-font-weight: bold;");
        logsButton.setOnAction(e -> {
            if (onShowLogs != null) {
                onShowLogs.run();
            }
        });

        buildInventoryBox();

        VBox leftColumn = buildLeftColumn(heroViewGUI);
        HBox topBar = buildTopBar(roomViewGUI);
        StackPane centerWrapper = buildCenterWrapper(roomViewGUI);
        HBox bottomBar = buildBottomBar(roomViewGUI);

        root.setTop(topBar);
        root.setLeft(leftColumn);
        root.setCenter(centerWrapper);
        root.setBottom(bottomBar);
        root.setPadding(new Insets(10));

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

    private void buildGameOverOverlay() {
        Label title = new Label("GAME OVER");
        title.setStyle("-fx-font-size: 42px; -fx-font-weight: bold;");
        title.setTextFill(Color.RED);

        Label subtitle = new Label("Your hero is dead.");
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

        for (Button button : buttons) {
            button.setStyle("-fx-font-size: 16px;");
        }
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

        northPressed = false;
        southPressed = false;
        eastPressed = false;
        westPressed = false;

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

    private void buildInventoryBox() {
        inventoryTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        inventoryBox.getChildren().addAll(inventoryTitle, inventoryItemsBox);
        inventoryBox.setPadding(new Insets(12));
        inventoryBox.setSpacing(8);
        inventoryBox.setPrefWidth(220);
        inventoryBox.setMinWidth(220);
        inventoryBox.setBorder(new javafx.scene.layout.Border(
                new javafx.scene.layout.BorderStroke(
                        javafx.scene.paint.Color.BLACK,
                        javafx.scene.layout.BorderStrokeStyle.SOLID,
                        javafx.scene.layout.CornerRadii.EMPTY,
                        javafx.scene.layout.BorderWidths.DEFAULT)));
    }

    private VBox buildLeftColumn(HeroViewGUI heroViewGUI) {
        VBox leftColumn = new VBox(18);
        leftColumn.setPadding(new Insets(10, 14, 10, 10));
        leftColumn.setAlignment(Pos.TOP_LEFT);

        Region heroStatsBox = heroViewGUI.getRoot();
        heroStatsBox.setStyle(
                "-fx-background-color: white;" +
                        "-fx-border-color: black;" +
                        "-fx-border-width: 1;");

        inventoryBox.setStyle(
                "-fx-background-color: white;" +
                        "-fx-border-color: black;" +
                        "-fx-border-width: 1;");

        leftColumn.getChildren().addAll(heroStatsBox, inventoryBox);
        return leftColumn;
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

        bottomBar.getChildren().addAll(infoLabel, spacer, helpContainer);
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

            int inventorySlot = inventorySlotFromKey(code);
            if (inventorySlot != -1) {
                if (onUseInventorySlot != null) {
                    onUseInventorySlot.accept(inventorySlot);
                }
                event.consume();
                return;
            }

            if (code == GameConfig.getMoveUpKey()) {
                northPressed = true;
            } else if (code == GameConfig.getMoveDownKey()) {
                southPressed = true;
            } else if (code == GameConfig.getMoveLeftKey()) {
                westPressed = true;
            } else if (code == GameConfig.getMoveRightKey()) {
                eastPressed = true;
            } else if (code == GameConfig.getInteractKey()) {
                if (onTakeItem != null) {
                    onTakeItem.run();
                }
            } else if (code == KeyCode.SPACE) {
                if (onAttack != null) {
                    onAttack.run();
                }
                event.consume();
            } else if (code == KeyCode.I) {
                if (onToggleInventory != null) {
                    onToggleInventory.run();
                }
            }
        });

        scene.setOnKeyReleased(event -> {
            KeyCode code = event.getCode();

            if (code == GameConfig.getMoveUpKey()) {
                northPressed = false;
            } else if (code == GameConfig.getMoveDownKey()) {
                southPressed = false;
            } else if (code == GameConfig.getMoveLeftKey()) {
                westPressed = false;
            } else if (code == GameConfig.getMoveRightKey()) {
                eastPressed = false;
            }
        });
    }

    private int inventorySlotFromKey(KeyCode code) {
        return switch (code) {
            case DIGIT1, NUMPAD1 -> 0;
            case DIGIT2, NUMPAD2 -> 1;
            case DIGIT3, NUMPAD3 -> 2;
            case DIGIT4, NUMPAD4 -> 3;
            case DIGIT5, NUMPAD5 -> 4;
            case DIGIT6, NUMPAD6 -> 5;
            case DIGIT7, NUMPAD7 -> 6;
            case DIGIT8, NUMPAD8 -> 7;
            case DIGIT9, NUMPAD9 -> 8;
            default -> -1;
        };
    }

    @Override
    public void setOnUseInventorySlot(IntConsumer action) {
        this.onUseInventorySlot = action;
    }

    @Override
    public void displayInfo(String message) {
        Platform.runLater(() -> infoLabel.setText(message));
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
    public void setOnInteract(Runnable action) {
        this.onTakeItem = action;
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
        Platform.runLater(() -> {
            inventoryItemsBox.getChildren().clear();

            if (items == null || items.isEmpty()) {
                Label emptyLabel = new Label("Inventaire vide");
                inventoryItemsBox.getChildren().add(emptyLabel);
                return;
            }

            for (String itemName : items) {
                Label itemLabel = new Label("- " + itemName);
                inventoryItemsBox.getChildren().add(itemLabel);
            }
        });
    }

    @Override
    public void toggleInventoryOverlay() {
        // Inventaire fixe à gauche :
        // on ne masque plus, on laisse juste le refresh se faire.
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

            northPressed = false;
            southPressed = false;
            eastPressed = false;
            westPressed = false;

            timer.stop();

            pauseOverlay.setVisible(false);
            pauseOverlay.setManaged(false);

            gameOverOverlay.setVisible(true);
            gameOverOverlay.setManaged(true);
            gameOverOverlay.toFront();
        });
    }
}