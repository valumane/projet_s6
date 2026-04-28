package entity;

public class Key extends Item {

    public Key(String name) {
        super(name);
    }

    @Override
    public void use(Hero h) {
        System.out.println("You look at the " + getName() + ". It might open something...");
    }

    @Override
    public String getDescription() {
        return getName() + " (key)";
    }
}
