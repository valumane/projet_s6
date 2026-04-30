package common.item;

import common.entity.Hero;
import common.map.Exit;
import common.map.LockedExit;

public class Key extends UsableItem {

    public Key(String name) {
        super(name);
    }

    public Key(String name, String description) {
        super(name, description);
    }

    @Override
    public void use(Hero h) {
        if (h.getRoom() == null) {
            return;
        }

        for (Exit exit : h.getRoom().getExits().values()) {
            if (exit instanceof LockedExit lockedExit) {
                if (lockedExit.isLocked() && lockedExit.getKey() == this) {
                    lockedExit.unlock(h);
                    return;
                }
            }
        }
    }
}