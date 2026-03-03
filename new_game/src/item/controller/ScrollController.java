package item.controller;

import item.model.ScrollModel;
import item.view.ScrollView;
// import entity.model.Hero;

public class ScrollController {

    private final ScrollModel model;
    private final ScrollView view1;
    private final ScrollView view2;

    public ScrollController(ScrollModel model, ScrollView view1, ScrollView view2) {
        this.model = model;
        this.view1 = view1;
        this.view2 = view2;
    }
    
    /*
     * Utilisation du Scroll
     * (pas encore la classe héro mais t'as capté)
    public void onUse(Hero hero) {
        hero.learnSpell(model.getSpell());
        // Appel des deux vues
        view1.displayOnUse(model);
        view2.displayOnUse(model);
        
        hero.removeFromInventory(model);
    }
    */
    
    
}