package entity;

import java.io.Serializable;

public class Monster extends Character implements Serializable {

    public Monster(String name, int hp) {
        super(name, hp);
    }
}
