package item.model;

public class HealSpellModel extends SpellModel {

    private final int healAmount;

    public HealSpellModel(int healAmount) {
        super("Heal");
        this.healAmount = healAmount;
    }
    
    // temp le temps d'avoir la classe hero
    public void castSpell() {}

    /*
     * 
     * 
    @Override
    public int castSpell(Hero caster) {

        int before = caster.getHp();
        int after = before + healAmount;

        caster.setHp(after);
    }
    */
}