package game;

import java.util.List;

import entity.Hero;
import entity.Item;

public class InventoryCommand extends Command {

    public InventoryCommand() {
        super("INVENTORY");
    }

    @Override
    public void execute(Game g, List<String> args) {
        System.out.println("-----------");
        Hero h = g.getHero();
        List<Item> inv = h.getInventory();
        if (inv.isEmpty()) {
            System.out.println("Your inventory is empty.");
        } else {
            System.out.println("You are carrying:");
            for (Item i : inv) {
                System.out.println(" - " + i.getName());
            }
        }
        System.out.println("-----------");

    }
}
