package mvc.map.controller;

import java.util.List;

import common.item.Chest;
import common.item.Item;
import common.map.Exit;
import common.map.Room;
import mvc.entity.model.HeroModel;
import mvc.item.controller.ItemController;
import mvc.item.model.ItemModel;
import mvc.item.view.cli.ItemViewCLI;
import mvc.item.view.gui.ItemViewLogGUI;
import mvc.map.model.ExitModel;
import mvc.map.model.RoomModel;
import mvc.map.view.base.RoomView;
import mvc.map.view.cli.ExitViewCLI;
import mvc.map.view.gui.ExitViewGUI;
import mvc.map.MapLayout;
import mvc.mvc.Controller;

public class RoomController extends Controller {

    private static final double STEP = 2;

    private static final double ROOM_X = MapLayout.ROOM_X;
    private static final double ROOM_Y = MapLayout.ROOM_Y;
    private static final double ROOM_W = MapLayout.ROOM_W;
    private static final double ROOM_H = MapLayout.ROOM_H;

    private static final double HERO_RADIUS = MapLayout.HERO_RADIUS;

    private static final double MIN_X = ROOM_X + HERO_RADIUS;
    private static final double MAX_X = ROOM_X + ROOM_W - HERO_RADIUS;
    private static final double MIN_Y = ROOM_Y + HERO_RADIUS;
    private static final double MAX_Y = ROOM_Y + ROOM_H - HERO_RADIUS;

    private static final double INTERACT_DISTANCE = 35.0;

    private static final int ITEM_COLUMNS = 3;

    private final RoomModel roomModel;
    private final HeroModel heroModel;
    private final RoomView viewCLI;
    private final RoomView viewGUI;

    private static final long ROOM_TRANSITION_COOLDOWN_NS = 180_000_000L;

    private long movementLockedUntil = 0L;

    public RoomController(RoomModel roomModel, HeroModel heroModel, RoomView viewCLI, RoomView viewGUI) {
        super(roomModel, viewCLI, viewGUI);

        this.roomModel = roomModel;
        this.heroModel = heroModel;
        this.viewCLI = viewCLI;
        this.viewGUI = viewGUI;
    }

    public void onEnterRoom() {
        Room currentRoom = roomModel.getRoom();

        if (currentRoom == null) {
            return;
        }

        viewCLI.displayRoom(currentRoom);

        viewGUI.displayHeroPosition(heroModel.getX(), heroModel.getY());

        viewGUI.displayVisitedRooms(
                roomModel.getVisitedRoomPlacements(),
                roomModel.getCurrentGridX(),
                roomModel.getCurrentGridY());

        viewGUI.displayRoom(currentRoom);
    }

    public void heroMove(String direction) {
        if (System.nanoTime() < movementLockedUntil) {
            return;
        }

        Room currentRoom = roomModel.getRoom();

        if (currentRoom == null) {
            return;
        }

        double dx = 0;
        double dy = 0;

        switch (direction) {
            case "north" -> dy = -STEP;
            case "south" -> dy = STEP;
            case "east" -> dx = STEP;
            case "west" -> dx = -STEP;
            default -> {
                return;
            }
        }

        double nextX = heroModel.getX() + dx;
        double nextY = heroModel.getY() + dy;

        if (nextX < MIN_X) {
            if (currentRoom.getExit("west") != null) {
                crossExit("west");
                return;
            }

            nextX = MIN_X;
        }

        if (nextX > MAX_X) {
            if (currentRoom.getExit("east") != null) {
                crossExit("east");
                return;
            }

            nextX = MAX_X;
        }

        if (nextY < MIN_Y) {
            if (currentRoom.getExit("north") != null) {
                crossExit("north");
                return;
            }

            nextY = MIN_Y;
        }

        if (nextY > MAX_Y) {
            if (currentRoom.getExit("south") != null) {
                crossExit("south");
                return;
            }

            nextY = MAX_Y;
        }

        heroModel.setPosition(nextX, nextY);
        viewGUI.displayHeroPosition(nextX, nextY);
    }

    private void crossExit(String direction) {
        Room currentRoom = roomModel.getRoom();

        if (currentRoom == null) {
            return;
        }

        Exit exit = currentRoom.getExit(direction);

        if (exit == null) {
            viewCLI.displayNoExit(direction);
            viewGUI.displayNoExit(direction);
            return;
        }

        ExitController exitController = new ExitController(
                new ExitModel(exit),
                new ExitViewCLI(viewCLI::displayMessage),
                new ExitViewGUI(viewGUI::displayMessage));

        exitController.onUnlock(heroModel.getHero());

        Room target = exitController.onCross(heroModel.getHero());

        if (target == null) {
            return;
        }

        heroModel.setRoom(target);
        roomModel.moveTo(target, direction);
        heroModel.placeAfterCrossing(direction);

        movementLockedUntil = System.nanoTime() + ROOM_TRANSITION_COOLDOWN_NS;

        viewCLI.displayMove(direction, target.getName());
        viewGUI.displayMove(direction, target.getName());

        onEnterRoom();
    }

    public void goTo(String direction) {
        Room currentRoom = roomModel.getRoom();

        if (currentRoom == null) {
            return;
        }

        Exit exit = currentRoom.getExit(direction);

        if (exit == null) {
            viewCLI.displayNoExit(direction);
            viewGUI.displayNoExit(direction);
            return;
        }

        crossExit(direction);
    }

    public void interactNearby() {
        Room currentRoom = roomModel.getRoom();

        if (currentRoom == null) {
            return;
        }

        List<Item> items = currentRoom.getItems();

        if (items.isEmpty()) {
            viewCLI.displayMessage("There is nothing to interact with.");
            viewGUI.displayMessage("There is nothing to interact with.");
            return;
        }

        NearestItem nearest = findNearestItem(items);

        if (nearest == null) {
            viewCLI.displayMessage("There is nothing to interact with.");
            viewGUI.displayMessage("There is nothing to interact with.");
            return;
        }

        if (nearest.distance > INTERACT_DISTANCE) {
            viewCLI.displayMessage("Nothing nearby to interact with.");
            viewGUI.displayMessage("Nothing nearby to interact with.");
            return;
        }

        interactWithItem(nearest.item);
    }

    public void interactCli() {
        Room currentRoom = roomModel.getRoom();

        if (currentRoom == null) {
            return;
        }

        Item candidate = null;

        for (Item item : currentRoom.getItems()) {
            if (item.canBeTaken()) {
                candidate = item;
                break;
            }
        }

        if (candidate == null) {
            for (Item item : currentRoom.getItems()) {
                if (item instanceof Chest) {
                    candidate = item;
                    break;
                }
            }
        }

        if (candidate == null) {
            for (Item item : currentRoom.getItems()) {
                if (item.canBeUsed()) {
                    candidate = item;
                    break;
                }
            }
        }

        if (candidate == null) {
            viewCLI.displayMessage("There is nothing to interact with.");
            return;
        }

        interactWithItem(candidate);
    }

    private NearestItem findNearestItem(List<Item> items) {
        Item nearestItem = null;
        double bestDistance = Double.MAX_VALUE;

        for (int i = 0; i < items.size(); i++) {
            double itemX = getItemX(i);
            double itemY = getItemY(i);

            double dx = heroModel.getX() - itemX;
            double dy = heroModel.getY() - itemY;

            double distance = Math.hypot(dx, dy);

            if (distance < bestDistance) {
                bestDistance = distance;
                nearestItem = items.get(i);
            }
        }

        if (nearestItem == null) {
            return null;
        }

        return new NearestItem(nearestItem, bestDistance);
    }

    private double getItemX(int index) {
        int col = index % ITEM_COLUMNS;

        double startX = ROOM_X + ROOM_W * 0.18;
        double gapX = ROOM_W * 0.22;

        return startX + col * gapX;
    }

    private double getItemY(int index) {
        int row = index / ITEM_COLUMNS;

        double startY = ROOM_Y + ROOM_H * 0.28;
        double gapY = ROOM_H * 0.14;

        return startY + row * gapY;
    }

    private void interactWithItem(Item item) {
        ItemController itemController = new ItemController(
                new ItemModel(item, heroModel),
                new ItemViewCLI("Item"),
                new ItemViewLogGUI(viewGUI::displayMessage),
                heroModel);

        itemController.onInteractItem();

        refreshRoomViews();
    }

    private void refreshRoomViews() {
        Room currentRoom = roomModel.getRoom();

        if (currentRoom == null) {
            return;
        }

        viewCLI.displayRoom(currentRoom);

        viewGUI.displayHeroPosition(heroModel.getX(), heroModel.getY());

        viewGUI.displayVisitedRooms(
                roomModel.getVisitedRoomPlacements(),
                roomModel.getCurrentGridX(),
                roomModel.getCurrentGridY());

        viewGUI.displayRoom(currentRoom);
    }

    private static final class NearestItem {
        private final Item item;
        private final double distance;

        private NearestItem(Item item, double distance) {
            this.item = item;
            this.distance = distance;
        }
    }
}