package common.item;

import java.io.Serializable;

import common.entity.Hero;

public abstract class Spell implements Serializable {
    private final String name;

    protected Spell(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    /* Lance le sort sur le héros (ici le lanceur lui-même). */
    // Y'a moyen que cast dégage pour un autre système :
    //  Liste de commandes dans le héro et apprendre un sort ajoute une commande
    public abstract void cast(Hero caster);
}
