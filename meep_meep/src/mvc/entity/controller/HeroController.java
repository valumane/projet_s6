package mvc.entity.controller;

import common.item.Item;
import mvc.entity.model.HeroModel;
import mvc.entity.view.base.HeroView;
import mvc.mvc.Controller;

public class HeroController extends Controller {

    private final HeroModel heroModel;
    private final HeroView viewGUI;
    private final HeroView viewCLI;

    public HeroController(HeroModel heroModel, HeroView viewCLI, HeroView viewGUI) {
        super(heroModel, viewCLI, viewGUI);

        this.heroModel = heroModel;
        this.viewGUI = viewGUI;
        this.viewCLI = viewCLI;

        this.heroModel.addListener(new HeroModel.Listener() {
            @Override
            public void onHealthChanged(int newHp) {
                HeroController.this.viewGUI.showHealth(newHp);
                HeroController.this.viewCLI.showHealth(newHp);
            }

            @Override
            public void onLocationChanged(String newLocation) {
                HeroController.this.viewGUI.showLocation(newLocation);
                HeroController.this.viewCLI.showLocation(newLocation);
            }
        });
    }

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