package mvc.map.controller;

import common.item.Chest;
import common.item.Item;
import common.map.Exit;
import common.map.Room;
import mvc.entity.model.HeroModel;
import mvc.map.ItemPlacement;
import mvc.map.model.ExitModel;
import mvc.map.model.RoomModel;
import mvc.map.view.base.RoomView;
import mvc.map.view.cli.ExitViewCLI;
import mvc.map.view.gui.ExitViewGUI;
import mvc.mvc.Controller;
import common.item.UsableItem;

public class RoomController extends Controller {

    private static final double STEP = 4;

    private static final double MIN_X = 110;
    private static final double MAX_X = 610;
    private static final double MIN_Y = 90;
    private static final double MAX_Y = 410;

    private static final double INTERACT_DISTANCE = 35.0;

    private final RoomModel roomModel;
    private final HeroModel heroModel;
    private final RoomView viewCLI;
    private final RoomView viewGUI;

    public RoomController(RoomModel roomModel, HeroModel heroModel, RoomView viewCLI, RoomView viewGUI) {
        super(roomModel, viewCLI, viewGUI);
        this.roomModel = roomModel;
        this.heroModel = heroModel;
        this.viewCLI = viewCLI;
        this.viewGUI = viewGUI;

        this.viewGUI.setOnMoveNorth(() -> heroMove("north"));
        this.viewGUI.setOnMoveSouth(() -> heroMove("south"));
        this.viewGUI.setOnMoveEast(() -> heroMove("east"));
        this.viewGUI.setOnMoveWest(() -> heroMove("west"));
    }

    public void onEnterRoom() {
        Room currentRoom = roomModel.getRoom();
        roomModel.rebuildItemPlacements();

        viewCLI.displayRoom(currentRoom);
        viewGUI.displayPlacedItems(roomModel.getItemPlacements());
        viewGUI.displayVisitedRooms(
                roomModel.getVisitedRoomPlacements(),
                roomModel.getCurrentGridX(),
                roomModel.getCurrentGridY());
        viewGUI.displayRoom(currentRoom);
        viewGUI.displayHeroPosition(heroModel.getX(), heroModel.getY());
    }

    public void heroMove(String direction) {
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

        moveHero(dx, dy);
    }

    private void moveHero(double dx, double dy) {
        Room currentRoom = roomModel.getRoom();

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
        Exit exit = roomModel.getRoom().getExit(direction);

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
        heroModel.resetPosition();

        viewCLI.displayMove(direction, target.getName());
        viewGUI.displayMove(direction, target.getName());

        onEnterRoom();
    }

    public void goTo(String direction) {
        Exit exit = roomModel.getRoom().getExit(direction);

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

        roomModel.rebuildItemPlacements();
        ItemPlacement nearest = findNearestPlacement();

        if (nearest == null) {
            viewCLI.displayMessage("There is nothing to interact with.");
            viewGUI.displayMessage("There is nothing to interact with.");
            return;
        }

        double dx = heroModel.getX() - nearest.getX();
        double dy = heroModel.getY() - nearest.getY();
        double distance = Math.hypot(dx, dy);

        if (distance > INTERACT_DISTANCE) {
            viewCLI.displayMessage("Nothing nearby to interact with.");
            viewGUI.displayMessage("Nothing nearby to interact with.");
            return;
        }

        interactWithItem(nearest.getItem());
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

    private ItemPlacement findNearestPlacement() {
        ItemPlacement nearest = null;
        double bestDistance = Double.MAX_VALUE;

        for (ItemPlacement placement : roomModel.getItemPlacements()) {
            double dx = heroModel.getX() - placement.getX();
            double dy = heroModel.getY() - placement.getY();
            double distance = Math.hypot(dx, dy);

            if (distance < bestDistance) {
                bestDistance = distance;
                nearest = placement;
            }
        }

        return nearest;
    }

    private void interactWithItem(Item item) {
        Room currentRoom = roomModel.getRoom();

        if (item instanceof Chest chest) {
            int beforeCount = chest.getContent().size();
            chest.use(heroModel.getHero());

            if (beforeCount == 0) {
                viewCLI.displayMessage(chest.getName() + " is empty.");
                viewGUI.displayMessage(chest.getName() + " is empty.");
            } else {
                viewCLI.displayMessage("You open " + chest.getName() + ".");
                viewGUI.displayMessage("You open " + chest.getName() + ".");
            }

            heroModel.syncState();
            refreshRoomViews();
            return;
        }

        if (item.canBeTaken()) {
            currentRoom.removeItem(item);
            heroModel.getHero().addItem(item);

            String message = heroModel.getName() + " takes " + item.getName() + ".";
            viewCLI.displayMessage(message);
            viewGUI.displayMessage(message);

            heroModel.syncState();
            refreshRoomViews();
            return;
        }

        if (item.canBeUsed() && item instanceof UsableItem usableItem) {
            usableItem.use(heroModel.getHero());

            viewCLI.displayMessage("You use " + item.getName() + ".");
            viewGUI.displayMessage("You use " + item.getName() + ".");

            heroModel.syncState();
            refreshRoomViews();
            return;
        }

        viewCLI.displayMessage("Nothing happens.");
        viewGUI.displayMessage("Nothing happens.");
    }

    private void refreshRoomViews() {
        Room currentRoom = roomModel.getRoom();
        roomModel.rebuildItemPlacements();

        viewCLI.displayRoom(currentRoom);
        viewGUI.displayPlacedItems(roomModel.getItemPlacements());
        viewGUI.displayVisitedRooms(
                roomModel.getVisitedRoomPlacements(),
                roomModel.getCurrentGridX(),
                roomModel.getCurrentGridY());
        viewGUI.displayRoom(currentRoom);
        viewGUI.displayHeroPosition(heroModel.getX(), heroModel.getY());
    }
}