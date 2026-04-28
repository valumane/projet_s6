package game;

import java.util.List;

public class QuitCommand extends Command {

    public QuitCommand() {
        super("QUIT");
    }

    @Override
    public void execute(Game g, List<String> args) {
        System.out.println("Bye!");
        g.stop();
    }
}
