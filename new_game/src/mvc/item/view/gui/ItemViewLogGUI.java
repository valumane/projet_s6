package mvc.item.view.gui;

import java.util.function.Consumer;
import mvc.item.view.base.ItemView;

public class ItemViewLogGUI extends ItemView {

    private final Consumer<String> logger;

    public ItemViewLogGUI(Consumer<String> logger) {
        this.logger = logger;
    }

    private void log(String message) {
        if (logger != null) {
            logger.accept(message);
        }
    }

    @Override
    public void showItemName(String name) {
    }

    @Override
    public void showItemDescription(String description) {
    }

    @Override
    public void showOwnership(boolean inInventory) {
    }

    @Override
    public void showMessage(String message) {
        log(message);
    }
}