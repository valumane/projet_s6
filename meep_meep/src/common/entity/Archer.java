package common.entity;

public class Archer extends Enemy {

    public Archer(int difficulty) {
        super(
                "Archer",
                30 + difficulty * 12,
                difficulty,
                4 + difficulty * 2,
                difficulty,
                0.35 + difficulty * 0.05,
                430,
                280,
                Math.max(450_000_000L, 1_550_000_000L - difficulty * 220_000_000L)
        );
    }

    public int getProjectileCount() {
        return getDifficulty();
    }

    @Override
    public String getKind() {
        return "archer";
    }
}