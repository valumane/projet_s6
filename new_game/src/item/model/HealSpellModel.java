package item.model;

public class HealSpellModel extends SpellModel  {
    private final int healAmount;

    public HealSpellModel(int healAmount) {
        super("Heal");
        this.healAmount = healAmount;
    }

    @Override
    public void run() { // From [Model]
    	System.out.println("HealSpell#run");
    }
    
    /*
    @Override
    public void cast(Hero caster) {
        int before = caster.getHp();
        int after = before + healAmount;

        caster.setHp(after);
        System.out.println("You cast Heal and recover " + healAmount
                + " hp (" + before + " -> " + caster.getHp() + ").");
    }
    */
}
