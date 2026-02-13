package game;

import java.util.List;

import entity.Hero;
import entity.Weapon;

public class StatusCommand extends Command {

    public StatusCommand() {
        super("STATS");
    }

    @Override
    public void execute(Game g, List<String> args) {
        Hero h = g.getHero();
        System.out.println("=== Status ===");
        System.out.println("HP: " + h.getHp());
        System.out.println("Base damage: " + h.getBaseDamage());
        Weapon w = h.getWeapon();
        if (w != null) {
            System.out.println("Equipped weapon: " + w.getName()
                    + " (+" + w.getDamage() + " dmg)");
        } else {
            System.out.println("No weapon equipped.");
        }
        System.out.println("Total attack damage: " + h.getAttackDamage());
    }
}
