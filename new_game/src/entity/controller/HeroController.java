package entity.controller;

import mvc.Controller;
import entity.model.HeroModel;
import entity.model.Item;
import entity.view.base.HeroView;

public class HeroController extends Controller {

    private final HeroModel hero;
    private final HeroView view1;
    private final HeroView view2;

    public HeroController(HeroModel hero, HeroView view1, HeroView view2) {
        super(hero, view1);          // obligatoire (Controller n’a pas de super() vide)
        this.hero = hero;
        this.view1 = view1;
        this.view2 = view2;
    }

    // Déposer un item (je garde ta signature, mais je corrige le message)
    public void onDropItem(Item itemToDrop) {

        Item dropped = hero.drop(itemToDrop.getName());

        if (dropped == null) {
            // on affiche le nom demandé, pas dropped.getName() (car dropped == null)
            view1.showObjectNotFindInInventory(itemToDrop.getName());
            view2.showObjectNotFindInInventory(itemToDrop.getName());
        } else {
            view1.showDropObject(hero.getName(), dropped.getName());
            view2.showDropObject(hero.getName(), dropped.getName());
            hero.getLocation().addItem(dropped);
        }
    }

}