package mvc.game.view.gui;

import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import mvc.entity.view.gui.HeroViewGUI;
import mvc.game.view.base.GameView;
import mvc.map.view.gui.RoomViewGUI;

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

    private boolean northPressed;
    private boolean southPressed;
    private boolean eastPressed;
    private boolean westPressed;

    private final AnimationTimer timer = new AnimationTimer() {
        @Override
        public void handle(long now) {
            if (northPressed && onMoveNorth != null) {
                onMoveNorth.run();
            }
            if (southPressed && onMoveSouth != null) {
                onMoveSouth.run();
            }
            if (eastPressed && onMoveEast != null) {
                onMoveEast.run();
            }
            if (westPressed && onMoveWest != null) {
                onMoveWest.run();
            }
        }
    };

    public GameViewGUI(Stage stage, HeroViewGUI heroViewGUI, RoomViewGUI roomViewGUI) {
        this.stage = stage;

        logsButton.setFocusTraversable(false);
        logsButton.setOnAction(e -> {
            if (onShowLogs != null) {
                onShowLogs.run();
            }
        });

        HBox topRow = new HBox(20, heroViewGUI.getRoot(), roomViewGUI.getInfoBox(), logsButton);
        topRow.setPadding(new Insets(10));

        root.setTop(topRow);
        root.setCenter(roomViewGUI.getRoot());
        root.setFocusTraversable(true);

        scene = new Scene(root, 1300, 860);
        this.stage.setTitle("Dungeon MVC");
        this.stage.setScene(scene);

        scene.setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case Z -> northPressed = true;
                case S -> southPressed = true;
                case Q -> westPressed = true;
                case D -> eastPressed = true;
                case E -> {
                    if (onTakeItem != null) {
                        onTakeItem.run();
                    }
                }
                default -> {
                }
            }
        });

        scene.setOnKeyReleased(event -> {
            switch (event.getCode()) {
                case Z -> northPressed = false;
                case S -> southPressed = false;
                case Q -> westPressed = false;
                case D -> eastPressed = false;
                default -> {
                }
            }
        });

        scene.setOnMouseClicked(event -> scene.getRoot().requestFocus());
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
    public void show() {
        Platform.runLater(() -> {
            stage.show();
            scene.getRoot().requestFocus();
            timer.start();
        });
    }

    @Override
    public void hide() {
        Platform.runLater(() -> {
            timer.stop();
            stage.hide();
        });
    }
}