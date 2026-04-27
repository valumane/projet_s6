package mvc.entity.model;

import mvc.map.MapLayout;
import java.util.List;

public class ProjectileModel {

    public enum Owner {
        HERO,
        ENEMY
    }

    public static final double PROJECTILE_RADIUS = 6;

    private double x;
    private double y;
    private final double vx;
    private final double vy;
    private final int damage;
    private final Owner owner;
    private boolean alive = true;

    public ProjectileModel(double x, double y, double vx, double vy, int damage) {
        this(x, y, vx, vy, damage, Owner.ENEMY);
    }

    public ProjectileModel(double x, double y, double vx, double vy, int damage, Owner owner) {
        this.x = x;
        this.y = y;
        this.vx = vx;
        this.vy = vy;
        this.damage = damage;
        this.owner = owner;
    }

    public void update(HeroModel heroModel) {
        update(List.of(heroModel));
    }

    public void update(List<HeroModel> heroes) {
        if (!alive) {
            return;
        }

        x += vx;
        y += vy;

        double minX = MapLayout.ROOM_X;
        double maxX = MapLayout.ROOM_X + MapLayout.ROOM_W;
        double minY = MapLayout.ROOM_Y;
        double maxY = MapLayout.ROOM_Y + MapLayout.ROOM_H;

        if (x < minX || x > maxX || y < minY || y > maxY) {
            alive = false;
            return;
        }

        if (owner != Owner.ENEMY) {
            return;
        }

        if (heroes == null) {
            return;
        }

        for (HeroModel heroModel : heroes) {
            if (heroModel == null || heroModel.getHealth() <= 0) {
                continue;
            }

            double dx = heroModel.getX() - x;
            double dy = heroModel.getY() - y;
            double distance = Math.hypot(dx, dy);

            if (distance <= MapLayout.HERO_RADIUS + PROJECTILE_RADIUS) {
                heroModel.removeHp(damage);
                alive = false;
                return;
            }
        }
    }

    public boolean isAlive() {
        return alive;
    }

    public boolean isFromHero() {
        return owner == Owner.HERO;
    }

    public int getDamage() {
        return damage;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public void kill() {
        alive = false;
    }

    public ProjectileSnapshot snapshot() {
        return new ProjectileSnapshot(x, y, isFromHero());
    }
}