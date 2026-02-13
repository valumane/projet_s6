package entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Character implements Serializable {
    private final String name;
    private int hp;
    private int baseDamage;
    private final List<Item> inventory;
    private Weapon weapon; // arme équipée (peut être null)

    public Character(String name, int hp) {
        this(name, hp, 10);
    }

    public Character(String name, int hp, int baseDamage) {
        this.name = name;
        this.hp = hp;
        this.baseDamage = baseDamage;
        this.inventory = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public int getHp() {
        return hp;
    }

    public void setHp(int hp) {
        this.hp = hp;
    }

    public int getBaseDamage() {
        return baseDamage;
    }

    public void setBaseDamage(int baseDamage) {
        this.baseDamage = baseDamage;
    }

    public Weapon getWeapon() {
        return weapon;
    }

    public void setWeapon(Weapon weapon) {
        this.weapon = weapon;
        if (weapon != null && !inventory.contains(weapon)) {
            inventory.add(weapon);
        }
    }

    public void attack(Character c) {
        int damage = getAttackDamage();
        c.setHp(c.getHp() - damage);
        System.out.println(getName() + " attacks " + c.getName()
                + " for " + damage + " damage. (" + c.getHp() + " hp left)");
    }

    public int getAttackDamage() {
        int damage = baseDamage;
        if (weapon != null) {
            damage += weapon.getDamage();
        }
        return damage;
    }

    public void take(Item i) {
        inventory.add(i);
    }

    public List<Item> getInventory() {
        return inventory;
    }

    public boolean removeFromInventory(Item i) {
        return inventory.remove(i);
    }
}
