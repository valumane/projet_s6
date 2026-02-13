package game;

import java.util.List;

import entity.Hero;
import entity.Item;
import location.Room;

public class DropCommand extends Command {

    public DropCommand() {
        super("DROP");
    }

    @Override
    public void execute(Game g, List<String> args) {
        if (args.isEmpty()) {
            System.out.println("Drop what?");
            return;
        }
        String itemName = String.join(" ", args);
        Hero h = g.getHero();
        Item dropped = h.drop(itemName);
        if (dropped == null) {
            System.out.println("You don't have that item.");
            return;
        }
        Room current = g.getCurrentRoom();
        current.addItem(dropped);
        System.out.println("You drop the " + dropped.getName() + " on the ground.");
    }
}
