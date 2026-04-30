package common.entity;

import java.util.List;

import common.item.Bag;
import common.item.Item;
import common.item.Weapon;
import common.map.Room;

public class Hero extends Character {

    private final Bag backpack;
    private Room room;

    private int baseDamage;
    private int maxHp;
    private double damageMultiplier = 1.0;

    private Weapon equippedWeapon;

    public Hero(String name, int hp, Bag backpack, Room room, int baseDamage) {
        super(name, hp);
        this.backpack = backpack;
        this.room = room;
        this.baseDamage = baseDamage;
        this.maxHp = hp;
    }

    public Bag getBackpack() {
        return this.backpack;
    }

    public boolean addItem(Item item) {
        return this.backpack.addItem(item);
    }

    public Item dropItem(String itemName) {
        Item found = null;

        for (Item item : this.getInventory()) {
            if (item.getName().equalsIgnoreCase(itemName)) {
                found = item;
                break;
            }
        }

        if (found == null) {
            return null;
        }

        if (found == equippedWeapon) {
            equippedWeapon = null;
        }

        this.removeFromInventory(found);

        if (this.room != null) {
            this.room.addItem(found);
        }

        return found;
    }

    public boolean equipWeapon(Weapon weapon) {
        if (weapon == null) {
            return false;
        }

        if (!getInventory().contains(weapon)) {
            return false;
        }

        this.equippedWeapon = weapon;
        return true;
    }

    public Weapon getEquippedWeapon() {
        return equippedWeapon;
    }

    public String getEquippedWeaponName() {
        return equippedWeapon == null ? "aucune" : equippedWeapon.getName();
    }

    public boolean isEquippedWeaponRanged() {
        return equippedWeapon != null && equippedWeapon.isRanged();
    }

    public boolean isEquippedWeaponMelee() {
        return equippedWeapon != null && equippedWeapon.isMelee();
    }

    public Room getRoom() {
        return this.room;
    }

    public void setCurrentRoom(Room r) {
        this.room = r;
    }

    @Override
    public List<Item> getInventory() {
        return this.backpack.getContent();
    }

    @Override
    public void addToInventory(Item item) {
        addItem(item);
    }

    @Override
    public void removeFromInventory(Item item) {
        if (item == equippedWeapon) {
            equippedWeapon = null;
        }

        this.backpack.removeItem(item);
    }

    public int getDamage() {
        int weaponDamage = equippedWeapon == null ? 0 : equippedWeapon.getDamage();
        int rawDamage = baseDamage + weaponDamage;

        return Math.max(1, (int) Math.round(rawDamage * damageMultiplier));
    }

    public int getMaxHp() {
        return maxHp;
    }

    public void increaseMaxHp(int amount) {
        if (amount <= 0) {
            return;
        }

        maxHp += amount;
        setHp(Math.min(maxHp, getHp() + amount));
    }

    public void increaseBaseDamage(int amount) {
        if (amount <= 0) {
            return;
        }

        baseDamage += amount;
    }

    public void increaseDamagePercent(int percent) {
        if (percent <= 0) {
            return;
        }

        damageMultiplier *= 1.0 + percent / 100.0;
    }

    public void healPercentOfMaxHp(int percent) {
        if (percent <= 0) {
            return;
        }

        int healAmount = Math.max(1, maxHp * percent / 100);
        setHp(Math.min(maxHp, getHp() + healAmount));
    }
}