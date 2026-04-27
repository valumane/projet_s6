package mvc.item.controller;

import mvc.entity.model.HeroModel;
import mvc.item.model.ItemModel;
import mvc.item.view.base.ItemView;
import mvc.mvc.Controller;

public class ItemController extends Controller {

    private final ItemModel itemModel;
    private final ItemView viewCLI;
    private final ItemView viewGUI;

    public ItemController(ItemModel itemModel, ItemView viewCLI, ItemView viewGUI, HeroModel heroModel) {
        super(itemModel, viewCLI, viewGUI);

        this.itemModel = itemModel;
        this.viewCLI = viewCLI;
        this.viewGUI = viewGUI;

        this.itemModel.setListener(new ItemModel.Listener() {
            @Override
            public void onStateChanged(String name, String description, boolean inInventory) {
                ItemController.this.viewCLI.showItemName(name);
                ItemController.this.viewCLI.showItemDescription(description);
                ItemController.this.viewCLI.showOwnership(inInventory);

                ItemController.this.viewGUI.showItemName(name);
                ItemController.this.viewGUI.showItemDescription(description);
                ItemController.this.viewGUI.showOwnership(inInventory);

                ItemController.this.updateActions();
            }

            @Override
            public void onMessage(String message) {
                ItemController.this.viewCLI.showMessage(message);
                ItemController.this.viewGUI.showMessage(message);
            }
        });

        this.viewGUI.setOnTake(this::onTakeItem);
        this.viewGUI.setOnDrop(this::onDropItem);
        this.viewGUI.setOnUse(this::onUseItem);
        this.viewGUI.setOnRefresh(this::onRefreshItem);

        this.itemModel.syncState();
    }

    public void onTakeItem() {
        itemModel.take();
    }

    public void onDropItem() {
        itemModel.drop();
    }

    public void onUseItem() {
        itemModel.use();
    }

    public void onRefreshItem() {
        itemModel.syncState();
    }

    public void onInteractItem() {
        itemModel.interact();
    }

    private void updateActions() {
        boolean inInventory = itemModel.isInInventory();
        boolean inRoom = itemModel.isInCurrentRoom();

        boolean takeVisible = itemModel.getItem().canBeTaken() && inRoom && !inInventory;
        boolean dropVisible = itemModel.getItem().canBeDropped() && inInventory;

        boolean useVisible;
        if (itemModel.getItem() instanceof common.item.Chest) {
            useVisible = itemModel.getItem().canBeUsed() && inRoom;
        } else {
            useVisible = itemModel.getItem().canBeUsed() && inInventory;
        }

        viewGUI.setActionsVisible(takeVisible, dropVisible, useVisible);
    }
}