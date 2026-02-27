package item.model;

import java.io.Serializable;
import mvc.Model;

public abstract class SpellModel implements Model, Serializable {
    private final String name;

    protected SpellModel(String name) {
        this.name = name;
    }
    
    @Override
    public void run() { // From [Model]
    	System.out.println("Spell#run");
    }
    
    public String getName() {
        return name;
    }

    /* Lance le sort sur le héros (ici le lanceur lui-même). */
    // public abstract void cast(Hero caster);
}
