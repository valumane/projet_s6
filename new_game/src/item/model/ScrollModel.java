package item.model;

public class ScrollModel extends ItemModel {
    private final SpellModel spell;

    public ScrollModel(String name, SpellModel spell) {
        super(name);
        this.spell = spell;
    }

    @Override
    public void run() { // From [Model]
    	System.out.println("Scroll#run");
    }
    
    /* 
    @Override
    public void use(Hero h) {
        h.learnSpell(spell);
        System.out.println("You read the " + getName()
                + " and learn the spell " + spell.getName() + ".");

        h.removeFromInventory(this);
    }
    */

    @Override
    public String getDescription() {
        return getName() + " (scroll: " + spell.getName() + ")";
    }
}
