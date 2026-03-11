package common.item;

import common.entity.Hero;

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
    }
}
