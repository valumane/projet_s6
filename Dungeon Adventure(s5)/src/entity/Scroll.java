package entity;

public class Scroll extends Item {
    private final Spell spell;

    public Scroll(String name, Spell spell) {
        super(name);
        this.spell = spell;
    }

    @Override
    public void use(Hero h) {
        h.learnSpell(spell);
        System.out.println("You read the " + getName()
                + " and learn the spell " + spell.getName() + ".");

        h.removeFromInventory(this);
    }

    @Override
    public String getDescription() {
        return getName() + " (scroll: " + spell.getName() + ")";
    }
}
