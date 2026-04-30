package mvc.item.view.base;

import mvc.mvc.View;

public abstract class ItemView implements View {

    @Override
    public void hide() {
    }

    @Override
    public void show() {
    }

    public abstract void showItemName(String name);

    public abstract void showItemDescription(String description);

    public abstract void showOwnership(boolean inInventory);

    public abstract void showMessage(String message);

    public void setOnTake(Runnable action) {
    }

    public void setOnDrop(Runnable action) {
    }

    public void setOnUse(Runnable action) {
    }

    public void setOnRefresh(Runnable action) {
    }

    public void setActionsVisible(boolean takeVisible, boolean dropVisible, boolean useVisible) {
    }
}