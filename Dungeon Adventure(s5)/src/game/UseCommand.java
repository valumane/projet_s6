package game;

import java.util.List;

import entity.Hero;
import entity.Item;
import entity.Key;
import location.Room;
import location.Exit;
import location.LockedExit;

public class UseCommand extends Command {

    public UseCommand() {
        super("USE");
    }

    @Override
    public void execute(Game g, List<String> args) {
        if (args.isEmpty()) {
            System.out.println("Use what?");
            return;
        }

        Hero h = g.getHero();
        List<Item> inv = h.getInventory();

        // 1) Essayer d'interpréter TOUT comme un nom d'item (ex: "Small potion")
        String fullName = String.join(" ", args);
        Item item = findItem(inv, fullName);

        if (item != null) {
            // cas normal : USE <item>
            item.use(h);
            return;
        }

        // 2) Si l'item n'existe pas sous ce nom et qu'on a au moins 2 args,
        // on essaye la forme: USE <item...> <exit>
        if (args.size() < 2) {
            System.out.println("You don't have that item.");
            return;
        }

        String exitName = args.get(args.size() - 1);
        String itemName = String.join(" ", args.subList(0, args.size() - 1));
        item = findItem(inv, itemName);

        if (item == null) {
            System.out.println("You don't have that item.");
            return;
        }

        // Pour l'instant, on ne sait utiliser qu'une Key sur une sortie
        if (!(item instanceof Key)) {
            System.out.println("You can't use that on an exit.");
            return;
        }

        Room current = g.getCurrentRoom();
        Exit exit = current.getExit(exitName);
        if (exit == null) {
            System.out.println("There is no such exit here.");
            return;
        }

        if (!(exit instanceof LockedExit)) {
            System.out.println("This exit is not locked.");
            return;
        }

        LockedExit lockedExit = (LockedExit) exit;
        if (!lockedExit.isLocked()) {
            System.out.println("This exit is already unlocked.");
            return;
        }

        lockedExit.unlock();
        System.out.println("You unlock the " + exitName + " with the " + item.getName() + ".");
        inv.remove(item);
    }

    private Item findItem(List<Item> inv, String name) {
        for (Item i : inv) {
            if (i.getName().equalsIgnoreCase(name)) {
                return i;
            }
        }
        return null;
    }
}
