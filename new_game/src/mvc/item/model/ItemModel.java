package mvc.item.model;

import common.entity.Hero;
import common.item.Chest;
import common.item.Item;
import common.item.Weapon;
import mvc.entity.model.HeroModel;
import mvc.mvc.Model;
import common.item.UsableItem;

public class ItemModel implements Model {

    public interface Listener {
        void onStateChanged(String name, String description, boolean inInventory);

        void onMessage(String message);
    }

    private final Item item;
    private final HeroModel heroModel;
    private Listener listener;

    public ItemModel(Item item, HeroModel heroModel) {
        this.item = item;
        this.heroModel = heroModel;
    }

    @Override
    public void run() {
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    public Item getItem() {
        return item;
    }

    public String getName() {
        return item.getName();
    }

    public String getDescription() {
        String desc = item.getDescription();
        return (desc == null || desc.isEmpty()) ? "(no description)" : desc;
    }

    public boolean isInInventory() {
        return heroModel.getHero().getInventory().contains(item);
    }

    public boolean isInCurrentRoom() {
        return heroModel.getRoom() != null && heroModel.getRoom().getItems().contains(item);
    }

    public void syncState() {
        notifyState();
    }

    public void take() {
        if (!item.canBeTaken()) {
            notifyMessage(item.getName() + " cannot be taken.");
            notifyState();
            return;
        }

        if (isInInventory()) {
            notifyMessage(item.getName() + " is already in inventory.");
            notifyState();
            return;
        }

        if (!isInCurrentRoom()) {
            notifyMessage(item.getName() + " is not in the current room.");
            notifyState();
            return;
        }

        heroModel.getRoom().removeItem(item);
        heroModel.getHero().addItem(item);

        notifyMessage(heroModel.getName() + " takes " + item.getName() + ".");
        heroModel.syncState();
        notifyState();
    }

    public void drop() {
        if (!item.canBeDropped()) {
            notifyMessage(item.getName() + " cannot be dropped.");
            notifyState();
            return;
        }

        if (!isInInventory()) {
            notifyMessage(item.getName() + " not in the inventory.");
            notifyState();
            return;
        }

        Item dropped = heroModel.drop(item.getName());
        if (dropped == null) {
            notifyMessage(item.getName() + " not in the inventory.");
        } else {
            notifyMessage(heroModel.getName() + " drops " + dropped.getName() + ".");
        }

        heroModel.syncState();
        notifyState();
    }

    public void use() {
        Hero hero = heroModel.getHero();

        if (!item.canBeUsed()) {
            notifyMessage(item.getName() + " cannot be used.");
            notifyState();
            return;
        }

        if (item instanceof Chest chest) {
            if (!isInCurrentRoom()) {
                notifyMessage(item.getName() + " is not in the current room.");
                notifyState();
                return;
            }

            chest.use(hero);
            notifyMessage("You open " + item.getName() + ".");
            heroModel.syncState();
            notifyState();
            return;
        }

        if (!isInInventory()) {
            notifyMessage("Take " + item.getName() + " first.");
            notifyState();
            return;
        }

        if (item instanceof Weapon) {
            notifyMessage(item.getName() + " is already applied while carried by the hero.");
            heroModel.syncState();
            notifyState();
            return;
        }

        UsableItem usableItem = (UsableItem) item;
        usableItem.use(hero);

        notifyMessage("You use " + item.getName() + ".");
        heroModel.syncState();
        notifyState();
    }

    private void notifyState() {
        if (listener != null) {
            listener.onStateChanged(getName(), getDescription(), isInInventory());
        }
    }

    private void notifyMessage(String message) {
        if (listener != null) {
            listener.onMessage(message);
        }
    }
}