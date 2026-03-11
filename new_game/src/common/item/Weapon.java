package common.item;

import common.entity.Hero;

public class Weapon extends UsableItem {

    private final int damage;

    public Weapon(String name, int damage) {
        super(name);
        this.damage = damage;
    }

    public int getDamage() {
        return damage;
    }

    @Override
    public void use(Hero h) {
        h.addItem(this);
    }

    @Override
    public String getDescription() {
        return getName() + " (weapon, +" + damage + " dmg)";
    }
}
