package mvc.map.controller;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import common.entity.Enemy;
import common.item.Chest;
import common.item.Item;
import common.map.Exit;
import common.map.Room;
import mvc.entity.model.EnemyModel;
import mvc.entity.model.HeroModel;
import mvc.entity.model.ProjectileModel;
import mvc.item.controller.ItemController;
import mvc.item.model.ItemModel;
import mvc.item.view.cli.ItemViewCLI;
import mvc.item.view.gui.ItemViewLogGUI;
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

    private static final double INTERACT_DISTANCE = 35.0;

    private static final int ITEM_COLUMNS = 3;

    private final RoomModel roomModel;
    private final HeroModel heroModel;
    private final RoomView viewCLI;
    private final RoomView viewGUI;

    private static final double PLAYER_SWORD_RANGE = 58.0;
    private static final double PLAYER_BOW_RANGE = Math.hypot(ROOM_W, ROOM_H);
    private static final double PLAYER_ARROW_SPEED = 7.2;
    private static final double PLAYER_PROJECTILE_HIT_RADIUS = 24.0;

    private final Map<Enemy, EnemyModel> enemyModels = new LinkedHashMap<>();
    private final List<ProjectileModel> projectiles = new ArrayList<>();

    private final Runnable onBossRoomCleared;
    private boolean bossRoomRewardGiven = false;

    private static final long ROOM_TRANSITION_COOLDOWN_NS = 180_000_000L;
    private long movementLockedUntil = 0L;

    private static final long PLAYER_SWORD_COOLDOWN_NS = 350_000_000L;
    private static final long PLAYER_BOW_COOLDOWN_NS = 650_000_000L;
    private long nextPlayerAttackTime = 0L;

    public RoomController(RoomModel roomModel, HeroModel heroModel, RoomView viewCLI, RoomView viewGUI) {
        this(roomModel, heroModel, viewCLI, viewGUI, null);
    }

    public RoomController(RoomModel roomModel, HeroModel heroModel, RoomView viewCLI, RoomView viewGUI,
            Runnable onBossRoomCleared) {
        super(roomModel, viewCLI, viewGUI);

        this.roomModel = roomModel;
        this.heroModel = heroModel;
        this.viewCLI = viewCLI;
        this.viewGUI = viewGUI;
        this.onBossRoomCleared = onBossRoomCleared;
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

        syncEnemiesForCurrentRoom();
        displayEnemiesAndProjectiles();

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

        projectiles.clear();

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

    public void updateEnemies(long now) {
        Room currentRoom = roomModel.getRoom();

        if (currentRoom == null) {
            return;
        }

        syncEnemiesForCurrentRoom();

        updatePlayerAutoAttack(now);

        for (EnemyModel enemyModel : getCurrentEnemyModels()) {
            enemyModel.update(heroModel, now, projectiles);
        }

        for (ProjectileModel projectile : projectiles) {
            projectile.update(heroModel);
        }

        updateHeroProjectileHits(now);

        projectiles.removeIf(projectile -> !projectile.isAlive());

        currentRoom.getCharacters().removeIf(character -> character instanceof Enemy enemy && !enemy.isAlive());

        enemyModels.entrySet().removeIf(entry -> !entry.getKey().isAlive());

        updateHeroAutoFacing();
        displayEnemiesAndProjectiles();
    }

    private void syncEnemiesForCurrentRoom() {
        Room currentRoom = roomModel.getRoom();

        if (currentRoom == null) {
            return;
        }

        int index = 0;

        for (common.entity.Character character : currentRoom.getCharacters()) {
            if (!(character instanceof Enemy enemy)) {
                continue;
            }

            if (!enemyModels.containsKey(enemy)) {
                double x = getEnemyStartX(index);
                double y = getEnemyStartY(index);
                enemyModels.put(enemy, new EnemyModel(enemy, x, y));
            }

            index++;
        }
    }

    private List<EnemyModel> getCurrentEnemyModels() {
        Room currentRoom = roomModel.getRoom();
        List<EnemyModel> result = new ArrayList<>();

        if (currentRoom == null) {
            return result;
        }

        for (common.entity.Character character : currentRoom.getCharacters()) {
            if (character instanceof Enemy enemy) {
                EnemyModel enemyModel = enemyModels.get(enemy);

                if (enemyModel != null && enemyModel.isAlive()) {
                    result.add(enemyModel);
                }
            }
        }

        return result;
    }

    private double getEnemyStartX(int index) {
        int col = index % 3;
        return ROOM_X + ROOM_W * (0.25 + col * 0.25);
    }

    private double getEnemyStartY(int index) {
        int row = index / 3;
        return ROOM_Y + ROOM_H * (0.42 + row * 0.18);
    }

    private void displayEnemiesAndProjectiles() {
        viewGUI.displayEnemies(
                getCurrentEnemyModels().stream()
                        .map(EnemyModel::snapshot)
                        .toList());

        viewGUI.displayProjectiles(
                projectiles.stream()
                        .map(ProjectileModel::snapshot)
                        .toList());
    }

    private void updateHeroAutoFacing() {
        NearestEnemy nearest = findNearestEnemy();

        if (nearest == null) {
            return;
        }

        double dx = nearest.enemyModel.getX() - heroModel.getX();
        double dy = nearest.enemyModel.getY() - heroModel.getY();
        double distance = Math.hypot(dx, dy);

        if (distance <= 180 && distance > 0.001) {
            viewGUI.displayHeroFacing(dx / distance, dy / distance);
        }
    }

    private NearestEnemy findNearestEnemy() {
        EnemyModel nearestEnemy = null;
        double bestDistance = Double.MAX_VALUE;

        for (EnemyModel enemyModel : getCurrentEnemyModels()) {
            double dx = heroModel.getX() - enemyModel.getX();
            double dy = heroModel.getY() - enemyModel.getY();
            double distance = Math.hypot(dx, dy);

            if (distance < bestDistance) {
                bestDistance = distance;
                nearestEnemy = enemyModel;
            }
        }

        if (nearestEnemy == null) {
            return null;
        }

        return new NearestEnemy(nearestEnemy, bestDistance);
    }

    private static final class NearestEnemy {
        private final EnemyModel enemyModel;
        private final double distance;

        private NearestEnemy(EnemyModel enemyModel, double distance) {
            this.enemyModel = enemyModel;
            this.distance = distance;
        }
    }

    private void updatePlayerAutoAttack(long now) {
        Room currentRoom = roomModel.getRoom();

        if (currentRoom == null) {
            return;
        }

        if (heroModel.getEquippedWeapon() == null) {
            return;
        }

        if (now < nextPlayerAttackTime) {
            return;
        }

        NearestEnemy nearestEnemy = findNearestEnemy();

        if (nearestEnemy == null) {
            return;
        }

        double dx = nearestEnemy.enemyModel.getX() - heroModel.getX();
        double dy = nearestEnemy.enemyModel.getY() - heroModel.getY();
        double distance = Math.hypot(dx, dy);

        if (distance > 0.001) {
            viewGUI.displayHeroFacing(dx / distance, dy / distance);
        }

        if (heroModel.isEquippedWeaponRanged()) {
            if (nearestEnemy.distance <= PLAYER_BOW_RANGE) {
                nextPlayerAttackTime = now + PLAYER_BOW_COOLDOWN_NS;
                shootHeroArrow(nearestEnemy.enemyModel, now);
            }

            return;
        }

        if (nearestEnemy.distance <= PLAYER_SWORD_RANGE) {
            nextPlayerAttackTime = now + PLAYER_SWORD_COOLDOWN_NS;
            attackEnemy(nearestEnemy.enemyModel, heroModel.getEquippedWeaponName(), now);
        }
    }

    private void shootHeroArrow(EnemyModel target, long now) {
        double dx = target.getX() - heroModel.getX();
        double dy = target.getY() - heroModel.getY();
        double distance = Math.hypot(dx, dy);

        if (distance <= 0.001) {
            return;
        }

        double vx = dx / distance * PLAYER_ARROW_SPEED;
        double vy = dy / distance * PLAYER_ARROW_SPEED;

        int damage = Math.max(1, heroModel.getDamage());

        projectiles.add(new ProjectileModel(
                heroModel.getX(),
                heroModel.getY(),
                vx,
                vy,
                damage,
                ProjectileModel.Owner.HERO));

        viewGUI.displayHeroFacing(dx / distance, dy / distance);
        viewGUI.displayHeroAttackFlash();

        target.flashHit(now);

        viewCLI.displayMessage("You shoot an arrow at " + target.getEnemy().getName() + ".");
        viewGUI.displayMessage("You shoot an arrow at " + target.getEnemy().getName() + ".");

        displayEnemiesAndProjectiles();
    }

    private void updateHeroProjectileHits(long now) {
        for (ProjectileModel projectile : projectiles) {
            if (!projectile.isAlive() || !projectile.isFromHero()) {
                continue;
            }

            for (EnemyModel enemyModel : getCurrentEnemyModels()) {
                if (!enemyModel.isAlive()) {
                    continue;
                }

                double dx = enemyModel.getX() - projectile.getX();
                double dy = enemyModel.getY() - projectile.getY();
                double distance = Math.hypot(dx, dy);

                if (distance <= PLAYER_PROJECTILE_HIT_RADIUS + ProjectileModel.PROJECTILE_RADIUS) {
                    int realDamage = enemyModel.receiveDamage(projectile.getDamage());

                    enemyModel.flashHit(now);
                    projectile.kill();

                    viewCLI.displayMessage(
                            "Arrow hits " + enemyModel.getEnemy().getName() + " for " + realDamage + " damage.");
                    viewGUI.displayMessage(
                            "Arrow hits " + enemyModel.getEnemy().getName() + " for " + realDamage + " damage.");

                    if (!enemyModel.isAlive()) {
                        handleEnemyDefeated(enemyModel);
                    }

                    break;
                }
            }
        }
    }

    private void attackEnemy(EnemyModel enemyModel) {
        attackEnemy(enemyModel, "sword", System.nanoTime());
    }

    private void attackEnemy(EnemyModel enemyModel, String attackName, long now) {
        int damage = heroModel.getDamage();
        int realDamage = enemyModel.receiveDamage(damage);

        enemyModel.flashHit(now);
        viewGUI.displayHeroAttackFlash();

        viewCLI.displayMessage("You attack " + enemyModel.getEnemy().getName() + " with " + attackName + " for "
                + realDamage + " damage.");
        viewGUI.displayMessage("You attack " + enemyModel.getEnemy().getName() + " with " + attackName + " for "
                + realDamage + " damage.");

        if (!enemyModel.isAlive()) {
            handleEnemyDefeated(enemyModel);
        }

        displayEnemiesAndProjectiles();
    }

    private void handleEnemyDefeated(EnemyModel enemyModel) {
        Room currentRoom = roomModel.getRoom();

        if (currentRoom != null) {
            dropEnemyInventory(enemyModel.getEnemy(), currentRoom);
            currentRoom.removeCharacter(enemyModel.getEnemy());
        }

        viewCLI.displayMessage(enemyModel.getEnemy().getName() + " is defeated.");
        viewGUI.displayMessage(enemyModel.getEnemy().getName() + " is defeated.");

        heroModel.healPercent(10);
        heroModel.increaseDamageByPercent(20);

        String rewardMessage = "Kill reward: +10% HP regenerated and +20% damage. HP: "
                + heroModel.getHealth() + "/" + heroModel.getMaxHealth()
                + " | Damage: " + heroModel.getDamage() + ".";

        viewCLI.displayMessage(rewardMessage);
        viewGUI.displayMessage(rewardMessage);

        checkBossRoomCleared(currentRoom);
        refreshRoomViews();
    }

    private void dropEnemyInventory(Enemy enemy, Room room) {
        List<Item> loot = new ArrayList<>(enemy.getInventory());

        if (loot.isEmpty()) {
            return;
        }

        for (Item item : loot) {
            enemy.removeFromInventory(item);
            room.addItem(item);

            viewCLI.displayMessage(enemy.getName() + " dropped " + item.getName() + ".");
            viewGUI.displayMessage(enemy.getName() + " dropped " + item.getName() + ".");
        }
    }

    private void checkBossRoomCleared(Room room) {
        if (room == null || !room.isBossRoom()) {
            return;
        }

        if (bossRoomRewardGiven) {
            return;
        }

        if (hasAliveEnemy(room)) {
            return;
        }

        bossRoomRewardGiven = true;

        boolean hpReward = ThreadLocalRandom.current().nextBoolean();

        if (hpReward) {
            int bonus = 20;
            heroModel.increaseMaxHp(bonus);

            String message = "Boss room cleared! Reward: +" + bonus + " max HP. Current HP: "
                    + heroModel.getHealth() + "/" + heroModel.getMaxHealth() + ".";

            viewCLI.displayMessage(message);
            viewGUI.displayMessage(message);
        } else {
            int bonus = 5;
            heroModel.increaseBaseDamage(bonus);

            String message = "Boss room cleared! Reward: +" + bonus + " base damage. Current damage: "
                    + heroModel.getDamage() + ".";

            viewCLI.displayMessage(message);
            viewGUI.displayMessage(message);
        }

        if (onBossRoomCleared != null) {
            onBossRoomCleared.run();
        }
    }

    private boolean hasAliveEnemy(Room room) {
        for (common.entity.Character character : room.getCharacters()) {
            if (character instanceof Enemy enemy && enemy.isAlive()) {
                return true;
            }
        }

        return false;
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

    public void attackNearby() {
        Room currentRoom = roomModel.getRoom();

        if (currentRoom == null) {
            return;
        }

        if (heroModel.getEquippedWeapon() == null) {
            viewCLI.displayMessage("No weapon equipped.");
            viewGUI.displayMessage("No weapon equipped.");
            return;
        }

        NearestEnemy nearestEnemy = findNearestEnemy();

        if (nearestEnemy == null) {
            viewCLI.displayMessage("There is no enemy nearby.");
            viewGUI.displayMessage("There is no enemy nearby.");
            return;
        }

        long now = System.nanoTime();

        if (now < nextPlayerAttackTime) {
            return;
        }

        if (heroModel.isEquippedWeaponRanged()) {
            if (nearestEnemy.distance > PLAYER_BOW_RANGE) {
                viewCLI.displayMessage("Enemy is too far away.");
                viewGUI.displayMessage("Enemy is too far away.");
                return;
            }

            nextPlayerAttackTime = now + PLAYER_BOW_COOLDOWN_NS;
            shootHeroArrow(nearestEnemy.enemyModel, now);
            return;
        }

        if (nearestEnemy.distance > PLAYER_SWORD_RANGE) {
            viewCLI.displayMessage("Enemy is too far away.");
            viewGUI.displayMessage("Enemy is too far away.");
            return;
        }

        nextPlayerAttackTime = now + PLAYER_SWORD_COOLDOWN_NS;
        attackEnemy(nearestEnemy.enemyModel, heroModel.getEquippedWeaponName(), now);
    }
}