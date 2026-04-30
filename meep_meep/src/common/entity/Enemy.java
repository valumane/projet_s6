package common.entity;

public abstract class Enemy extends Character {

    private final int maxHp;
    private final int difficulty;
    private final int damage;
    private final int defense;
    private final double speed;
    private final double detectionRange;
    private final double attackRange;
    private final long attackCooldownNs;

    protected Enemy(
            String name,
            int hp,
            int difficulty,
            int damage,
            int defense,
            double speed,
            double detectionRange,
            double attackRange,
            long attackCooldownNs
    ) {
        super(name, hp);
        this.maxHp = hp;
        this.difficulty = Math.max(1, Math.min(5, difficulty));
        this.damage = damage;
        this.defense = defense;
        this.speed = speed;
        this.detectionRange = detectionRange;
        this.attackRange = attackRange;
        this.attackCooldownNs = attackCooldownNs;
    }

    public abstract String getKind();

    public int getMaxHp() {
        return maxHp;
    }

    public int getDifficulty() {
        return difficulty;
    }

    public int getDamage() {
        return damage;
    }

    public int getDefense() {
        return defense;
    }

    public double getSpeed() {
        return speed;
    }

    public double getDetectionRange() {
        return detectionRange;
    }

    public double getAttackRange() {
        return attackRange;
    }

    public long getAttackCooldownNs() {
        return attackCooldownNs;
    }

    public boolean isAlive() {
        return getHp() > 0;
    }

    public int receiveDamage(int rawDamage) {
        int realDamage = Math.max(1, rawDamage - defense);
        setHp(Math.max(0, getHp() - realDamage));
        return realDamage;
    }
}