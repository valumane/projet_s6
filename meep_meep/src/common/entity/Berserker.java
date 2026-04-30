package common.entity;

public class Berserker extends Enemy {

    public Berserker(int difficulty) {
        super(
                "Berserker",
                45 + difficulty * 20,
                difficulty,
                7 + difficulty * 3,
                difficulty * 2,
                0.85 + difficulty * 0.15,
                360,
                38,
                700_000_000L
        );
    }

    @Override
    public String getKind() {
        return "berserker";
    }
}