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
    private final HeroModel secondHeroModel;

    private final RoomView viewCLI;
    private final RoomView viewGUI;

    private final CombatController combatController;
    private final RoomItemInteractionController itemInteractionController;
    private final RoomItemInteractionController secondItemInteractionController;

    private long movementLockedUntil = 0L;

    public RoomController(RoomModel roomModel, HeroModel heroModel, RoomView viewCLI, RoomView viewGUI) {
        this(roomModel, heroModel, null, viewCLI, viewGUI, null);
    }

    public RoomController(
            RoomModel roomModel,
            HeroModel heroModel,
            RoomView viewCLI,
            RoomView viewGUI,
            Runnable onBossRoomCleared
    ) {
        this(roomModel, heroModel, null, viewCLI, viewGUI, onBossRoomCleared);
    }

    public RoomController(
            RoomModel roomModel,
            HeroModel heroModel,
            HeroModel secondHeroModel,
            RoomView viewCLI,
            RoomView viewGUI,
            Runnable onBossRoomCleared
    ) {
        super(roomModel, viewCLI, viewGUI);

        this.roomModel = roomModel;
        this.heroModel = heroModel;
        this.secondHeroModel = secondHeroModel;
        this.viewCLI = viewCLI;
        this.viewGUI = viewGUI;

        this.combatController = new CombatController(
                roomModel,
                heroModel,
                secondHeroModel,
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

        if (secondHeroModel == null) {
            this.secondItemInteractionController = null;
        } else {
            this.secondItemInteractionController = new RoomItemInteractionController(
                    roomModel,
                    secondHeroModel,
                    viewCLI,
                    viewGUI,
                    this::refreshRoomViews
            );
        }

        this.subControllers.add(combatController);
        this.subControllers.add(itemInteractionController);

        if (secondItemInteractionController != null) {
            this.subControllers.add(secondItemInteractionController);
        }
    }

    public void onEnterRoom() {
        Room currentRoom = roomModel.getRoom();

        if (currentRoom == null) {
            return;
        }

        viewCLI.displayRoom(currentRoom);

        displayHeroPositions();

        viewGUI.displayVisitedRooms(
                roomModel.getVisitedRoomPlacements(),
                roomModel.getCurrentGridX(),
                roomModel.getCurrentGridY()
        );

        combatController.onEnterRoom();

        viewGUI.displayRoom(currentRoom);
        displayHeroPositions();
    }

    public void heroMove(String direction) {
        moveHero(heroModel, false, direction);
    }

    public void secondHeroMove(String direction) {
        if (secondHeroModel == null) {
            return;
        }

        moveHero(secondHeroModel, true, direction);
    }

    private void moveHero(HeroModel movingHero, boolean secondPlayer, String direction) {
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

        if (secondPlayer) {
            viewGUI.displaySecondHeroFacing(dx, dy);
        } else {
            viewGUI.displayHeroFacing(dx, dy);
        }

        double nextX = movingHero.getX() + dx;
        double nextY = movingHero.getY() + dy;

        if (nextX < MIN_X) {
            if (currentRoom.getExit("west") != null) {
                crossExit(movingHero, "west");
                return;
            }

            nextX = MIN_X;
        }

        if (nextX > MAX_X) {
            if (currentRoom.getExit("east") != null) {
                crossExit(movingHero, "east");
                return;
            }

            nextX = MAX_X;
        }

        if (nextY < MIN_Y) {
            if (currentRoom.getExit("north") != null) {
                crossExit(movingHero, "north");
                return;
            }

            nextY = MIN_Y;
        }

        if (nextY > MAX_Y) {
            if (currentRoom.getExit("south") != null) {
                crossExit(movingHero, "south");
                return;
            }

            nextY = MAX_Y;
        }

        movingHero.setPosition(nextX, nextY);

        if (secondPlayer) {
            viewGUI.displaySecondHeroPosition(nextX, nextY);
        } else {
            viewGUI.displayHeroPosition(nextX, nextY);
        }
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

        crossExit(heroModel, direction);
    }

    private void crossExit(HeroModel actor, String direction) {
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

        exitController.onUnlock(actor.getHero());

        Room target = exitController.onCross(actor.getHero());

        if (target == null) {
            return;
        }

        roomModel.moveTo(target, direction);

        heroModel.setRoom(target);
        heroModel.placeAfterCrossing(direction);

        if (secondHeroModel != null) {
            secondHeroModel.setRoom(target);
            secondHeroModel.placeAfterCrossing(direction);
            separateHeroesIfOverlapping();
        }

        combatController.clearProjectiles();

        movementLockedUntil = System.nanoTime() + ROOM_TRANSITION_COOLDOWN_NS;

        viewCLI.displayMove(direction, target.getName());
        viewGUI.displayMove(direction, target.getName());

        onEnterRoom();
    }

    private void separateHeroesIfOverlapping() {
        if (secondHeroModel == null) {
            return;
        }

        double dx = secondHeroModel.getX() - heroModel.getX();
        double dy = secondHeroModel.getY() - heroModel.getY();

        if (Math.hypot(dx, dy) > MapLayout.HERO_RADIUS * 1.5) {
            return;
        }

        double newX = Math.min(MAX_X, heroModel.getX() + MapLayout.HERO_RADIUS * 2.4);
        secondHeroModel.setPosition(newX, heroModel.getY());
    }

    public void updateEnemies(long now) {
        combatController.update(now);
    }

    public void attackNearby() {
        combatController.attackNearby();
    }

    public void secondAttackNearby() {
        if (secondHeroModel != null) {
            combatController.secondAttackNearby();
        }
    }

    public void interactNearby() {
        itemInteractionController.interactNearby();
    }

    public void secondInteractNearby() {
        if (secondItemInteractionController != null) {
            secondItemInteractionController.interactNearby();
        }
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

        displayHeroPositions();

        viewGUI.displayVisitedRooms(
                roomModel.getVisitedRoomPlacements(),
                roomModel.getCurrentGridX(),
                roomModel.getCurrentGridY()
        );

        viewGUI.displayRoom(currentRoom);
        displayHeroPositions();
    }

    private void displayHeroPositions() {
        viewGUI.displayHeroPosition(heroModel.getX(), heroModel.getY());

        if (secondHeroModel != null) {
            viewGUI.displaySecondHeroPosition(secondHeroModel.getX(), secondHeroModel.getY());
        }
    }
}