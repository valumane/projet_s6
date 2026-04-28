package location;

import java.io.Serializable;

import entity.Hero;

public abstract class Exit implements Serializable {
    private static final long serialVersionUID = 1L;
    private final Room target;

    protected Exit(Room target) {
        this.target = target;
    }

    public Room getTarget() {
        return target;
    }

    public abstract boolean canCross(Hero h);

    public Room cross(Hero h) {
        if (canCross(h)) {
            return target;
        }
        return null;
    }
}
