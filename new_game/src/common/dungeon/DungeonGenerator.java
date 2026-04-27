package common.dungeon;

import common.item.Key;
import common.map.Room;

import java.util.*;

public class DungeonGenerator {

    private static final String[] ROOM_NAMES = {
            "Entrance", "Corridor", "Vault", "Chapel", "Armory",
            "Library", "Storage", "Barracks", "Hall", "Cellar",
            "Kitchen", "Workshop", "Gallery", "Watchtower", "Crypt"
    };

    private static final String[] ROOM_DESCRIPTIONS = {
            "A cold and silent room.",
            "Dust covers the floor.",
            "You hear water dripping somewhere.",
            "The walls are cracked and old.",
            "An uneasy feeling fills the air.",
            "There is almost nothing here.",
            "A forgotten place of the dungeon.",
            "The atmosphere is strangely calm."
    };

    private static final String[] DIRECTIONS = {"north", "south", "east", "west"};

    private final Random random;

    public DungeonGenerator(long seed) {
        this.random = new Random(seed);
    }

    public DungeonData generate(int roomCount) {
        if (roomCount < 2) {
            roomCount = 2;
        }

        Map<Pos, Room> grid = new HashMap<>();
        List<Room> rooms = new ArrayList<>();

        Room start = new Room("Entrance", "The beginning of the dungeon.");
        Pos startPos = new Pos(0, 0);

        grid.put(startPos, start);
        rooms.add(start);

        while (rooms.size() < roomCount) {
            List<Map.Entry<Pos, Room>> placedRooms = new ArrayList<>(grid.entrySet());
            Map.Entry<Pos, Room> baseEntry = placedRooms.get(random.nextInt(placedRooms.size()));

            Pos basePos = baseEntry.getKey();
            Room baseRoom = baseEntry.getValue();

            String dir = DIRECTIONS[random.nextInt(DIRECTIONS.length)];
            Pos newPos = move(basePos, dir);

            if (grid.containsKey(newPos)) {
                continue;
            }

            Room newRoom = new Room(randomName(rooms.size()), randomDescription());
            grid.put(newPos, newRoom);
            rooms.add(newRoom);

            connectBothWays(baseRoom, newRoom, dir);
        }

        addExtraConnections(grid, roomCount / 3);

        Key key = new Key("Golden Key", "A key to a special door");
        lockRandomConnection(grid, start, key);

        return new DungeonData(start, rooms, key);
    }

    private void addExtraConnections(Map<Pos, Room> grid, int attempts) {
        List<Pos> positions = new ArrayList<>(grid.keySet());

        for (int i = 0; i < attempts; i++) {
            Pos a = positions.get(random.nextInt(positions.size()));
            String dir = DIRECTIONS[random.nextInt(DIRECTIONS.length)];
            Pos b = move(a, dir);

            if (!grid.containsKey(b)) {
                continue;
            }

            Room roomA = grid.get(a);
            Room roomB = grid.get(b);

            if (roomA.getExit(dir) != null) {
                continue;
            }

            connectBothWays(roomA, roomB, dir);
        }
    }

    private void lockRandomConnection(Map<Pos, Room> grid, Room start, Key key) {
        List<LockedCandidate> candidates = new ArrayList<>();

        for (Map.Entry<Pos, Room> entry : grid.entrySet()) {
            Pos pos = entry.getKey();
            Room room = entry.getValue();

            for (String dir : DIRECTIONS) {
                Pos next = move(pos, dir);
                Room target = grid.get(next);

                if (target == null) {
                    continue;
                }

                if (room.getExit(dir) == null) {
                    continue;
                }

                // éviter de verrouiller la room de départ directement
                if (room == start) {
                    continue;
                }

                candidates.add(new LockedCandidate(room, target, dir));
            }
        }

        if (candidates.isEmpty()) {
            return;
        }

        LockedCandidate chosen = candidates.get(random.nextInt(candidates.size()));

        // remplace juste un côté par une locked exit
        chosen.from.getExits().put(chosen.direction, new common.map.LockedExit(chosen.to, key));

        // on met la clé dans la start room pour faire simple
        start.addItem(key);
    }

    private void connectBothWays(Room a, Room b, String dirFromAToB) {
        String reverse = reverse(dirFromAToB);
        a.addExit(dirFromAToB, b);
        b.addExit(reverse, a);
    }

    private String reverse(String dir) {
        return switch (dir) {
            case "north" -> "south";
            case "south" -> "north";
            case "east" -> "west";
            case "west" -> "east";
            default -> throw new IllegalArgumentException("Unknown direction: " + dir);
        };
    }

    private Pos move(Pos pos, String dir) {
        return switch (dir) {
            case "north" -> new Pos(pos.x, pos.y - 1);
            case "south" -> new Pos(pos.x, pos.y + 1);
            case "east" -> new Pos(pos.x + 1, pos.y);
            case "west" -> new Pos(pos.x - 1, pos.y);
            default -> throw new IllegalArgumentException("Unknown direction: " + dir);
        };
    }

    private String randomName(int index) {
        String base = ROOM_NAMES[random.nextInt(ROOM_NAMES.length)];
        return base + " " + index;
    }

    private String randomDescription() {
        return ROOM_DESCRIPTIONS[random.nextInt(ROOM_DESCRIPTIONS.length)];
    }

    private static final class Pos {
        private final int x;
        private final int y;

        private Pos(int x, int y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Pos pos)) return false;
            return x == pos.x && y == pos.y;
        }

        @Override
        public int hashCode() {
            return Objects.hash(x, y);
        }
    }

    private static final class LockedCandidate {
        private final Room from;
        private final Room to;
        private final String direction;

        private LockedCandidate(Room from, Room to, String direction) {
            this.from = from;
            this.to = to;
            this.direction = direction;
        }
    }
}