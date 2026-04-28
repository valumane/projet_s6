package location;

import entity.Hero;

public class SimpleExit extends Exit {

    public SimpleExit(Room target) {
        super(target);
    }

    @Override
    public boolean canCross(Hero h) {
        return true;
    }
}
