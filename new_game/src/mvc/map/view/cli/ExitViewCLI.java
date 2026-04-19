package mvc.map.view.cli;

import mvc.map.view.base.ExitView;

public class ExitViewCLI extends ExitView {
	
	@Override
    public void displayLockedDoor() {
        System.out.println("Door is locked.");
    }
	
	@Override
    public void displayUnlockedDoor() {
        System.out.println("The door is now unlocked.");
    }

    @Override
    public void displayNeedKey(String keyName) {
        System.out.println("You need the " + keyName + " to unlock this door.");
    }
}

