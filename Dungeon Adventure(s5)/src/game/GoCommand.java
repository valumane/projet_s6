package game;

import java.util.List;
import entity.Hero;
import location.Exit;
import location.Room;

public class GoCommand extends Command {

    public GoCommand() {
        super("GO");
    }

    @Override
    public void execute(Game g, List<String> args) {
        if (args.isEmpty()) {
            System.out.println("Go where?");
            return;
        }
        String destName = args.get(0);
        Room current = g.getCurrentRoom();
        Exit exit = current.getExit(destName);
        if (exit == null) {
            System.out.println("You can't go there.");
            return;
        }
        Hero h = g.getHero();
        Room target = exit.cross(h);
        if (target == null) {
            System.out.println("You can't cross this exit.");
            return;
        }
        
        System.out.println("-----------");
        System.out.println("You leave the "+g.getCurrentRoom().getName());
        g.setCurrentRoom(target);
        g.executeCommand("LOOK");
    }
}
