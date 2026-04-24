package mvc.map.view.gui;

import java.util.ArrayList;
import java.util.List;

import common.map.Room;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import mvc.map.ItemPlacement;
import mvc.map.view.base.RoomView;
import common.map.Exit;
import common.map.LockedExit;

public class RoomViewGUI extends RoomView {

    private final BorderPane root = new BorderPane();

    private final Label titleLabel = new Label("Room");
    private final Label exitsLabel = new Label("Exits: none");

    private final TextArea descriptionArea = new TextArea();
    private final TextArea logArea = new TextArea();

    private final Pane mapPane = new Pane();

    private final Button northButton = new Button("North");
    private final Button southButton = new Button("South");
    private final Button eastButton = new Button("East");
    private final Button westButton = new Button("West");

    private final Label helpLabel = new Label("Déplacements : ZQSD | Interagir : E");

    private final VBox infoBox = new VBox(8);
    private final VBox playableBox = new VBox(12);
    private final VBox logBox = new VBox(10);

    private Room currentRoom;
    private double heroX = 230;
    private double heroY = 150;

    private List<ItemPlacement> placedItems = new ArrayList<>();

    public RoomViewGUI() {
        descriptionArea.setEditable(false);
        descriptionArea.setWrapText(true);
        descriptionArea.setPrefRowCount(4);
        descriptionArea.setFocusTraversable(false);
        descriptionArea.setPrefWidth(520);

        logArea.setEditable(false);
        logArea.setWrapText(true);
        logArea.setPrefRowCount(20);
        logArea.setFocusTraversable(false);
        logArea.setPrefWidth(320);
        logArea.setPrefHeight(500);

        mapPane.setPrefSize(460, 320);
        mapPane.setMinSize(460, 320);
        mapPane.setFocusTraversable(false);

        northButton.setFocusTraversable(false);
        southButton.setFocusTraversable(false);
        eastButton.setFocusTraversable(false);
        westButton.setFocusTraversable(false);

        GridPane moves = new GridPane();
        moves.setHgap(8);
        moves.setVgap(8);
        moves.add(northButton, 1, 0);
        moves.add(westButton, 0, 1);
        moves.add(eastButton, 2, 1);
        moves.add(southButton, 1, 2);

        infoBox.getChildren().addAll(titleLabel, exitsLabel, descriptionArea);
        infoBox.setPadding(new Insets(10));
        infoBox.setPrefWidth(560);

        playableBox.getChildren().addAll(mapPane, moves, helpLabel);
        playableBox.setPadding(new Insets(10));

        Label logLabel = new Label("Logs");
        logBox.getChildren().addAll(logLabel, logArea);
        logBox.setPadding(new Insets(10));
        logBox.setPrefWidth(320);

        root.setCenter(playableBox);
        root.setRight(logBox);
        root.setPadding(new Insets(10));
        BorderPane.setMargin(playableBox, new Insets(10));
        BorderPane.setMargin(logBox, new Insets(10));
    }

    private Color getExitColor(String direction) {
        if (currentRoom == null) {
            return Color.GREEN;
        }

        Exit exit = currentRoom.getExit(direction);
        if (exit instanceof LockedExit lockedExit && lockedExit.isLocked()) {
            return Color.RED;
        }

        return Color.LIMEGREEN;
    }

    public VBox getInfoBox() {
        return infoBox;
    }

    public BorderPane getRoot() {
        return root;
    }

    private void log(String msg) {
        Platform.runLater(() -> {
            if (!logArea.getText().isEmpty()) {
                logArea.appendText("\n");
            }
            logArea.appendText(msg);
        });
    }

    private void setDirectionButtonVisible(Button button, boolean visible) {
        button.setVisible(visible);
        button.setManaged(visible);
    }

    private void updateDirectionButtons(Room room) {
        setDirectionButtonVisible(northButton, room.getExit("north") != null);
        setDirectionButtonVisible(southButton, room.getExit("south") != null);
        setDirectionButtonVisible(eastButton, room.getExit("east") != null);
        setDirectionButtonVisible(westButton, room.getExit("west") != null);
    }

    private void drawRoom() {
        if (currentRoom == null) {
            return;
        }

        mapPane.getChildren().clear();

        Rectangle roomRect = new Rectangle(60, 40, 340, 220);
        roomRect.setArcWidth(20);
        roomRect.setArcHeight(20);
        roomRect.setFill(Color.BEIGE);
        roomRect.setStroke(Color.BLACK);
        roomRect.setStrokeWidth(10); // mur noir épais

        Circle hero = new Circle(heroX, heroY, 12, Color.DARKRED);
        Text heroText = new Text(heroX - 18, heroY + 28, "Hero");
        Text roomName = new Text(185, 70, currentRoom.getName());

        mapPane.getChildren().addAll(roomRect, roomName, hero, heroText);
        drawItems();

        // NORTH EXIT
        if (currentRoom.getExit("north") != null) {
            Rectangle northExit = new Rectangle(210, 35, 40, 10);
            northExit.setFill(getExitColor("north"));
            northExit.setStroke(Color.BLACK);

            Text northText = new Text(223, 25, "N");
            mapPane.getChildren().addAll(northExit, northText);
        }

        // SOUTH EXIT
        if (currentRoom.getExit("south") != null) {
            Rectangle southExit = new Rectangle(210, 255, 40, 10);
            southExit.setFill(getExitColor("south"));
            southExit.setStroke(Color.BLACK);

            Text southText = new Text(223, 290, "S");
            mapPane.getChildren().addAll(southExit, southText);
        }

        // WEST EXIT
        if (currentRoom.getExit("west") != null) {
            Rectangle westExit = new Rectangle(55, 140, 10, 40);
            westExit.setFill(getExitColor("west"));
            westExit.setStroke(Color.BLACK);

            Text westText = new Text(25, 155, "W");
            mapPane.getChildren().addAll(westExit, westText);
        }

        // EAST EXIT
        if (currentRoom.getExit("east") != null) {
            Rectangle eastExit = new Rectangle(395, 140, 10, 40);
            eastExit.setFill(getExitColor("east"));
            eastExit.setStroke(Color.BLACK);

            Text eastText = new Text(425, 155, "E");
            mapPane.getChildren().addAll(eastExit, eastText);
        }
    }

    private void drawItems() {
        if (placedItems == null || placedItems.isEmpty()) {
            Text noItemText = new Text(170, 235, "No item in this room");
            mapPane.getChildren().add(noItemText);
            return;
        }

        for (ItemPlacement placement : placedItems) {
            double x = placement.getX();
            double y = placement.getY();

            Circle itemMarker = new Circle(x, y, 7, Color.GOLDENROD);
            Text itemText = new Text(x + 14, y + 4, placement.getItem().getName());

            mapPane.getChildren().addAll(itemMarker, itemText);
        }
    }

    @Override
    public void show() {
        Platform.runLater(() -> {
            root.setVisible(true);
            root.setManaged(true);
        });
    }

    @Override
    public void hide() {
        Platform.runLater(() -> {
            root.setVisible(false);
            root.setManaged(false);
        });
    }

    @Override
    public void displayRoom(Room room) {
        Platform.runLater(() -> {
            currentRoom = room;

            titleLabel.setText("=== " + room.getName().toUpperCase() + " ===");
            descriptionArea.setText(room.getDescription());

            if (room.getExits().isEmpty()) {
                exitsLabel.setText("Exits: none");
            } else {
                exitsLabel.setText("Exits: " + String.join(", ", room.getExits().keySet()));
            }

            updateDirectionButtons(room);
            drawRoom();
        });
    }

    @Override
    public void displayHeroPosition(double x, double y) {
        Platform.runLater(() -> {
            this.heroX = x;
            this.heroY = y;
            drawRoom();
        });
    }

    @Override
    public void displayPlacedItems(List<ItemPlacement> placedItems) {
        Platform.runLater(() -> {
            this.placedItems = new ArrayList<>(placedItems);
            drawRoom();
        });
    }

    @Override
    public void displayMove(String direction, String roomName) {
        log("You go " + direction + " and enter: " + roomName);
    }

    @Override
    public void displayNoExit(String direction) {
        log("There is no exit to the " + direction + ".");
    }

    @Override
    public void displayMessage(String message) {
        log(message);
    }

    @Override
    public void setOnMoveNorth(Runnable action) {
        Platform.runLater(() -> northButton.setOnAction(e -> action.run()));
    }

    @Override
    public void setOnMoveSouth(Runnable action) {
        Platform.runLater(() -> southButton.setOnAction(e -> action.run()));
    }

    @Override
    public void setOnMoveEast(Runnable action) {
        Platform.runLater(() -> eastButton.setOnAction(e -> action.run()));
    }

    @Override
    public void setOnMoveWest(Runnable action) {
        Platform.runLater(() -> westButton.setOnAction(e -> action.run()));
    }
}