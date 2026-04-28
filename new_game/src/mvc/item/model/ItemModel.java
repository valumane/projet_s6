package mvc.item.model;

import common.entity.Hero;
import common.language.Language;
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
            notifyMessage(Language.tf("item.cannotTake", item.getName()));
            notifyState();
            return;
        }

        if (isInInventory()) {
            notifyMessage(Language.tf("item.alreadyInInventory", item.getName()));
            notifyState();
            return;
        }

        if (!isInCurrentRoom()) {
            notifyMessage(Language.tf("item.notInRoom", item.getName()));
            notifyState();
            return;
        }

        if (!heroModel.getHero().addItem(item)) {
            notifyMessage(Language.t("item.inventoryFull"));
            notifyState();
            return;
        }

        heroModel.getRoom().removeItem(item);

        notifyMessage(Language.tf("item.heroTakes", heroModel.getName(), item.getName()));
        heroModel.syncState();
        notifyState();
    }

    public void interact() {
        if (item instanceof Chest) {
            use();
            return;
        }

        if (isInCurrentRoom() && item.canBeTaken()) {
            take();
            return;
        }

        if (isInInventory() && item.canBeUsed()) {
            use();
            return;
        }

        if (item.canBeUsed()) {
            use();
            return;
        }

        notifyMessage(Language.t("item.nothingHappens"));
        notifyState();
    }

    public void drop() {
        if (!item.canBeDropped()) {
            notifyMessage(Language.tf("item.cannotDrop", item.getName()));
            notifyState();
            return;
        }

        if (!isInInventory()) {
            notifyMessage(Language.tf("item.notInInventory", item.getName()));
            notifyState();
            return;
        }

        Item dropped = heroModel.drop(item.getName());
        if (dropped == null) {
            notifyMessage(Language.tf("item.notInInventory", item.getName()));
        } else {
            notifyMessage(Language.tf("item.heroDrops", heroModel.getName(), dropped.getName()));
        }

        heroModel.syncState();
        notifyState();
    }

    public void use() {
        Hero hero = heroModel.getHero();

        if (!item.canBeUsed()) {
            notifyMessage(Language.tf("item.cannotUse", item.getName()));
            notifyState();
            return;
        }

        if (item instanceof Chest chest) {
            if (!isInCurrentRoom()) {
                notifyMessage(Language.tf("item.notInRoom", item.getName()));
                notifyState();
                return;
            }

            chest.use(hero);
            notifyMessage(Language.tf("item.youOpen", item.getName()));
            heroModel.syncState();
            notifyState();
            return;
        }

        if (!isInInventory()) {
            notifyMessage(Language.tf("item.takeFirst", item.getName()));
            notifyState();
            return;
        }

        if (item instanceof Weapon) {
            notifyMessage(Language.tf("item.alreadyApplied", item.getName()));
            heroModel.syncState();
            notifyState();
            return;
        }

        UsableItem usableItem = (UsableItem) item;
        usableItem.use(hero);

        notifyMessage(Language.tf("item.youUse", item.getName()));
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
