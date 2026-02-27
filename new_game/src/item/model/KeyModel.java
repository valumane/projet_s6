package item.model;

public class KeyModel extends ItemModel {

    public KeyModel(String name) {
        super(name);
    }
    
    @Override
    public void run() { // From [Model]
    	System.out.println("Key#run");
    }

    /*
    @Override
    public void use(Hero h) {
        System.out.println("You look at the " + getName() + ". It might open something...");
    }
    */

    @Override
    public String getDescription() {
        return getName() + " (key)";
    }
}
