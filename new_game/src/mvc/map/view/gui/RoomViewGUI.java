package mvc.map.view.gui;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import common.map.Exit;
import common.map.LockedExit;
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
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import mvc.map.ItemPlacement;
import mvc.map.RoomPlacement;
import mvc.map.view.base.RoomView;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class RoomViewGUI extends RoomView {

    private static final double MAP_WIDTH = 720;
    private static final double MAP_HEIGHT = 520;

    private static final double ROOM_X = 90;
    private static final double ROOM_Y = 70;
    private static final double ROOM_W = 540;
    private static final double ROOM_H = 360;

    private static final int MINI_MAP_SIZE = 3;
    private static final double MINI_CELL_W = 105;
    private static final double MINI_CELL_H = 82;
    private static final double MINI_MARGIN_X = 18;
    private static final double MINI_MARGIN_Y = 18;

    private final BorderPane root = new BorderPane();

    private final Label titleLabel = new Label("Room");
    private final Label exitsLabel = new Label("Exits: none");

    private final TextArea descriptionArea = new TextArea();

    private final Pane mapPane = new Pane();
    private final Pane exploredMapPane = new Pane();

    private final Button northButton = new Button("North");
    private final Button southButton = new Button("South");
    private final Button eastButton = new Button("East");
    private final Button westButton = new Button("West");

    private final Label helpLabel = new Label("Déplacements : ZQSD | Interagir : E");

    private final VBox infoBox = new VBox(8);
    private final VBox playableBox = new VBox(12);
    private final VBox explorationBox = new VBox(10);

    private final Consumer<String> logger;

    private Room currentRoom;
    private double heroX = 360;
    private double heroY = 250;

    private List<ItemPlacement> placedItems = new ArrayList<>();
    private List<RoomPlacement> visitedRooms = new ArrayList<>();
    private int currentGridX = 0;
    private int currentGridY = 0;

    public RoomViewGUI(Consumer<String> logger) {
        this.logger = logger;

        descriptionArea.setEditable(false);
        descriptionArea.setWrapText(true);
        descriptionArea.setPrefRowCount(4);
        descriptionArea.setFocusTraversable(false);
        descriptionArea.setPrefWidth(520);

        mapPane.setPrefSize(MAP_WIDTH, MAP_HEIGHT);
        mapPane.setMinSize(MAP_WIDTH, MAP_HEIGHT);
        mapPane.setFocusTraversable(false);

        exploredMapPane.setPrefSize(360, 300);
        exploredMapPane.setMinSize(360, 300);
        exploredMapPane.setFocusTraversable(false);

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

        Label explorationLabel = new Label("Exploration 3x3");
        explorationBox.getChildren().addAll(explorationLabel, exploredMapPane);
        explorationBox.setPadding(new Insets(10));
        explorationBox.setPrefWidth(380);

        root.setCenter(playableBox);
        root.setRight(explorationBox);
        root.setPadding(new Insets(10));
        BorderPane.setMargin(playableBox, new Insets(10));
        BorderPane.setMargin(explorationBox, new Insets(10));
    }

    private Color getExitColor(String direction) {
        if (currentRoom == null) {
            return Color.LIMEGREEN;
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
        if (logger != null) {
            logger.accept(msg);
        }
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

        Rectangle roomRect = new Rectangle(ROOM_X, ROOM_Y, ROOM_W, ROOM_H);
        roomRect.setArcWidth(20);
        roomRect.setArcHeight(20);
        roomRect.setFill(Color.BEIGE);
        roomRect.setStroke(Color.BLACK);
        roomRect.setStrokeWidth(10);

        Circle hero = new Circle(heroX, heroY, 12, Color.DARKRED);
        Text heroText = new Text(heroX - 18, heroY + 28, "Hero");
        Text roomName = new Text(330, 105, currentRoom.getName());

        mapPane.getChildren().addAll(roomRect, roomName, hero, heroText);
        drawItems();

        if (currentRoom.getExit("north") != null) {
            Rectangle northExit = new Rectangle(340, 65, 40, 10);
            northExit.setFill(getExitColor("north"));
            northExit.setStroke(Color.BLACK);
            Text northText = new Text(353, 55, "N");
            mapPane.getChildren().addAll(northExit, northText);
        }

        if (currentRoom.getExit("south") != null) {
            Rectangle southExit = new Rectangle(340, 425, 40, 10);
            southExit.setFill(getExitColor("south"));
            southExit.setStroke(Color.BLACK);
            Text southText = new Text(353, 460, "S");
            mapPane.getChildren().addAll(southExit, southText);
        }

        if (currentRoom.getExit("west") != null) {
            Rectangle westExit = new Rectangle(85, 230, 10, 40);
            westExit.setFill(getExitColor("west"));
            westExit.setStroke(Color.BLACK);
            Text westText = new Text(55, 250, "W");
            mapPane.getChildren().addAll(westExit, westText);
        }

        if (currentRoom.getExit("east") != null) {
            Rectangle eastExit = new Rectangle(625, 230, 10, 40);
            eastExit.setFill(getExitColor("east"));
            eastExit.setStroke(Color.BLACK);
            Text eastText = new Text(665, 250, "E");
            mapPane.getChildren().addAll(eastExit, eastText);
        }
    }

    private void drawItems() {
        if (placedItems == null || placedItems.isEmpty()) {
            Text noItemText = new Text(300, 395, "No item in this room");
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

    private void drawVisitedRooms() {
        exploredMapPane.getChildren().clear();

        int pageStartX = getPageStart(currentGridX);
        int pageStartY = getPageStart(currentGridY);

        for (int row = 0; row < MINI_MAP_SIZE; row++) {
            for (int col = 0; col < MINI_MAP_SIZE; col++) {
                Rectangle slot = new Rectangle(
                        MINI_MARGIN_X + col * MINI_CELL_W,
                        MINI_MARGIN_Y + row * MINI_CELL_H,
                        MINI_CELL_W - 12,
                        MINI_CELL_H - 12);
                slot.setFill(Color.web("#f2f2f2"));
                slot.setStroke(Color.LIGHTGRAY);
                exploredMapPane.getChildren().add(slot);
            }
        }

        List<RoomPlacement> visibleRooms = new ArrayList<>();
        Set<Room> allVisitedRooms = new HashSet<>();
        Set<String> occupiedCells = new HashSet<>();

        for (RoomPlacement placement : visitedRooms) {
            allVisitedRooms.add(placement.getRoom());

            if (placement.getGridX() >= pageStartX
                    && placement.getGridX() < pageStartX + MINI_MAP_SIZE
                    && placement.getGridY() >= pageStartY
                    && placement.getGridY() < pageStartY + MINI_MAP_SIZE) {
                visibleRooms.add(placement);
                occupiedCells.add(placement.getGridX() + ":" + placement.getGridY());
            }
        }

        Map<String, Color> unknownCells = new HashMap<>();

        for (RoomPlacement placement : visibleRooms) {
            Room room = placement.getRoom();

            for (Map.Entry<String, Exit> entry : room.getExits().entrySet()) {
                String direction = entry.getKey();
                Exit exit = entry.getValue();

                int nx = placement.getGridX();
                int ny = placement.getGridY();

                switch (direction) {
                    case "north" -> ny--;
                    case "south" -> ny++;
                    case "east" -> nx++;
                    case "west" -> nx--;
                    default -> {
                        continue;
                    }
                }

                if (nx < pageStartX || nx >= pageStartX + MINI_MAP_SIZE
                        || ny < pageStartY || ny >= pageStartY + MINI_MAP_SIZE) {
                    continue;
                }

                if (allVisitedRooms.contains(exit.getTarget())) {
                    continue;
                }

                String cellKey = nx + ":" + ny;
                if (!occupiedCells.contains(cellKey)) {
                    Color markerColor = isLockedExit(exit) ? Color.RED : Color.DARKGOLDENROD;
                    unknownCells.putIfAbsent(cellKey, markerColor);
                }
            }
        }

        for (int i = 0; i < visibleRooms.size(); i++) {
            RoomPlacement a = visibleRooms.get(i);

            for (int j = i + 1; j < visibleRooms.size(); j++) {
                RoomPlacement b = visibleRooms.get(j);

                int deltaX = b.getGridX() - a.getGridX();
                int deltaY = b.getGridY() - a.getGridY();

                Color connectionColor = null;

                if (deltaX == 1 && deltaY == 0) {
                    connectionColor = getConnectionColor(a.getRoom(), b.getRoom(), "east", "west");
                } else if (deltaX == -1 && deltaY == 0) {
                    connectionColor = getConnectionColor(a.getRoom(), b.getRoom(), "west", "east");
                } else if (deltaX == 0 && deltaY == 1) {
                    connectionColor = getConnectionColor(a.getRoom(), b.getRoom(), "south", "north");
                } else if (deltaX == 0 && deltaY == -1) {
                    connectionColor = getConnectionColor(a.getRoom(), b.getRoom(), "north", "south");
                }

                if (connectionColor == null) {
                    continue;
                }

                int aCol = a.getGridX() - pageStartX;
                int aRow = a.getGridY() - pageStartY;
                int bCol = b.getGridX() - pageStartX;
                int bRow = b.getGridY() - pageStartY;

                double x1 = MINI_MARGIN_X + aCol * MINI_CELL_W + (MINI_CELL_W - 12) / 2.0;
                double y1 = MINI_MARGIN_Y + aRow * MINI_CELL_H + (MINI_CELL_H - 12) / 2.0;
                double x2 = MINI_MARGIN_X + bCol * MINI_CELL_W + (MINI_CELL_W - 12) / 2.0;
                double y2 = MINI_MARGIN_Y + bRow * MINI_CELL_H + (MINI_CELL_H - 12) / 2.0;

                Line link = new Line(x1, y1, x2, y2);
                link.setStroke(connectionColor);
                link.setStrokeWidth(3);
                exploredMapPane.getChildren().add(link);
            }
        }

        for (Map.Entry<String, Color> entry : unknownCells.entrySet()) {
            String[] parts = entry.getKey().split(":");
            int gx = Integer.parseInt(parts[0]);
            int gy = Integer.parseInt(parts[1]);

            int col = gx - pageStartX;
            int row = gy - pageStartY;

            double x = MINI_MARGIN_X + col * MINI_CELL_W;
            double y = MINI_MARGIN_Y + row * MINI_CELL_H;

            Rectangle unknownRect = new Rectangle(x, y, MINI_CELL_W - 12, MINI_CELL_H - 12);
            unknownRect.setFill(Color.web("#fff8dc"));
            unknownRect.setStroke(entry.getValue());
            unknownRect.setStrokeWidth(2);
            unknownRect.getStrokeDashArray().addAll(6.0, 4.0);

            Text questionMark = new Text(x + 42, y + 42, "?");

            exploredMapPane.getChildren().addAll(unknownRect, questionMark);
        }

        for (RoomPlacement placement : visibleRooms) {
            int col = placement.getGridX() - pageStartX;
            int row = placement.getGridY() - pageStartY;

            double x = MINI_MARGIN_X + col * MINI_CELL_W;
            double y = MINI_MARGIN_Y + row * MINI_CELL_H;

            Rectangle roomRect = new Rectangle(x, y, MINI_CELL_W - 12, MINI_CELL_H - 12);
            boolean isCurrent = placement.getGridX() == currentGridX && placement.getGridY() == currentGridY;

            roomRect.setFill(isCurrent ? Color.LIGHTBLUE : Color.WHITE);
            roomRect.setStroke(isCurrent ? Color.DODGERBLUE : Color.BLACK);
            roomRect.setStrokeWidth(isCurrent ? 3 : 2);

            String roomName = placement.getRoom().getName();
            if (roomName.length() > 12) {
                roomName = roomName.substring(0, 12) + ".";
            }

            Text nameText = new Text(x + 10, y + 28, roomName);
            Text coordText = new Text(x + 10, y + 48,
                    "(" + placement.getGridX() + "," + placement.getGridY() + ")");

            exploredMapPane.getChildren().addAll(roomRect, nameText, coordText);
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
            heroX = x;
            heroY = y;
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
    public void displayVisitedRooms(List<RoomPlacement> visitedRooms, int currentGridX, int currentGridY) {
        Platform.runLater(() -> {
            this.visitedRooms = new ArrayList<>(visitedRooms);
            this.currentGridX = currentGridX;
            this.currentGridY = currentGridY;
            drawVisitedRooms();
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

    private int getPageStart(int current) {
        // Premier bloc centré sur l'origine : [-1, 0, 1]
        if (current >= -1 && current <= 1) {
            return -1;
        }

        // Blocs à droite : [2..4], [5..7], [8..10], ...
        if (current >= 2) {
            return 2 + ((current - 2) / MINI_MAP_SIZE) * MINI_MAP_SIZE;
        }

        // Blocs à gauche : [-4..-2], [-7..-5], [-10..-8], ...
        return -4 - (((-current) - 2) / MINI_MAP_SIZE) * MINI_MAP_SIZE;
    }

    private Exit getExitBetween(Room from, Room to, String direction) {
        if (from == null || to == null) {
            return null;
        }

        Exit exit = from.getExit(direction);
        if (exit == null) {
            return null;
        }

        return exit.getTarget() == to ? exit : null;
    }

    private boolean isLockedExit(Exit exit) {
        return exit instanceof LockedExit lockedExit && lockedExit.isLocked();
    }

    private Color getConnectionColor(Room a, Room b, String dirAB, String dirBA) {
        Exit ab = getExitBetween(a, b, dirAB);
        Exit ba = getExitBetween(b, a, dirBA);

        if (ab == null && ba == null) {
            return null;
        }

        if (isLockedExit(ab) || isLockedExit(ba)) {
            return Color.RED;
        }

        return Color.LIMEGREEN;
    }

}