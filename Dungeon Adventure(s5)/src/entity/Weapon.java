package entity;

public class Weapon extends Item {

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
        h.setWeapon(this);
        System.out.println("You equip the " + getName() + " (+" + damage + " dmg).");
    }

    @Override
    public String getDescription() {
        return getName() + " (weapon, +" + damage + " dmg)";
    }
}
