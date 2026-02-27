package game;

import java.util.List;

import entity.Hero;

public class HelpCommand extends Command {

    public HelpCommand() {
        super("HELP");
    }

    @Override
    public void execute(Game g, List<String> args) {
        Hero h = g.getHero();

        System.out.println("Available commands:");
        System.out.println(" GO <direction>      Move to another room");
        System.out.println("      LOOK           Look nearby");
        System.out.println("  ATTACK [enemy]     Attack an enemy");
        System.out.println("   TAKE <item>       Take an item");
        System.out.println("   TAKE all          Take every item on a room");
        System.out.println("   DROP <item>       Drop an item");
        System.out.println("    USE <item>       Use an item");
        System.out.println("    INVENTORY        Open inventory");
        System.out.println("      STATS          Show stats");
        System.out.println("      HELP           Show this help");
        System.out.println("      QUIT           Leave the game");
        System.out.println(" SAVE <filename>     Save game state");
        System.out.println(" LOAD <filename>     Load game state from file");

        if (h.hasHealSpell()) {
            System.out.println("      HEAL           Heal yourself using magic");
        }
    }
}
