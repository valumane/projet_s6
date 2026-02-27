package map.model;

public class SimpleExitModel extends ExitModel {

    public SimpleExitModel(RoomModel p_target) {
        super(p_target);
    }

    @Override
    public boolean canCross() {
        return true;
    }
}