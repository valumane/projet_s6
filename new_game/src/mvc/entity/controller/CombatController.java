package mvc.entity.controller;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import common.entity.Enemy;
import common.item.Item;
import common.map.Room;
import mvc.entity.model.EnemyModel;
import mvc.entity.model.HeroModel;
import mvc.entity.model.ProjectileModel;
import mvc.map.MapLayout;
import mvc.map.model.RoomModel;
import mvc.map.view.base.RoomView;
import mvc.mvc.Controller;

public class CombatController extends Controller {

    private static final double ROOM_W = MapLayout.ROOM_W;
    private static final double ROOM_H = MapLayout.ROOM_H;

    private static final double PLAYER_SWORD_RANGE = 58.0;
    private static final double PLAYER_BOW_RANGE = Math.hypot(ROOM_W, ROOM_H);
    private static final double PLAYER_ARROW_SPEED = 7.2;
    private static final double PLAYER_PROJECTILE_HIT_RADIUS = 24.0;

    private static final long PLAYER_SWORD_COOLDOWN_NS = 350_000_000L;
    private static final long PLAYER_BOW_COOLDOWN_NS = 650_000_000L;

    private final RoomModel roomModel;
    private final HeroModel heroModel;
    private final RoomView viewCLI;
    private final RoomView viewGUI;

    private final Map<Enemy, EnemyModel> enemyModels = new LinkedHashMap<>();
    private final List<ProjectileModel> projectiles = new ArrayList<>();

    private final Runnable onBossRoomCleared;
    private final Runnable refreshRoomViews;

    private boolean bossRoomRewardGiven = false;
    private long nextPlayerAttackTime = 0L;

    public CombatController(
            RoomModel roomModel,
            HeroModel heroModel,
            RoomView viewCLI,
            RoomView viewGUI,
            Runnable onBossRoomCleared,
            Runnable refreshRoomViews
    ) {
        super(roomModel, viewCLI, viewGUI);

        this.roomModel = roomModel;
        this.heroModel = heroModel;
        this.viewCLI = viewCLI;
        this.viewGUI = viewGUI;
        this.onBossRoomCleared = onBossRoomCleared;
        this.refreshRoomViews = refreshRoomViews;
    }

    public void onEnterRoom() {
        syncEnemiesForCurrentRoom();
        displayEnemiesAndProjectiles();
    }

    public void clearProjectiles() {
        projectiles.clear();
        displayEnemiesAndProjectiles();
    }

    public void update(long now) {
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

        currentRoom.getCharacters().removeIf(character ->
                character instanceof Enemy enemy && !enemy.isAlive()
        );

        enemyModels.entrySet().removeIf(entry -> !entry.getKey().isAlive());

        updateHeroAutoFacing();
        displayEnemiesAndProjectiles();
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
        return MapLayout.ROOM_X + MapLayout.ROOM_W * (0.25 + col * 0.25);
    }

    private double getEnemyStartY(int index) {
        int row = index / 3;
        return MapLayout.ROOM_Y + MapLayout.ROOM_H * (0.42 + row * 0.18);
    }

    private void displayEnemiesAndProjectiles() {
        viewGUI.displayEnemies(
                getCurrentEnemyModels().stream()
                        .map(EnemyModel::snapshot)
                        .toList()
        );

        viewGUI.displayProjectiles(
                projectiles.stream()
                        .map(ProjectileModel::snapshot)
                        .toList()
        );
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
                ProjectileModel.Owner.HERO
        ));

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

        if (refreshRoomViews != null) {
            refreshRoomViews.run();
        }
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

    private static final class NearestEnemy {
        private final EnemyModel enemyModel;
        private final double distance;

        private NearestEnemy(EnemyModel enemyModel, double distance) {
            this.enemyModel = enemyModel;
            this.distance = distance;
        }
    }
}