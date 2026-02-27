package item.model;

public class PotionModel extends ItemModel {

    private final int healAmount;

    public PotionModel(String name, int healAmount) {
        super(name);
        this.healAmount = healAmount;
    }
    
    @Override
    public void run() { // From [Model]
    	System.out.println("Potion#run");
    }

    /*
    @Override
    public void use(Hero h) {
        int before = h.getHp();
        h.setHp(before + healAmount);
        System.out.println("You drink the " + getName()
                + " and heal " + healAmount + " hp (" + before + " -> " + h.getHp() + ").");
        h.removeFromInventory(this);
    }
    */

    @Override
    public String getDescription() {
        return getName() + " (potion, +" + healAmount + " hp)";
    }
}
