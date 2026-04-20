package mvc.item.view.cli;

import mvc.item.view.base.ItemView;

public class ItemViewCLI extends ItemView {

    private final String prefix;

    public ItemViewCLI(String prefix) {
        this.prefix = prefix;
    }

    @Override
    public void showItemName(String name) {
        System.out.println("[" + prefix + "] Item: " + name);
    }

    @Override
    public void showItemDescription(String description) {
        System.out.println("[" + prefix + "] Description: " + description);
    }

    @Override
    public void showOwnership(boolean inInventory) {
        System.out.println("[" + prefix + "] Location: " + (inInventory ? "inventory" : "room"));
    }

    @Override
    public void showMessage(String message) {
        System.out.println("[" + prefix + "] " + message);
    }
}