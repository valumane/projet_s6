package item.view;

import item.model.ScrollModel;

public class ScrollViewCLI extends ScrollView{

    public void displayOnUse(ScrollModel model) {
        System.out.println("You read the " + model.getName()
                + " and learn the spell " + model.getSpell().getName() + ".");
    }
}