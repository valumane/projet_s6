package map.model;

import mvc.Model;

import java.util.HashMap;
import java.util.Map;

public class GameMapModel implements Model {

    private final Map<String, RoomModel> rooms;

    private final RoomModel startingRoomModel;

    private RoomModel currentRoomModel;

    public GameMapModel() {
        this.rooms = new HashMap<>();
        this.startingRoomModel = buildMap();
        this.currentRoomModel = this.startingRoomModel;
    }

    private RoomModel buildMap() {
        // --- Rooms ---
        SimpleRoom entrance    = new SimpleRoom("entrance",
                "You are at the entrance of a dark dungeon. You can go west and east.");
        SimpleRoom corridor1   = new SimpleRoom("dark and silent corridor",
                "You are in a dark and silent corridor. You can go west and east.");
        SimpleRoom corridor2   = new SimpleRoom("narrow corridor",
                "You are in a narrow corridor leading deeper into the dungeon. You can go north and east. There is also a locked door to the south.");
        SimpleRoom corridor3   = new SimpleRoom("damp corridor",
                "You are in a damp corridor with cobwebs on the walls. You can go south and to the north.");
        SimpleRoom corridor4   = new SimpleRoom("swampy corridor",
                "You are in a swampy corridor with a musty smell. You can go north, east and west.");
        SimpleRoom corridor5   = new SimpleRoom("cul-de-sac",
                "That's a cul-de-sac, you need to go back. You can go south.");
        SimpleRoom corridor6   = new SimpleRoom("goblin corridor",
                "You are in a corridor where goblin noises can be heard. You can go west and there is a huge locked door to the south.");
        SimpleRoom runicRoom   = new SimpleRoom("runic room",
                "You are in a room filled with ancient runes and an altar at its centre.");
        TreasureRoom treasureRoom = new TreasureRoom("treasure room",
                "A room filled with sparkling treasures.");
        BossRoom bossRoom      = new BossRoom("boss room",
                "The final boss room.", "Basilisk");

        // --- Exits ---
        entrance.addExit("west",  new SimpleExit(corridor1));
        entrance.addExit("east",  new SimpleExit(corridor4));

        corridor1.addExit("east", new SimpleExit(entrance));
        corridor1.addExit("west", new SimpleExit(corridor2));

        corridor2.addExit("east",  new SimpleExit(corridor1));
        corridor2.addExit("north", new SimpleExit(corridor3));
        corridor2.addExit("south", new LockExit(treasureRoom, true));

        corridor3.addExit("south", new SimpleExit(corridor2));
        corridor3.addExit("north", new SimpleExit(runicRoom));

        runicRoom.addExit("south", new SimpleExit(corridor3));

        treasureRoom.addExit("north", new SimpleExit(corridor2));
        treasureRoom.addTreasure("Medium Potion");
        treasureRoom.addTreasure("Boss Key");
        treasureRoom.addTreasure("Katana");

        corridor4.addExit("west",  new SimpleExit(entrance));
        corridor4.addExit("north", new SimpleExit(corridor5));
        corridor4.addExit("east",  new SimpleExit(corridor6));

        corridor5.addExit("south", new SimpleExit(corridor4));

        corridor6.addExit("west",  new SimpleExit(corridor4));
        corridor6.addExit("south", new LockExit(bossRoom, true));

        bossRoom.addExit("north", new SimpleExit(corridor6));

        // --- Register rooms ---
        addRoom(entrance);
        addRoom(corridor1);
        addRoom(corridor2);
        addRoom(corridor3);
        addRoom(corridor4);
        addRoom(corridor5);
        addRoom(corridor6);
        addRoom(runicRoom);
        addRoom(treasureRoom);
        addRoom(bossRoom);

        return entrance;
    }

    public void addRoom(RoomModel p_roomModel) {
        this.rooms.put(p_roomModel.getName(), p_roomModel);
    }

    public RoomModel getRoom(String p_name) {
        return this.rooms.get(p_name);
    }

    public Map<String, RoomModel> getRooms() {
        return this.rooms;
    }

    public RoomModel getStartingRoom() {
        return this.startingRoomModel;
    }

    public RoomModel getCurrentRoom() {
        return this.currentRoomModel;
    }

    public boolean move(String p_direction) {
        Exit exit = this.currentRoomModel.getExit(p_direction);
        if (exit == null) {
            return false;
        }
        RoomModel next = exit.cross();
        if (next == null) {
            return false;
        }
        this.currentRoomModel = next;
        return true;
    }

    @Override
    public void run() {
        // No specific behaviour at model level
    }
}