package mvc.game.controller;

import mvc.game.model.GameModel;
import mvc.game.view.base.GameView;
import mvc.map.controller.RoomController;
import mvc.mvc.Controller;

public class GameController extends Controller {

    private final GameView gameView;
    private final RoomController roomController;

    public GameController(GameModel gameModel, GameView gameView, RoomController roomController) {
        super(gameModel, gameView, gameView);
        this.gameView = gameView;
        this.roomController = roomController;

        this.gameView.setOnMoveNorth(() -> roomController.heroMove("north"));
        this.gameView.setOnMoveSouth(() -> roomController.heroMove("south"));
        this.gameView.setOnMoveEast(() -> roomController.heroMove("east"));
        this.gameView.setOnMoveWest(() -> roomController.heroMove("west"));
        this.gameView.setOnInteract(roomController::interactNearby);
    }
}