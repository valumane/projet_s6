package mvc.entity.controller;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import common.entity.Enemy;
import common.language.Language;
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
    private long nextSecondPlayerAttackTime = 0L;
    private final HeroModel secondHeroModel;

    public CombatController(
            RoomModel roomModel,
            HeroModel heroModel,
            HeroModel secondHeroModel,
            RoomView viewCLI,
            RoomView viewGUI,
            Runnable onBossRoomCleared,
            Runnable refreshRoomViews) {
        super(roomModel, viewCLI, viewGUI);

        this.roomModel = roomModel;
        this.heroModel = heroModel;
        this.secondHeroModel = secondHeroModel;
        this.viewCLI = viewCLI;
        this.viewGUI = viewGUI;
        this.onBossRoomCleared = onBossRoomCleared;
        this.refreshRoomViews = refreshRoomViews;
    }

    public CombatController(
            RoomModel roomModel,
            HeroModel heroModel,
            RoomView viewCLI,
            RoomView viewGUI,
            Runnable onBossRoomCleared,
            Runnable refreshRoomViews) {
        this(roomModel, heroModel, null, viewCLI, viewGUI, onBossRoomCleared, refreshRoomViews);
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

        updatePlayerAutoAttack(heroModel, false, now);

        if (secondHeroModel != null) {
            updatePlayerAutoAttack(secondHeroModel, true, now);
        }

        for (EnemyModel enemyModel : getCurrentEnemyModels()) {
            HeroModel target = findNearestHero(enemyModel.getX(), enemyModel.getY());

            if (target != null) {
                enemyModel.update(target, now, projectiles);
            }
        }

        for (ProjectileModel projectile : projectiles) {
            projectile.update(getAliveHeroes());
        }

        updateHeroProjectileHits(now);

        projectiles.removeIf(projectile -> !projectile.isAlive());

        currentRoom.getCharacters().removeIf(character -> character instanceof Enemy enemy && !enemy.isAlive());

        enemyModels.entrySet().removeIf(entry -> !entry.getKey().isAlive());

        updateHeroAutoFacing();
        displayEnemiesAndProjectiles();
    }

    public void attackNearby() {
        attackNearby(heroModel, false);
    }

    public void secondAttackNearby() {
        if (secondHeroModel == null) {
            return;
        }

        attackNearby(secondHeroModel, true);
    }

    private void attackNearby(HeroModel attacker, boolean secondPlayer) {
        Room currentRoom = roomModel.getRoom();

        if (currentRoom == null || attacker == null) {
            return;
        }

        if (attacker.getEquippedWeapon() == null) {

            displayMessage(Language.tf("combat.noWeapon", attacker.getName()));
            return;
        }

        NearestEnemy nearestEnemy = findNearestEnemy(attacker);

        if (nearestEnemy == null) {
            return;
        }

        long now = System.nanoTime();

        if (isPlayerAttackOnCooldown(secondPlayer, now)) {
            return;
        }

        if (attacker.isEquippedWeaponRanged()) {
            if (nearestEnemy.distance > PLAYER_BOW_RANGE) {
                return;
            }

            setNextPlayerAttackTime(secondPlayer, now + PLAYER_BOW_COOLDOWN_NS);
            shootHeroArrow(attacker, nearestEnemy.enemyModel, now, secondPlayer);
            return;
        }

        if (nearestEnemy.distance > PLAYER_SWORD_RANGE) {
            return;
        }

        setNextPlayerAttackTime(secondPlayer, now + PLAYER_SWORD_COOLDOWN_NS);
        attackEnemy(attacker, nearestEnemy.enemyModel, attacker.getEquippedWeaponName(), now, secondPlayer);
    }

    private boolean isPlayerAttackOnCooldown(boolean secondPlayer, long now) {
        return now < (secondPlayer ? nextSecondPlayerAttackTime : nextPlayerAttackTime);
    }

    private void setNextPlayerAttackTime(boolean secondPlayer, long value) {
        if (secondPlayer) {
            nextSecondPlayerAttackTime = value;
        } else {
            nextPlayerAttackTime = value;
        }
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
                        .toList());

        viewGUI.displayProjectiles(
                projectiles.stream()
                        .map(ProjectileModel::snapshot)
                        .toList());
    }

    private void updateHeroAutoFacing() {
        NearestEnemy nearest = findNearestEnemy(heroModel);

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

    private NearestEnemy findNearestEnemy(HeroModel attacker) {
        if (attacker == null) {
            return null;
        }

        EnemyModel nearestEnemy = null;
        double bestDistance = Double.MAX_VALUE;

        for (EnemyModel enemyModel : getCurrentEnemyModels()) {
            double dx = attacker.getX() - enemyModel.getX();
            double dy = attacker.getY() - enemyModel.getY();
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

    private void updatePlayerAutoAttack(HeroModel attacker, boolean secondPlayer, long now) {
        Room currentRoom = roomModel.getRoom();

        if (currentRoom == null || attacker == null || attacker.getHealth() <= 0) {
            return;
        }

        if (attacker.getEquippedWeapon() == null) {
            return;
        }

        if (isPlayerAttackOnCooldown(secondPlayer, now)) {
            return;
        }

        NearestEnemy nearestEnemy = findNearestEnemy(attacker);

        if (nearestEnemy == null) {
            return;
        }

        double dx = nearestEnemy.enemyModel.getX() - attacker.getX();
        double dy = nearestEnemy.enemyModel.getY() - attacker.getY();
        double distance = Math.hypot(dx, dy);

        if (distance > 0.001) {
            if (secondPlayer) {
                viewGUI.displaySecondHeroFacing(dx / distance, dy / distance);
            } else {
                viewGUI.displayHeroFacing(dx / distance, dy / distance);
            }
        }

        if (attacker.isEquippedWeaponRanged()) {
            if (nearestEnemy.distance <= PLAYER_BOW_RANGE) {
                setNextPlayerAttackTime(secondPlayer, now + PLAYER_BOW_COOLDOWN_NS);
                shootHeroArrow(attacker, nearestEnemy.enemyModel, now, secondPlayer);
            }

            return;
        }

        if (nearestEnemy.distance <= PLAYER_SWORD_RANGE) {
            setNextPlayerAttackTime(secondPlayer, now + PLAYER_SWORD_COOLDOWN_NS);
            attackEnemy(attacker, nearestEnemy.enemyModel, attacker.getEquippedWeaponName(), now, secondPlayer);
        }
    }

    private void shootHeroArrow(HeroModel attacker, EnemyModel target, long now, boolean secondPlayer) {
        double dx = target.getX() - attacker.getX();
        double dy = target.getY() - attacker.getY();
        double distance = Math.hypot(dx, dy);

        if (distance <= 0.001) {
            return;
        }

        double vx = dx / distance * PLAYER_ARROW_SPEED;
        double vy = dy / distance * PLAYER_ARROW_SPEED;

        int damage = Math.max(1, attacker.getDamage());

        projectiles.add(new ProjectileModel(
                attacker.getX(),
                attacker.getY(),
                vx,
                vy,
                damage,
                ProjectileModel.Owner.HERO));

        if (secondPlayer) {
            viewGUI.displaySecondHeroFacing(dx / distance, dy / distance);
            viewGUI.displaySecondHeroAttackFlash();
        } else {
            viewGUI.displayHeroFacing(dx / distance, dy / distance);
            viewGUI.displayHeroAttackFlash();
        }

        target.flashHit(now);

        displayMessage(Language.tf("combat.shootsArrow", attacker.getName(), target.getEnemy().getName()));

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

                    displayMessage(Language.tf("combat.arrowHits", enemyModel.getEnemy().getName(), realDamage));

                    if (!enemyModel.isAlive()) {
                        handleEnemyDefeated(enemyModel);
                    }

                    break;
                }
            }
        }
    }

    private void attackEnemy(HeroModel attacker, EnemyModel enemyModel, String attackName, long now,
                             boolean secondPlayer) {
        int damage = attacker.getDamage();
        int realDamage = enemyModel.receiveDamage(damage);

        enemyModel.flashHit(now);

        if (secondPlayer) {
            viewGUI.displaySecondHeroAttackFlash();
        } else {
            viewGUI.displayHeroAttackFlash();
        }

        displayMessage(Language.tf("combat.meleeAttack", attacker.getName(), enemyModel.getEnemy().getName(), attackName, realDamage));


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

        displayMessage(Language.tf("combat.defeated", enemyModel.getEnemy().getName()));

        for (HeroModel hero : getAliveHeroes()) {
            applyKillReward(hero);
        }

        checkBossRoomCleared(currentRoom);

        if (refreshRoomViews != null) {
            refreshRoomViews.run();
        }
    }

    private void applyKillReward(HeroModel rewardedHero) {
        if (rewardedHero == null || rewardedHero.getHealth() <= 0) {
            return;
        }

        rewardedHero.healPercent(10);
        rewardedHero.increaseDamageByPercent(20);

        displayMessage(Language.tf("combat.killReward",
                rewardedHero.getName(),
                rewardedHero.getHealth(), rewardedHero.getMaxHealth(),
                rewardedHero.getDamage()));
    }

    private void dropEnemyInventory(Enemy enemy, Room room) {
        List<Item> loot = new ArrayList<>(enemy.getInventory());

        if (loot.isEmpty()) {
            return;
        }

        for (Item item : loot) {
            enemy.removeFromInventory(item);
            room.addItem(item);

            displayMessage(Language.tf("combat.dropped", enemy.getName(), item.getName()));
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

            String message = Language.tf("combat.bossHP", bonus,
                    heroModel.getHealth(), heroModel.getMaxHealth());

            displayMessage(message);
        } else {
            int bonus = 5;
            heroModel.increaseBaseDamage(bonus);

            String message = Language.tf("combat.bossDamage", bonus,
                    heroModel.getDamage());

            displayMessage(message);
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

    private List<HeroModel> getAliveHeroes() {
        List<HeroModel> heroes = new ArrayList<>();

        if (heroModel != null && heroModel.getHealth() > 0) {
            heroes.add(heroModel);
        }

        if (secondHeroModel != null && secondHeroModel.getHealth() > 0) {
            heroes.add(secondHeroModel);
        }

        return heroes;
    }

    private HeroModel findNearestHero(double x, double y) {
        HeroModel nearest = null;
        double bestDistance = Double.MAX_VALUE;

        for (HeroModel hero : getAliveHeroes()) {
            double dx = hero.getX() - x;
            double dy = hero.getY() - y;
            double distance = Math.hypot(dx, dy);

            if (distance < bestDistance) {
                bestDistance = distance;
                nearest = hero;
            }
        }

        return nearest;
    }

    private void displayMessage(String message) {
        viewCLI.displayMessage(message);
        viewGUI.displayMessage(message);
    }

}