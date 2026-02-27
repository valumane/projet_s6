package game;

import java.util.List;

public class SaveCommand extends Command {

    public SaveCommand() {
        super("SAVE"); // nom de la commande en MAJUSCULE
    }

    @Override
    public void execute(Game game, List<String> args) {
        if (args.isEmpty()) {
            System.out.println("Usage: SAVE filename");
            return;
        }

        String filename = args.get(0);
        game.save(filename);
    }
}
