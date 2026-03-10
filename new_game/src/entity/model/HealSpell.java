package entity.model;

public class HealSpell implements Spell {
    private final int amount;

    public HealSpell(int amount) {
        this.amount = amount;
    }

    @Override
    public String getName() {
        return "Heal";
    }

    @Override
    public void cast(HeroModel target) {
        target.setHp(target.getHp() + amount);
    }
}