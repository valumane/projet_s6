package common.item;

import common.entity.Hero;

public class Scroll extends Item {
    private final Spell spell;

    public Scroll(String name, Spell spell) {
        super(name);
        this.spell = spell;
    }

    public void use(Hero h) {
    	// TODO Auto-generated method stub
    	// Faire apprendre la commande du sort au héro
    	// -> ajouter une liste de commande au héro au préalable
        h.removeFromInventory(this);
    }

    @Override
    public String getDescription() {
        return getName() + " (scroll: " + spell.getName() + ")";
    }
}
