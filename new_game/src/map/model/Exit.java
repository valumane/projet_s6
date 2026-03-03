package map.model;

import mvc.Model;

public abstract class Exit implements Model {

    private final RoomModel target;

    protected Exit(RoomModel p_target) {
        this.target = p_target;
    }

    public RoomModel getTarget() {
        return this.target;
    }

    public abstract boolean canCross();

    public RoomModel cross() {
        if (canCross()) {
            return this.target;
        }
        return null;
    }

    @Override
    public void run() {
        // No specific behaviour at model level
    }
}