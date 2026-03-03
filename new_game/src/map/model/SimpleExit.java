package map.model;

public class SimpleExit extends Exit {

    public SimpleExit(RoomModel p_target) {
        super(p_target);
    }

    @Override
    public boolean canCross() {
        return true;
    }
}