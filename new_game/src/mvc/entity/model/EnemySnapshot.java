package mvc.entity.model;

public class EnemySnapshot {

    private final String name;
    private final String kind;
    private final double x;
    private final double y;
    private final double facingX;
    private final double facingY;
    private final int hp;
    private final int maxHp;
    private final boolean keyHolder;
    private final boolean highlighted;

    public EnemySnapshot(String name, String kind, double x, double y,
                         double facingX, double facingY,
                         int hp, int maxHp,
                         boolean keyHolder) {
        this(name, kind, x, y, facingX, facingY, hp, maxHp, keyHolder, false);
    }

    public EnemySnapshot(String name, String kind, double x, double y,
                         double facingX, double facingY,
                         int hp, int maxHp,
                         boolean keyHolder,
                         boolean highlighted) {
        this.name = name;
        this.kind = kind;
        this.x = x;
        this.y = y;
        this.facingX = facingX;
        this.facingY = facingY;
        this.hp = hp;
        this.maxHp = maxHp;
        this.keyHolder = keyHolder;
        this.highlighted = highlighted;
    }

    public String getName() {
        return name;
    }

    public String getKind() {
        return kind;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getFacingX() {
        return facingX;
    }

    public double getFacingY() {
        return facingY;
    }

    public int getHp() {
        return hp;
    }

    public int getMaxHp() {
        return maxHp;
    }

    public boolean isKeyHolder() {
        return keyHolder;
    }

    public boolean isHighlighted() {
        return highlighted;
    }
}