package mvc.map.view.gui;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.function.Consumer;

import common.language.Language;
import common.map.Exit;
import common.map.LockedExit;
import common.map.Room;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import mvc.GameConfig;
import mvc.map.view.base.RoomView;
import javafx.scene.layout.Region;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import common.item.Item;
import mvc.map.MapLayout;
import mvc.map.model.RoomPlacement;
import mvc.entity.model.EnemySnapshot;
import mvc.entity.model.ProjectileSnapshot;

public class RoomViewGUI extends RoomView {

    private static final double MAP_WIDTH = MapLayout.MAP_WIDTH;
    private static final double MAP_HEIGHT = MapLayout.MAP_HEIGHT;

    private static final double ROOM_X = MapLayout.ROOM_X;
    private static final double ROOM_Y = MapLayout.ROOM_Y;
    private static final double ROOM_W = MapLayout.ROOM_W;
    private static final double ROOM_H = MapLayout.ROOM_H;

    private static final int MINI_MAP_SIZE = 3;
    private static final double MINI_CELL_W = 62;
    private static final double MINI_CELL_H = 50;
    private static final double MINI_MARGIN_X = 10;
    private static final double MINI_MARGIN_Y = 10;

    private final VBox miniMapBox = new VBox(8);
    private final VBox helpBox = new VBox(8);
    private final StackPane gameAreaBox = new StackPane();

    private final BorderPane root = new BorderPane();

    private final Label titleLabel = new Label("");
    private final Label exitsLabel = new Label(Language.t("map.exitsNone"));

    private final Label descriptionArea = new Label();

    private final Pane mapPane = new Pane();
    private final Pane exploredMapPane = new Pane();

    private final Label helpLabel = new Label(GameConfig.getControlHelpText());

    private final VBox infoBox = new VBox(6);

    private final Pane enemiesLayer = new Pane();
    private final Pane projectilesLayer = new Pane();

    private Line heroFacingNode;
    private double heroFacingX = 0;
    private double heroFacingY = -1;

    private final Consumer<String> logger;

    private Room currentRoom;
    private double heroX = 360;
    private double heroY = 250;

    private List<RoomPlacement> visitedRooms = new ArrayList<>();
    private int currentGridX = 0;
    private int currentGridY = 0;

    private Circle heroNode;
    private Text heroTextNode;

    private Circle secondHeroNode;
    private Text secondHeroTextNode;
    private Line secondHeroFacingNode;

    private double secondHeroX = MapLayout.getRoomCenterX() + 55;
    private double secondHeroY = MapLayout.getRoomCenterY();

    private double secondHeroFacingX = 0;
    private double secondHeroFacingY = -1;

    private static final long HERO_ATTACK_FLASH_NS = 160_000_000L;
    private long heroAttackFlashUntil = 0L;

    private long secondHeroAttackFlashUntil = 0L;

    public RoomViewGUI(Consumer<String> logger) {
        this.logger = logger;

        titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
        exitsLabel.setStyle("-fx-font-size: 13px;");

        mapPane.setPrefSize(MAP_WIDTH, MAP_HEIGHT);
        mapPane.setMinSize(MAP_WIDTH, MAP_HEIGHT);
        mapPane.setFocusTraversable(false);

        exploredMapPane.setPrefSize(210, 170);
        exploredMapPane.setMinSize(210, 170);
        exploredMapPane.setMaxSize(210, 170);
        exploredMapPane.setFocusTraversable(false);

        descriptionArea.setWrapText(true);
        descriptionArea.setFocusTraversable(false);
        descriptionArea.setPrefWidth(520);

        descriptionArea.setMinHeight(Region.USE_PREF_SIZE);
        descriptionArea.setPrefHeight(40);
        descriptionArea.setMaxHeight(40);

        infoBox.getChildren().addAll(titleLabel, exitsLabel, descriptionArea);
        infoBox.setPadding(new Insets(4, 8, 4, 8));
        infoBox.setMaxHeight(Region.USE_PREF_SIZE);

        Label explorationLabel = new Label(Language.t("map.minimap"));
        miniMapBox.getChildren().addAll(explorationLabel, exploredMapPane);
        miniMapBox.setAlignment(Pos.CENTER);
        miniMapBox.setPadding(new Insets(8));

        helpBox.getChildren().add(helpLabel);
        helpBox.setAlignment(Pos.CENTER);
        helpBox.setPadding(new Insets(8));

        gameAreaBox.getChildren().add(mapPane);
        gameAreaBox.setPrefSize(MAP_WIDTH, MAP_HEIGHT);
        gameAreaBox.setMinSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        gameAreaBox.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
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

    public StackPane getGameArea() {
        return gameAreaBox;
    }

    private void log(String msg) {
        if (logger != null) {
            logger.accept(msg);
        }
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

        heroFacingNode = new Line(
                heroX,
                heroY,
                heroX + heroFacingX * 38,
                heroY + heroFacingY * 38);
        heroFacingNode.setStroke(isHeroAttackFlashing() ? Color.GOLD : Color.BLACK);
        heroFacingNode.setStrokeWidth(3);

        heroNode = new Circle(heroX, heroY, MapLayout.HERO_RADIUS, Color.DARKRED);
        heroTextNode = new Text(heroX - 18, heroY + 28, Language.t("map.heroLabel1"));

        if (GameConfig.isTwoPlayers()) {
            secondHeroFacingNode = new Line(
                    secondHeroX,
                    secondHeroY,
                    secondHeroX + secondHeroFacingX * 38,
                    secondHeroY + secondHeroFacingY * 38);
            secondHeroFacingNode.setStroke(Color.BLACK);
            secondHeroFacingNode.setStrokeWidth(3);

            secondHeroNode = new Circle(secondHeroX, secondHeroY, MapLayout.HERO_RADIUS, Color.DARKBLUE);
            secondHeroTextNode = new Text(secondHeroX - 18, secondHeroY + 28, Language.t("map.heroLabel2"));
        } else {
            secondHeroFacingNode = null;
            secondHeroNode = null;
            secondHeroTextNode = null;
        }

        Text roomName = new Text(getRoomCenterX() - 40, ROOM_Y + 18, currentRoom.getName());

        mapPane.getChildren().addAll(roomRect, roomName);
        drawItems();

        mapPane.getChildren().addAll(
                enemiesLayer,
                projectilesLayer,
                heroFacingNode,
                heroNode,
                heroTextNode);

        if (GameConfig.isTwoPlayers()) {
            mapPane.getChildren().addAll(
                    secondHeroFacingNode,
                    secondHeroNode,
                    secondHeroTextNode);
        }

        double horizontalExitW = Math.max(40, ROOM_W * 0.08);
        double horizontalExitH = 10;

        double verticalExitW = 10;
        double verticalExitH = Math.max(40, ROOM_H * 0.08);

        if (currentRoom.getExit("north") != null) {
            double x = getNorthExitX(horizontalExitW);
            double y = getNorthExitY(horizontalExitH);

            Rectangle northExit = new Rectangle(x, y, horizontalExitW, horizontalExitH);
            northExit.setFill(getExitColor("north"));
            northExit.setStroke(Color.BLACK);

            Text northText = new Text(x + horizontalExitW / 2.0 - 4, y - 8, Language.dir("north").substring(0, 1).toUpperCase());
            mapPane.getChildren().addAll(northExit, northText);
        }

        if (currentRoom.getExit("south") != null) {
            double x = getSouthExitX(horizontalExitW);
            double y = getSouthExitY(horizontalExitH);

            Rectangle southExit = new Rectangle(x, y, horizontalExitW, horizontalExitH);
            southExit.setFill(getExitColor("south"));
            southExit.setStroke(Color.BLACK);

            Text southText = new Text(x + horizontalExitW / 2.0 - 4, y + 35, Language.dir("south").substring(0, 1).toUpperCase());
            mapPane.getChildren().addAll(southExit, southText);
        }

        if (currentRoom.getExit("west") != null) {
            double x = getWestExitX(verticalExitW);
            double y = getWestExitY(verticalExitH);

            Rectangle westExit = new Rectangle(x, y, verticalExitW, verticalExitH);
            westExit.setFill(getExitColor("west"));
            westExit.setStroke(Color.BLACK);

            Text westText = new Text(x - 28, y + verticalExitH / 2.0 + 4, Language.dir("west").substring(0, 1).toUpperCase());
            mapPane.getChildren().addAll(westExit, westText);
        }

        if (currentRoom.getExit("east") != null) {
            double x = getEastExitX(verticalExitW);
            double y = getEastExitY(verticalExitH);

            Rectangle eastExit = new Rectangle(x, y, verticalExitW, verticalExitH);
            eastExit.setFill(getExitColor("east"));
            eastExit.setStroke(Color.BLACK);

            Text eastText = new Text(x + 20, y + verticalExitH / 2.0 + 4, Language.dir("east").substring(0, 1).toUpperCase());
            mapPane.getChildren().addAll(eastExit, eastText);
        }
    }

    private void drawItems() {
        if (currentRoom == null) {
            return;
        }

        List<Item> items = currentRoom.getItems();

        if (items == null || items.isEmpty()) {
            Text noItemText = new Text(getRoomCenterX() - 55, ROOM_Y + ROOM_H - 35, Language.t("map.noItems"));
            mapPane.getChildren().add(noItemText);
            return;
        }

        for (int i = 0; i < items.size(); i++) {
            Item item = items.get(i);

            double x = MapLayout.getItemX(i);
            double y = MapLayout.getItemY(i);

            Circle itemMarker = new Circle(x, y, MapLayout.ITEM_RADIUS, Color.GOLDENROD);
            Text itemText = new Text(x + 14, y + 4, item.getName());

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
                        MINI_CELL_W - 8,
                        MINI_CELL_H - 8);
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

                double x1 = MINI_MARGIN_X + aCol * MINI_CELL_W + (MINI_CELL_W - 8) / 2.0;
                double y1 = MINI_MARGIN_Y + aRow * MINI_CELL_H + (MINI_CELL_H - 8) / 2.0;
                double x2 = MINI_MARGIN_X + bCol * MINI_CELL_W + (MINI_CELL_W - 8) / 2.0;
                double y2 = MINI_MARGIN_Y + bRow * MINI_CELL_H + (MINI_CELL_H - 8) / 2.0;

                Line link = new Line(x1, y1, x2, y2);
                link.setStroke(connectionColor);
                link.setStrokeWidth(2);
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

            Rectangle unknownRect = new Rectangle(x, y, MINI_CELL_W - 8, MINI_CELL_H - 8);
            unknownRect.setFill(Color.web("#fff8dc"));
            unknownRect.setStroke(entry.getValue());
            unknownRect.setStrokeWidth(1.5);
            unknownRect.getStrokeDashArray().addAll(4.0, 3.0);

            Text questionMark = new Text(x + 24, y + 27, "?");

            exploredMapPane.getChildren().addAll(unknownRect, questionMark);
        }

        for (RoomPlacement placement : visibleRooms) {
            int col = placement.getGridX() - pageStartX;
            int row = placement.getGridY() - pageStartY;

            double x = MINI_MARGIN_X + col * MINI_CELL_W;
            double y = MINI_MARGIN_Y + row * MINI_CELL_H;

            Rectangle roomRect = new Rectangle(x, y, MINI_CELL_W - 8, MINI_CELL_H - 8);
            boolean isCurrent = placement.getGridX() == currentGridX && placement.getGridY() == currentGridY;

            roomRect.setFill(isCurrent ? Color.LIGHTBLUE : Color.WHITE);
            roomRect.setStroke(isCurrent ? Color.DODGERBLUE : Color.BLACK);
            roomRect.setStrokeWidth(isCurrent ? 2.5 : 1.5);

            exploredMapPane.getChildren().add(roomRect);
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

            titleLabel.setText(room.getName());
            descriptionArea.setText(room.getDescription());
            if (room.getExits().isEmpty()) {
                exitsLabel.setText(Language.t("map.exitsNone"));
            } else {
                String translatedExits = room.getExits().keySet().stream().map(Language::dir).collect(Collectors.joining(", "));
                exitsLabel.setText(Language.tf("map.exits", translatedExits));
            }

            drawRoom();
        });
    }

    @Override
    public void displayHeroPosition(double x, double y) {
        Platform.runLater(() -> {
            heroX = x;
            heroY = y;
            updateHeroNode();
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
        log(Language.tf("cli.youGo", Language.dir(direction), roomName));
    }

    @Override
    public void displayNoExit(String direction) {
        log(Language.tf("cli.noExit", Language.dir(direction)));
    }

    @Override
    public void displayMessage(String message) {
        log(message);
    }

    @Override
    public void setOnMoveNorth(Runnable action) {
    }

    @Override
    public void setOnMoveSouth(Runnable action) {
    }

    @Override
    public void setOnMoveEast(Runnable action) {
    }

    @Override
    public void setOnMoveWest(Runnable action) {
    }

    private int getPageStart(int current) {
        if (current >= -1 && current <= 1) {
            return -1;
        }

        if (current >= 2) {
            return 2 + ((current - 2) / MINI_MAP_SIZE) * MINI_MAP_SIZE;
        }

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

    public VBox getMiniMapBox() {
        return miniMapBox;
    }

    public VBox getHelpBox() {
        return helpBox;
    }

    public StackPane getGameAreaBox() {
        return gameAreaBox;
    }

    private double getRoomCenterX() {
        return ROOM_X + ROOM_W / 2.0;
    }

    private double getRoomCenterY() {
        return ROOM_Y + ROOM_H / 2.0;
    }

    private double getNorthExitX(double exitWidth) {
        return ROOM_X + (ROOM_W - exitWidth) / 2.0;
    }

    private double getNorthExitY(double exitHeight) {
        return ROOM_Y - exitHeight / 2.0;
    }

    private double getSouthExitX(double exitWidth) {
        return ROOM_X + (ROOM_W - exitWidth) / 2.0;
    }

    private double getSouthExitY(double exitHeight) {
        return ROOM_Y + ROOM_H - exitHeight / 2.0;
    }

    private double getWestExitX(double exitWidth) {
        return ROOM_X - exitWidth / 2.0;
    }

    private double getWestExitY(double exitHeight) {
        return ROOM_Y + (ROOM_H - exitHeight) / 2.0;
    }

    private double getEastExitX(double exitWidth) {
        return ROOM_X + ROOM_W - exitWidth / 2.0;
    }

    private double getEastExitY(double exitHeight) {
        return ROOM_Y + (ROOM_H - exitHeight) / 2.0;
    }

    private void updateHeroNode() {
        if (heroNode == null || heroTextNode == null) {
            return;
        }

        heroNode.setCenterX(heroX);
        heroNode.setCenterY(heroY);

        heroTextNode.setX(heroX - 18);
        heroTextNode.setY(heroY + 28);

        if (heroFacingNode != null) {
            heroFacingNode.setStartX(heroX);
            heroFacingNode.setStartY(heroY);
            heroFacingNode.setEndX(heroX + heroFacingX * 38);
            heroFacingNode.setEndY(heroY + heroFacingY * 38);
            heroFacingNode.setStroke(isHeroAttackFlashing() ? Color.GOLD : Color.BLACK);
        }
    }

    @Override
    public void displayHeroAttackFlash() {
        Platform.runLater(() -> {
            heroAttackFlashUntil = System.nanoTime() + HERO_ATTACK_FLASH_NS;
            updateHeroNode();
        });
    }

    @Override
    public void displaySecondHeroPosition(double x, double y) {
        Platform.runLater(() -> {
            secondHeroX = x;
            secondHeroY = y;
            updateSecondHeroNode();
        });
    }

    private void updateSecondHeroNode() {
        if (secondHeroNode == null || secondHeroTextNode == null) {
            return;
        }

        secondHeroNode.setCenterX(secondHeroX);
        secondHeroNode.setCenterY(secondHeroY);

        secondHeroTextNode.setX(secondHeroX - 18);
        secondHeroTextNode.setY(secondHeroY + 28);

        if (secondHeroFacingNode != null) {
            secondHeroFacingNode.setStartX(secondHeroX);
            secondHeroFacingNode.setStartY(secondHeroY);
            secondHeroFacingNode.setEndX(secondHeroX + secondHeroFacingX * 38);
            secondHeroFacingNode.setEndY(secondHeroY + secondHeroFacingY * 38);
            secondHeroFacingNode.setStroke(isSecondHeroAttackFlashing() ? Color.GOLD : Color.BLACK);
        }
    }

    @Override
    public void displaySecondHeroFacing(double dx, double dy) {
        Platform.runLater(() -> {
            double length = Math.hypot(dx, dy);

            if (length <= 0.001) {
                return;
            }

            secondHeroFacingX = dx / length;
            secondHeroFacingY = dy / length;

            updateSecondHeroNode();
        });
    }

    @Override
    public void displaySecondHeroAttackFlash() {
        Platform.runLater(() -> {
            secondHeroAttackFlashUntil = System.nanoTime() + HERO_ATTACK_FLASH_NS;
            updateSecondHeroNode();
        });
    }

    private boolean isSecondHeroAttackFlashing() {
        return System.nanoTime() < secondHeroAttackFlashUntil;
    }

    @Override
    public void displayHeroFacing(double dx, double dy) {
        Platform.runLater(() -> {
            double length = Math.hypot(dx, dy);

            if (length <= 0.001) {
                return;
            }

            heroFacingX = dx / length;
            heroFacingY = dy / length;

            updateHeroNode();
        });
    }

    @Override
    public void displayEnemies(List<EnemySnapshot> enemies) {
        Platform.runLater(() -> {
            enemiesLayer.getChildren().clear();

            if (enemies == null) {
                return;
            }

            for (EnemySnapshot enemy : enemies) {
                Color color;

                if (enemy.isHighlighted()) {
                    color = Color.GOLD;
                } else if (enemy.isKeyHolder()) {
                    color = Color.HOTPINK;
                } else if ("archer".equals(enemy.getKind())) {
                    color = Color.FORESTGREEN;
                } else {
                    color = Color.DARKORANGE;
                }
                Circle body = new Circle(enemy.getX(), enemy.getY(), 18, color);
                body.setStroke(Color.BLACK);

                Line facing = new Line(
                        enemy.getX(),
                        enemy.getY(),
                        enemy.getX() + enemy.getFacingX() * 32,
                        enemy.getY() + enemy.getFacingY() * 32);
                facing.setStroke(Color.BLACK);
                facing.setStrokeWidth(2);

                Text label = new Text(
                        enemy.getX() - 28,
                        enemy.getY() + 34,
                        enemy.getName() + " " + enemy.getHp() + "/" + enemy.getMaxHp());

                enemiesLayer.getChildren().addAll(facing, body, label);
            }

            updateHeroNode();
        });
    }

    @Override
    public void displayProjectiles(List<ProjectileSnapshot> projectiles) {
        Platform.runLater(() -> {
            projectilesLayer.getChildren().clear();

            if (projectiles == null) {
                return;
            }

            for (ProjectileSnapshot projectile : projectiles) {
                Color projectileColor = projectile.isFromHero() ? Color.SADDLEBROWN : Color.DARKBLUE;
                Circle shot = new Circle(projectile.getX(), projectile.getY(), 6, projectileColor);
                shot.setStroke(Color.BLACK);
                projectilesLayer.getChildren().add(shot);
            }
        });
    }

    private boolean isHeroAttackFlashing() {
        return System.nanoTime() < heroAttackFlashUntil;
    }
}