package game;

import java.util.List;

public class LoadCommand extends Command {

    public LoadCommand() {
        super("LOAD");
    }

    @Override
    public void execute(Game game, List<String> args) {
        if (args.isEmpty()) {
            System.out.println("Usage: LOAD filename");
            return;
        }

        String filename = args.get(0);
        Game loaded = Game.load(filename);

        if (loaded != null) {
            game.copyStateFrom(loaded);
            System.out.println("Game loaded.");
        } else {
            System.out.println("Failed to load game.");
        }
    }
}
