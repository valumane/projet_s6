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
    private final ExitView viewGUI;

    public ExitController(ExitModel exitModel, ExitView viewCLI, ExitView viewGUI) {
        super(exitModel, viewCLI, viewGUI);
        this.exitModel = exitModel;
        this.viewCLI = viewCLI;
        this.viewGUI = viewGUI;
    }

    public Room onCross(Hero h) {
        if (!exitModel.canCross(h)) {
            if (exitModel.isLocked()) {
                viewCLI.displayLockedDoor();
                viewGUI.displayLockedDoor();
            }
            return null;
        }
        return exitModel.getTarget();
    }

    public void onUnlock(Hero h) {
        if (!exitModel.isLocked()) {
            return;
        }

        if (exitModel.getExit() instanceof LockedExit lockedExit) {
            String keyName = lockedExit.getKey().getName();

            boolean hasKey = h.getInventory().stream()
                    .anyMatch(item -> item == lockedExit.getKey());

            if (hasKey) {
                exitModel.unlock(h);
                viewCLI.displayUnlockedDoor();
                viewGUI.displayUnlockedDoor();
            } else {
                viewCLI.displayNeedKey(keyName);
                viewGUI.displayNeedKey(keyName);
            }
        }
    }
}