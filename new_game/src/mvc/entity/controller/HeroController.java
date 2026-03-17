package entity.controller;

import mvc.Controller;
import common.item.Item;
import entity.model.HeroModel;
import entity.view.base.HeroView;

public class HeroController extends Controller {

    private final HeroModel heroModel;
    private final HeroView viewGUI;
    private final HeroView viewCLI;

    public HeroController(HeroModel heroModel, HeroView viewGUI, HeroView viewCLI) {
        super(heroModel, viewGUI, viewCLI);
        this.heroModel = heroModel;
        this.viewGUI = viewGUI;
        this.viewCLI = viewCLI;
    }

    // Déposer un item
    public void onDropItem(Item itemToDrop) {
        Item dropped = heroModel.drop(itemToDrop.getName());

        if (dropped == null) {
            viewGUI.showObjectNotFindInInventory(itemToDrop.getName());
            viewCLI.showObjectNotFindInInventory(itemToDrop.getName());
        } else {
            viewGUI.showDropObject(heroModel.getName(), dropped.getName());
            viewCLI.showDropObject(heroModel.getName(), dropped.getName());
        }
    }

}