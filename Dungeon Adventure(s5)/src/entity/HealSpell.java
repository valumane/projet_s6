package entity;

public class HealSpell extends Spell {
    private final int healAmount;

    public HealSpell(int healAmount) {
        super("Heal");
        this.healAmount = healAmount;
    }

    @Override
    public void cast(Hero caster) {
        int before = caster.getHp();
        int after = before + healAmount;

        caster.setHp(after);
        System.out.println("You cast Heal and recover " + healAmount
                + " hp (" + before + " -> " + caster.getHp() + ").");
    }
}
