package location;

import entity.Hero;

public class LockedExit extends Exit {

    private boolean locked;

    public LockedExit(Room target, boolean locked) {
        super(target);
        this.locked = locked;
    }

    @Override
    public boolean canCross(Hero h) {
        if (locked) {
            System.out.println("The door is locked.");
            return false;
        }
        return true;
    }

    public void unlock() {
        locked = false;
    }

    public boolean isLocked() {
        return locked;
    }
}
