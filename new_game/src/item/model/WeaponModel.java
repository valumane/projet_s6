package item.model;

public class WeaponModel extends ItemModel {

    private final int damage;

    public WeaponModel(String name, int damage) {
        super(name);
        this.damage = damage;
    }

    public int getDamage() {
        return damage;
    }

    /*
    @Override
    public void use(Hero h) {
        h.setWeapon(this);
        System.out.println("You equip the " + getName() + " (+" + damage + " dmg).");
    }
    */

    @Override
    public String getDescription() {
        return getName() + " (weapon, +" + damage + " dmg)";
    }
}
