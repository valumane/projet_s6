package item.view;

public class ScrollViewCLI extends ScrollView{

	@Override
    public void displayOnUse(String name, String spellName) {
        System.out.println("You read the " + name
                + " and learn the spell " + spellName + ".");
    }
}