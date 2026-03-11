package entity.controller;

import mvc.Controller;
import common.entity.Hero;
import common.item.Item;
import entity.model.HeroModel;
import entity.view.base.HeroView;

public class HeroController extends Controller {

    public HeroController(HeroModel heroModel, HeroView viewGUI, HeroView viewCLI) {
        super(heroModel, viewGUI, viewCLI); /// Hérité de la classe [mvc.Controller]
    }

    public void onDropItem(Item item) {

        boolean haveItem = heroModel.containsItem(item);

        // Si le héro a l'item sur lui on le dépose dans la salle
        // Sinon on informe le joueur qu'il n'a pas l'item et ne peut donc pas le supprimer
        if (haveItem) {
            heroModel.drop(item);
            viewGUI.showDropObject(heroModel.getName(), item.getName());
            viewCLI.showDropObject(heroModel.getName(), item.getName());
        } 
        else {
            viewGUI.showObjectNotFindInInventory(item.getName());
            viewCLI.showObjectNotFindInInventory(item.getName());
        }
    }
}