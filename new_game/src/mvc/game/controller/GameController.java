package mvc.game.controller;

import mvc.entity.model.HeroModel;
import mvc.game.model.GameModel;
import mvc.game.view.base.GameView;
import mvc.map.controller.RoomController;
import mvc.mvc.Controller;

public class GameController extends Controller {

    private final GameView gameView;
    private final RoomController roomController;
    private final HeroModel heroModel;

    public GameController(GameModel gameModel, GameView gameView, RoomController roomController, HeroModel heroModel) {
        super(gameModel, gameView, gameView);
        this.gameView = gameView;
        this.roomController = roomController;
        this.heroModel = heroModel;

        this.gameView.setOnMoveNorth(() -> roomController.heroMove("north"));
        this.gameView.setOnMoveSouth(() -> roomController.heroMove("south"));
        this.gameView.setOnMoveEast(() -> roomController.heroMove("east"));
        this.gameView.setOnMoveWest(() -> roomController.heroMove("west"));
        this.gameView.setOnInteract(roomController::interactNearby);
        this.gameView.setOnToggleInventory(this::refreshInventory);

        this.heroModel.addListener(new HeroModel.Listener() {
            @Override
            public void onHealthChanged(int newHp) {
                refreshInventory();
            }

            @Override
            public void onLocationChanged(String newLocation) {
                refreshInventory();
            }
        });

        refreshInventory();
    }

    private void refreshInventory() {
        java.util.List<String> items = heroModel.getHero().getInventory().stream()
                .map(item -> item.getName())
                .toList();

        gameView.displayInventory(items);
    }
}