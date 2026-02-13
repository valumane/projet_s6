package entity;

import java.io.Serializable;

public abstract class Spell implements Serializable {
    private final String name;

    protected Spell(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    /* Lance le sort sur le héros (ici le lanceur lui-même). */
    public abstract void cast(Hero caster);
}
