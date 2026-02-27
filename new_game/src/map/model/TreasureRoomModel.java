package map.model;

import java.util.ArrayList;
import java.util.List;

public class TreasureRoomModel extends RoomModel {

    private final List<String> treasures;

    public TreasureRoomModel(String p_name, String p_description) {
        super(p_name, p_description);
        this.treasures = new ArrayList<>();
    }

    public List<String> getTreasures() {
        return this.treasures;
    }

    public void addTreasure(String p_treasure) {
        this.treasures.add(p_treasure);
    }

    public void removeTreasure(String p_treasure) {
        this.treasures.remove(p_treasure);
    }
}