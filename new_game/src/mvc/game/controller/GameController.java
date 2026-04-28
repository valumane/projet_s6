package mvc.game.controller;

import java.util.ArrayList;
import java.util.List;

import common.langage.Langage;
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
    private final HeroModel secondHeroModel;

    private boolean gameOver = false;

    public GameController(GameModel gameModel, GameView gameView, RoomController roomController, HeroModel heroModel) {
        this(gameModel, gameView, roomController, heroModel, null);
    }

    public GameController(
            GameModel gameModel,
            GameView gameView,
            RoomController roomController,
            HeroModel heroModel,
            HeroModel secondHeroModel
    ) {
        super(gameModel, gameView, gameView);

        this.gameView = gameView;
        this.roomController = roomController;
        this.heroModel = heroModel;
        this.secondHeroModel = secondHeroModel;

        this.gameView.setOnMoveNorth(() -> moveHero("north"));
        this.gameView.setOnMoveSouth(() -> moveHero("south"));
        this.gameView.setOnMoveEast(() -> moveHero("east"));
        this.gameView.setOnMoveWest(() -> moveHero("west"));

        this.gameView.setOnPlayer2MoveNorth(() -> moveSecondHero("north"));
        this.gameView.setOnPlayer2MoveSouth(() -> moveSecondHero("south"));
        this.gameView.setOnPlayer2MoveEast(() -> moveSecondHero("east"));
        this.gameView.setOnPlayer2MoveWest(() -> moveSecondHero("west"));

        this.gameView.setOnInteract(this::interact);
        this.gameView.setOnPlayer2Interact(this::secondInteract);

        this.gameView.setOnToggleInventory(this::refreshInventories);

        this.gameView.setOnUseInventorySlot(slot -> useInventorySlot(heroModel, slot, false));
        this.gameView.setOnPlayer2UseInventorySlot(slot -> {
            if (secondHeroModel != null) {
                useInventorySlot(secondHeroModel, slot, true);
            }
        });

        this.gameView.setOnGameTick(this::updateGame);
        this.gameView.setOnAttack(this::attack);

        this.heroModel.addListener(new HeroModel.Listener() {
            @Override
            public void onHealthChanged(int newHp) {
                refreshInventories();
                refreshInfoBars();

                if (newHp <= 0 && !gameOver) {
                    gameOver = true;
                    GameController.this.gameView.displayGameOver();
                }
            }

            @Override
            public void onLocationChanged(String newLocation) {
                refreshInventories();
                refreshInfoBars();
            }
        });

        if (secondHeroModel != null) {
            this.secondHeroModel.addListener(new HeroModel.Listener() {
                @Override
                public void onHealthChanged(int newHp) {
                    refreshInventories();
                    refreshInfoBars();

                    if (newHp <= 0 && !gameOver) {
                        gameOver = true;
                        GameController.this.gameView.displayGameOver();
                    }
                }

                @Override
                public void onLocationChanged(String newLocation) {
                    refreshInventories();
                    refreshInfoBars();
                }
            });
        }

        refreshInventories();
        refreshInfoBars();
    }

    private void moveHero(String direction) {
        if (gameOver) {
            return;
        }

        roomController.heroMove(direction);
    }

    private void moveSecondHero(String direction) {
        if (gameOver || secondHeroModel == null) {
            return;
        }

        roomController.secondHeroMove(direction);
    }

    private void interact() {
        if (gameOver) {
            return;
        }

        roomController.interactNearby();
        refreshInventories();
        refreshInfoBars();
    }

    private void secondInteract() {
        if (gameOver || secondHeroModel == null) {
            return;
        }

        roomController.secondInteractNearby();
        refreshInventories();
        refreshInfoBars();
    }

    private void attack() {
        if (gameOver) {
            return;
        }

        roomController.attackNearby();

        if (secondHeroModel != null) {
            roomController.secondAttackNearby();
        }

        refreshInventories();
        refreshInfoBars();
    }

    private void updateGame(long now) {
        if (gameOver) {
            return;
        }

        roomController.updateEnemies(now);
    }

    private void useInventorySlot(HeroModel targetHero, int slotIndex, boolean secondPlayer) {
        if (gameOver) {
            return;
        }

        String message = targetHero.useInventorySlot(slotIndex);

        if (secondPlayer) {
            gameView.displayPlayer2Info("J2 : " + message);
        } else {
            gameView.displayInfo("J1 : " + message);
        }

        refreshInventories();
        refreshInfoBars();
    }

    private void refreshInventories() {
        gameView.displayInventory(buildInventoryLabels(heroModel));

        if (secondHeroModel != null) {
            gameView.displayPlayer2Inventory(buildInventoryLabels(secondHeroModel));
        }
    }

    private List<String> buildInventoryLabels(HeroModel model) {
        List<Item> inventory = model.getInventory();
        List<String> labels = new ArrayList<>();

        for (int i = 0; i < 9; i++) {
            if (i >= inventory.size()) {
                labels.add((i + 1) + ". " + Langage.t("game.emptySlot"));
                continue;
            }

            Item item = inventory.get(i);
            String text = (i + 1) + ". " + item.getName();

            if (item instanceof Weapon weapon && weapon == model.getEquippedWeapon()) {
                text += " " + Langage.t("game.equipped");
            }

            labels.add(text);
        }

        return labels;
    }

    private void refreshInfoBars() {
        gameView.displayInfo(
                "J1 | " + Langage.t("game.weaponInfo") + " : " + heroModel.getEquippedWeaponName()
                        + " | " + Langage.t("game.damage") + " : " + heroModel.getDamage()
        );

        if (secondHeroModel != null) {
            gameView.displayPlayer2Info(
                    "J2 | " + Langage.t("game.weaponInfo") + " : " + secondHeroModel.getEquippedWeaponName()
                            + " | " + Langage.t("game.damage") + " : " + secondHeroModel.getDamage()
            );
        }
    }
}
