package game;

import java.util.ArrayList;
import java.util.List;

import entity.Hero;
import entity.Item;
import location.Room;

public class TakeCommand extends Command {

    public TakeCommand() {
        super("TAKE");
    }

    @Override
    public void execute(Game g, List<String> args) {
        Room current = g.getCurrentRoom();
        Hero h = g.getHero();

        if (args.isEmpty()) {
            System.out.println("Take what?");
            return;
        }

        // Cas spécial : TAKE ALL
        if (args.size() == 1 && args.get(0).equalsIgnoreCase("ALL")) {
            List<Item> itemsInRoom = new ArrayList<>(current.getItems());

            if (itemsInRoom.isEmpty()) {
                System.out.println("There is nothing to take here.");
                return;
            }

            System.out.println("You take everything:");
            for (Item it : itemsInRoom) {
                current.removeItem(it);
                h.take(it);
                System.out.println(" - " + it.getName());
            }
            return;
        }

        // Cas normal : TAKE <item>
        String itemName = String.join(" ", args);

        List<Item> items = current.getItems();
        Item target = null;
        for (Item i : items) {
            if (i.getName().equalsIgnoreCase(itemName)) {
                target = i;
                break;
            }
        }

        if (target == null) {
            System.out.println("There is no such item here.");
            return;
        }

        current.removeItem(target);
        h.take(target);
        System.out.println("You take the " + target.getName() + ".");
    }
}
