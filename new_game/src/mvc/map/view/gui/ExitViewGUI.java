package mvc.map.view.gui;

import java.util.function.Consumer;
import mvc.map.view.base.ExitView;

public class ExitViewGUI extends ExitView {

    private final Consumer<String> logger;

    public ExitViewGUI(Consumer<String> logger) {
        this.logger = logger;
    }

    private void log(String message) {
        if (logger != null) {
            logger.accept(message);
        }
    }

    @Override
    public void displayLockedDoor() {
        log("Door is locked.");
    }

    @Override
    public void displayUnlockedDoor() {
        log("The door is now unlocked.");
    }

    @Override
    public void displayNeedKey(String keyName) {
        log("You need the " + keyName + " to unlock this door.");
    }
}