package game;

import java.util.List;

import entity.Hero;
import entity.Item;
import location.Room;

public class LookCommand extends Command {

    public LookCommand() {
        super("LOOK");
    }

    @Override
    public void execute(Game g, List<String> args) {
        Room current = g.getCurrentRoom();

        // Aucun argument -> comportement normal : description de la salle
        if (args.isEmpty()) {
            System.out.println(current.describe());
            return;
        }

        // Avec arguments -> on cherche un item par son nom
        String targetName = String.join(" ", args);

        Hero h = g.getHero();

        // 1) Chercher dans l'inventaire du héros
        for (Item it : h.getInventory()) {
            if (it.getName().equalsIgnoreCase(targetName)) {
                System.out.println(it.getDescription());
                return;
            }
        }

        // 2) Chercher parmi les objets de la salle
        for (Item it : current.getItems()) {
            if (it.getName().equalsIgnoreCase(targetName)) {
                System.out.println(it.getDescription());
                return;
            }
        }

        // Rien trouvé
        System.out.println("You don't see any \"" + targetName + "\" here.");
    }
}
