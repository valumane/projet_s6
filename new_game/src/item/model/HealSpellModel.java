package item.model;

import entity.model.Hero;

public class HealSpellModel extends SpellModel {

    private final int healAmount;

    public HealSpellModel(int healAmount) {
        super("Heal");
        this.healAmount = healAmount;
    }
    
    /*
     * Calcul puis soigne le héro d'un certains nombres de points de vie.
     */ 
    @Override
    public void castSpell(Hero caster) {

        int before = caster.getHp();
        int after = before + healAmount;

        caster.setHp(after);
    }
}