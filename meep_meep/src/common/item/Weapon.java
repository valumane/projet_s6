package common.item;

import common.entity.Hero;

public class Weapon extends UsableItem {

    public enum WeaponType {
        MELEE,
        RANGED
    }

    private final int damage;
    private final WeaponType type;

    public Weapon(String name, int damage) {
        this(name, damage, WeaponType.MELEE);
    }

    public Weapon(String name, int damage, WeaponType type) {
        super(name);
        this.damage = damage;
        this.type = type;
    }

    public int getDamage() {
        return damage;
    }

    public WeaponType getType() {
        return type;
    }

    public boolean isRanged() {
        return type == WeaponType.RANGED;
    }

    public boolean isMelee() {
        return type == WeaponType.MELEE;
    }

    @Override
    public void use(Hero h) {
        if (h == null) {
            return;
        }

        if (!h.getInventory().contains(this)) {
            h.addItem(this);
        }

        h.equipWeapon(this);
    }

    @Override
    public String getDescription() {
        String typeName = isRanged() ? "ranged" : "melee";
        return getName() + " (weapon, " + typeName + ", +" + damage + " dmg)";
    }
}