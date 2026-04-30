package mvc.map.model;

import common.entity.Hero;
import common.map.Exit;
import common.map.LockedExit;
import common.map.Room;
import mvc.mvc.Model;

public class ExitModel implements Model {

    private final Exit exit;

    public ExitModel(Exit exit) {
        this.exit = exit;
    }

    @Override
    public void run() {
    }

    public boolean canCross(Hero h) {
        return exit.canCross(h);
    }

    public Room getTarget() {
        return exit.getTarget();
    }

    public boolean isLocked() {
        if (exit instanceof LockedExit) {
            return ((LockedExit) exit).isLocked();
        }
        return false;
    }

    public void unlock(Hero h) {
        if (exit instanceof LockedExit) {
            ((LockedExit) exit).unlock(h);
        }
    }

    
    public Exit getExit() {
        return exit;
    }
}
