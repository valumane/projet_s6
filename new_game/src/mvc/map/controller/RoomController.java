package mvc.map.controller;

import common.map.Exit;
import common.map.Room;
import mvc.entity.controller.CombatController;
import mvc.entity.model.HeroModel;
import mvc.item.controller.RoomItemInteractionController;
import mvc.map.MapLayout;
import mvc.map.model.ExitModel;
import mvc.map.model.RoomModel;
import mvc.map.view.base.RoomView;
import mvc.map.view.cli.ExitViewCLI;
import mvc.map.view.gui.ExitViewGUI;
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

    private static final long ROOM_TRANSITION_COOLDOWN_NS = 180_000_000L;

    private final RoomModel roomModel;
    private final HeroModel heroModel;
    private final RoomView viewCLI;
    private final RoomView viewGUI;

    private final CombatController combatController;
    private final RoomItemInteractionController itemInteractionController;

    private long movementLockedUntil = 0L;

    public RoomController(RoomModel roomModel, HeroModel heroModel, RoomView viewCLI, RoomView viewGUI) {
        this(roomModel, heroModel, viewCLI, viewGUI, null);
    }

    public RoomController(
            RoomModel roomModel,
            HeroModel heroModel,
            RoomView viewCLI,
            RoomView viewGUI,
            Runnable onBossRoomCleared
    ) {
        super(roomModel, viewCLI, viewGUI);

        this.roomModel = roomModel;
        this.heroModel = heroModel;
        this.viewCLI = viewCLI;
        this.viewGUI = viewGUI;

        this.combatController = new CombatController(
                roomModel,
                heroModel,
                viewCLI,
                viewGUI,
                onBossRoomCleared,
                this::refreshRoomViews
        );

        this.itemInteractionController = new RoomItemInteractionController(
                roomModel,
                heroModel,
                viewCLI,
                viewGUI,
                this::refreshRoomViews
        );

        this.subControllers.add(combatController);
        this.subControllers.add(itemInteractionController);
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
                roomModel.getCurrentGridY()
        );

        combatController.onEnterRoom();

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

        viewGUI.displayHeroFacing(dx, dy);

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
                new ExitViewGUI(viewGUI::displayMessage)
        );

        exitController.onUnlock(heroModel.getHero());

        Room target = exitController.onCross(heroModel.getHero());

        if (target == null) {
            return;
        }

        heroModel.setRoom(target);
        roomModel.moveTo(target, direction);
        heroModel.placeAfterCrossing(direction);

        combatController.clearProjectiles();

        movementLockedUntil = System.nanoTime() + ROOM_TRANSITION_COOLDOWN_NS;

        viewCLI.displayMove(direction, target.getName());
        viewGUI.displayMove(direction, target.getName());

        onEnterRoom();
    }

    public void updateEnemies(long now) {
        combatController.update(now);
    }

    public void attackNearby() {
        combatController.attackNearby();
    }

    public void interactNearby() {
        itemInteractionController.interactNearby();
    }

    public void interactCli() {
        itemInteractionController.interactCli();
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
                roomModel.getCurrentGridY()
        );

        viewGUI.displayRoom(currentRoom);
    }
}