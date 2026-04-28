package game;

import java.util.ArrayList;
import java.util.List;

import entity.Character;
import entity.Hero;
import entity.Item;
import entity.Boss;
import location.Room;

public class AttackCommand extends Command {

    public AttackCommand() {
        super("ATTACK");
    }

    @Override
    public void execute(Game g, List<String> args) {
        System.out.println("-----------");
        Room current = g.getCurrentRoom();
        List<Character> enemies = current.getCharacters();

        if (enemies.isEmpty()) {
            System.out.println("There is nothing to attack here.");
            System.out.println("-----------");
            return;
        }

        Character target;

        if (args.isEmpty()) {
            target = enemies.get(0); // pas de nom → premier ennemi
        } else {
            String name = String.join(" ", args);
            target = null;
            for (Character c : enemies) {
                if (c.getName().equalsIgnoreCase(name)) {
                    target = c;
                    break;
                }
            }
            if (target == null) {
                System.out.println("No such enemy here.");
                System.out.println("-----------");
                return;
            }
        }

        Hero hero = g.getHero();

        // Héros attaque d'abord
        hero.attack(target);

        if (target.getHp() <= 0) {

            System.out.println(target.getName() + " is dead!");
            // DROP : tous les items de l'ennemi vont dans la room
            List<Item> loot = new ArrayList<>(target.getInventory());
            target.getInventory().clear();
            if (!loot.isEmpty()) {
                System.out.println(target.getName() + " dropped:");
                for (Item it : loot) {
                    System.out.println(" - " + it.getName());
                    current.addItem(it);
                }
            }

            current.removeCharacter(target);

            System.out.println("-----------");
            if (target instanceof Boss) {
                System.out.println("You defeated the Boss! You win!");
                g.stop(); // arrête la boucle dans game.play() si le boss meurt
            }

            return;
        }

        // Monstre contre-attaque
        target.attack(hero);
        if (hero.getHp() <= 0) {
            System.out.println("You are dead!");
            g.stop();
            System.out.println("-----------");
            return;
        }

        System.out.println("Your remaining HP: " + hero.getHp());
        System.out.println("-----------");
    }
}
