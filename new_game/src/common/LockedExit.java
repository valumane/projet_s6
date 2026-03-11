package common;

public class LockedExit extends Exit {
    private boolean locked;
    private Key key;

    public LockedExit(Room target, Key keyItem) {
        super(target);
        this.key = keyItem;
        this.locked = false;
    }

    @Override
    public boolean canCross(Hero h) {
        return !locked;
    }

    public void unlock(Hero h) {
        locked = false;
    }
    
    public Key getKey() {
    	return this.key;
    }

    public boolean isLocked() {
        return locked;
    }
}
