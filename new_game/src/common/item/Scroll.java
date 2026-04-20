package common.item;

import common.entity.Hero;

public class Scroll extends UsableItem {
    private final Spell spell;

    public Scroll(String name, Spell spell) {
        super(name);
        this.spell = spell;
    }

    @Override
    public void use(Hero h) {
        spell.cast(h);
        h.removeFromInventory(this);
    }

    @Override
    public String getDescription() {
        return getName() + " (scroll: " + spell.getName() + ")";
    }
}