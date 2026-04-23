package mvc.map.controller;

import common.entity.Hero;
import common.map.LockedExit;
import common.map.Room;
import mvc.map.model.ExitModel;
import mvc.map.view.base.ExitView;
import mvc.mvc.Controller;

public class ExitController extends Controller {

    private final ExitModel exitModel;
    private final ExitView viewCLI;

    public ExitController(ExitModel exitModel, ExitView viewGUI, ExitView viewCLI) {
        super(exitModel, viewGUI, viewCLI);
        this.exitModel = exitModel;
        this.viewCLI = viewCLI;
    }

    // Regarde si le Hero peut prendr la sortie ou non
    public Room onCross(Hero h) {
        if (exitModel.isLocked()) {
            viewCLI.displayLockedDoor();
            return null;
        }
        return exitModel.getTarget();
    }

    // Regarde si le Hero peut dévérouiller la sortie ou non
    public void onUnlock(Hero h) {
        if (!exitModel.isLocked()) {
            return;
        }
        if (exitModel.getExit() instanceof LockedExit) {
            LockedExit lockedExit = (LockedExit) exitModel.getExit();
            String keyName = lockedExit.getKey().getName();
            
            // Vérifie si il a la clé
            boolean hasKey = h.getInventory().stream()
                    .anyMatch(item -> item.getName().equalsIgnoreCase(keyName));
            if (hasKey) {
                exitModel.unlock(h);
                viewCLI.displayUnlockedDoor();
            } else {
                viewCLI.displayNeedKey(keyName);
            }
        }
    }
}