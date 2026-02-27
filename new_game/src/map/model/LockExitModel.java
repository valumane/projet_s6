package map.model;

public class LockExitModel extends ExitModel {

    private boolean locked;

    public LockExitModel(RoomModel p_target, boolean p_locked) {
        super(p_target);
        this.locked = p_locked;
    }

    public boolean isLocked() {
        return this.locked;
    }

    public void lock() {
        this.locked = true;
    }

    public void unlock() {
        this.locked = false;
    }

    @Override
    public boolean canCross() {
        return !this.locked;
    }
}