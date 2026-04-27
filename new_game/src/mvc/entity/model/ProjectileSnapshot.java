package mvc.entity.model;

public class ProjectileSnapshot {

    private final double x;
    private final double y;
    private final boolean fromHero;

    public ProjectileSnapshot(double x, double y) {
        this(x, y, false);
    }

    public ProjectileSnapshot(double x, double y, boolean fromHero) {
        this.x = x;
        this.y = y;
        this.fromHero = fromHero;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public boolean isFromHero() {
        return fromHero;
    }
}