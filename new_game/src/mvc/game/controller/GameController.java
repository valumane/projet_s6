package mvc.game.controller;

import java.util.ArrayList;
import java.util.List;

import common.item.Item;
import common.item.Weapon;
import mvc.entity.model.HeroModel;
import mvc.game.model.GameModel;
import mvc.game.view.base.GameView;
import mvc.map.controller.RoomController;
import mvc.mvc.Controller;

public class GameController extends Controller {

    private final GameView gameView;
    private final RoomController roomController;
    private final HeroModel heroModel;

    private boolean gameOver = false;

    public GameController(GameModel gameModel, GameView gameView, RoomController roomController, HeroModel heroModel) {
        super(gameModel, gameView, gameView);

        this.gameView = gameView;
        this.roomController = roomController;
        this.heroModel = heroModel;

        this.gameView.setOnMoveNorth(() -> moveHero("north"));
        this.gameView.setOnMoveSouth(() -> moveHero("south"));
        this.gameView.setOnMoveEast(() -> moveHero("east"));
        this.gameView.setOnMoveWest(() -> moveHero("west"));

        this.gameView.setOnInteract(this::interact);
        this.gameView.setOnToggleInventory(this::refreshInventory);
        this.gameView.setOnUseInventorySlot(this::useInventorySlot);
        this.gameView.setOnGameTick(this::updateGame);
        this.gameView.setOnAttack(this::attack);

        this.heroModel.addListener(new HeroModel.Listener() {
            @Override
            public void onHealthChanged(int newHp) {
                refreshInventory();

                if (newHp <= 0 && !gameOver) {
                    gameOver = true;
                    GameController.this.gameView.displayGameOver();
                }
            }

            @Override
            public void onLocationChanged(String newLocation) {
                refreshInventory();
            }
        });

        refreshInventory();
        refreshInfoBar();
    }

    private void moveHero(String direction) {
        if (gameOver) {
            return;
        }

        roomController.heroMove(direction);
    }

    private void interact() {
        if (gameOver) {
            return;
        }

        roomController.interactNearby();
        refreshInventory();
        refreshInfoBar();
    }

    private void attack() {
        if (gameOver) {
            return;
        }

        roomController.attackNearby();
        refreshInventory();
        refreshInfoBar();
    }

    private void updateGame(long now) {
        if (gameOver) {
            return;
        }

        roomController.updateEnemies(now);
    }

    private void useInventorySlot(int slotIndex) {
        if (gameOver) {
            return;
        }

        String message = heroModel.useInventorySlot(slotIndex);

        gameView.displayInfo(message);
        refreshInventory();
    }

    private void refreshInventory() {
        List<Item> inventory = heroModel.getInventory();
        List<String> labels = new ArrayList<>();

        for (int i = 0; i < 9; i++) {
            if (i >= inventory.size()) {
                labels.add((i + 1) + ". (vide)");
                continue;
            }

            Item item = inventory.get(i);
            String text = (i + 1) + ". " + item.getName();

            if (item instanceof Weapon weapon && weapon == heroModel.getEquippedWeapon()) {
                text += " [équipée]";
            }

            labels.add(text);
        }

        gameView.displayInventory(labels);
    }

    private void refreshInfoBar() {
        gameView.displayInfo(
                "Arme équipée : " + heroModel.getEquippedWeaponName()
                        + " | dégâts : " + heroModel.getDamage()
        );
    }
}