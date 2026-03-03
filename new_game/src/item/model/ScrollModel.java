package item.model;

public class ScrollModel extends ItemModel {
    private final SpellModel spell;

    public ScrollModel(String name, SpellModel spell) {
        super(name);
        this.spell = spell;
    }

    @Override
    public String getDescription() {
        return getName() + " (scroll: " + spell.getName() + ")";
    }
    
    public SpellModel getSpell() {
        return spell;
    }
}
