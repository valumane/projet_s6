package mvc.entity.model;

import java.util.List;

import common.entity.Archer;
import common.entity.Berserker;
import common.entity.Enemy;
import mvc.map.MapLayout;
import mvc.mvc.Model;
import common.item.Key;

public class EnemyModel implements Model {

    private static final double PROJECTILE_SPEED = 4.8;

    private final Enemy enemy;

    private double x;
    private double y;

    private double facingX = -1;
    private double facingY = 0;

    private long nextAttackTime = 0L;

    private static final long ENEMY_ROTATION_COOLDOWN_NS = 450_000_000L;
    private long nextFacingUpdateTime = 0L;

    private static final long HIT_FLASH_NS = 160_000_000L;
    private long hitFlashUntil = 0L;

    public EnemyModel(Enemy enemy, double x, double y) {
        this.enemy = enemy;
        this.x = x;
        this.y = y;
    }

    @Override
    public void run() {
    }

    public Enemy getEnemy() {
        return enemy;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public boolean isAlive() {
        return enemy.isAlive();
    }

    public int receiveDamage(int damage) {
        return enemy.receiveDamage(damage);
    }

    public void update(HeroModel heroModel, long now, List<ProjectileModel> projectiles) {
        if (!enemy.isAlive()) {
            return;
        }

        double dx = heroModel.getX() - x;
        double dy = heroModel.getY() - y;
        double distance = Math.hypot(dx, dy);

        updateFacingTowardHero(dx, dy, distance, now);

        if (enemy instanceof Berserker) {
            updateBerserker(heroModel, now, distance);
        } else if (enemy instanceof Archer archer) {
            updateArcher(heroModel, archer, now, distance, projectiles);
        }
    }

    private void updateFacingTowardHero(double dx, double dy, double distance, long now) {
        if (distance <= 0.001) {
            return;
        }

        if (now < nextFacingUpdateTime) {
            return;
        }

        facingX = dx / distance;
        facingY = dy / distance;

        nextFacingUpdateTime = now + ENEMY_ROTATION_COOLDOWN_NS;
    }

    private void updateBerserker(HeroModel heroModel, long now, double distance) {
        if (distance > enemy.getDetectionRange()) {
            return;
        }

        if (distance > enemy.getAttackRange()) {
            moveTowardHero();
            return;
        }

        if (now >= nextAttackTime) {
            heroModel.removeHp(enemy.getDamage());
            nextAttackTime = now + enemy.getAttackCooldownNs();
        }
    }

    private void updateArcher(HeroModel heroModel, Archer archer, long now, double distance,
            List<ProjectileModel> projectiles) {
        if (distance > enemy.getDetectionRange()) {
            return;
        }

        if (distance < 120) {
            moveAwayFromHero();
        }

        if (now >= nextAttackTime) {
            shootProjectileBurst(archer, projectiles);
            nextAttackTime = now + enemy.getAttackCooldownNs();
        }
    }

    private void shootProjectileBurst(Archer archer, List<ProjectileModel> projectiles) {
        int count = archer.getProjectileCount();

        double baseAngle = Math.atan2(facingY, facingX);
        double spread = Math.toRadians(9);

        double startAngle = baseAngle - spread * (count - 1) / 2.0;

        for (int i = 0; i < count; i++) {
            double angle = startAngle + i * spread;

            double vx = Math.cos(angle) * PROJECTILE_SPEED;
            double vy = Math.sin(angle) * PROJECTILE_SPEED;

            projectiles.add(new ProjectileModel(x, y, vx, vy, enemy.getDamage()));
        }
    }

    private void moveTowardHero() {
        x += facingX * enemy.getSpeed();
        y += facingY * enemy.getSpeed();
        clampPosition();
    }

    private void moveAwayFromHero() {
        x -= facingX * enemy.getSpeed();
        y -= facingY * enemy.getSpeed();
        clampPosition();
    }

    private void clampPosition() {
        double minX = MapLayout.ROOM_X + MapLayout.HERO_RADIUS;
        double maxX = MapLayout.ROOM_X + MapLayout.ROOM_W - MapLayout.HERO_RADIUS;
        double minY = MapLayout.ROOM_Y + MapLayout.HERO_RADIUS;
        double maxY = MapLayout.ROOM_Y + MapLayout.ROOM_H - MapLayout.HERO_RADIUS;

        x = Math.max(minX, Math.min(maxX, x));
        y = Math.max(minY, Math.min(maxY, y));
    }

    public EnemySnapshot snapshot() {
        long now = System.nanoTime();

        boolean keyHolder = enemy.getInventory().stream()
                .anyMatch(item -> item instanceof Key);

        return new EnemySnapshot(
                enemy.getName(),
                enemy.getKind(),
                x,
                y,
                facingX,
                facingY,
                enemy.getHp(),
                enemy.getMaxHp(),
                keyHolder,
                now < hitFlashUntil);
    }

    public void flashHit(long now) {
        hitFlashUntil = now + HIT_FLASH_NS;
    }
}