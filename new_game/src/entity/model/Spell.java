package entity.model;

import java.io.Serializable;

public interface Spell extends Serializable {
    String getName();
    void cast(HeroModel target);
}