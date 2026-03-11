package entity.controller;

import mvc.Controller;
import common.entity.Hero;
import common.item.Item;
import entity.model.HeroModel;
import entity.view.base.HeroView;

public class HeroController extends Controller {

    public HeroController(HeroModel heroModel, HeroView viewGUI, HeroView viewCLI) {
        super(heroModel, viewGUI, viewCLI); /// Hérité de la	classe [mvc.Controller]
    }

    // Déposer un item (je garde ta signature, mais je corrige le message)
    public void onDropItem(Item itemToDrop) {

        Item dropped = heroModel.drop(itemToDrop.getName());

        if (dropped == null) {
            // on affiche le nom demandé, pas dropped.getName() (car dropped == null)
            viewGUI.showObjectNotFindInInventory(itemToDrop.getName());
            viewCLI.showObjectNotFindInInventory(itemToDrop.getName());
            
        } else {
        	viewGUI.showDropObject(heroModel.getName(), dropped.getName());
            viewCLI.showDropObject(heroModel.getName(), dropped.getName());
            heroModel.getRoom().addItem(dropped);
            
        }
    }

}