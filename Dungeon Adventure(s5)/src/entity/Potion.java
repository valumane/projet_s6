package entity;

import java.io.Serializable;

public class Potion extends Item implements Serializable  {

    private final int healAmount;

    public Potion(String name, int healAmount) {
        super(name);
        this.healAmount = healAmount;
    }

    @Override
    public void use(Hero h) {
        int before = h.getHp();
        h.setHp(before + healAmount);
        System.out.println("You drink the " + getName()
                + " and heal " + healAmount + " hp (" + before + " -> " + h.getHp() + ").");
        h.removeFromInventory(this);
    }

    @Override
    public String getDescription() {
        return getName() + " (potion, +" + healAmount + " hp)";
    }
}
