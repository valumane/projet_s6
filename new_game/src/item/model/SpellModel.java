package item.model;

import java.io.Serializable;
import entity.model.Hero;

public abstract class SpellModel implements Serializable {
	
    private final String name;

    protected SpellModel(String name) {
        this.name = name;
    }
    
    public String getName() {
        return name;
    }

    public abstract void castSpell(Hero caster);
}
