package common.dungeon;

import common.languages.Languages;
import common.item.Key;
import common.map.Room;

import java.util.*;

public class DungeonGenerator {

    private static final String[] ROOM_NAME_KEYS = {
            "Entrance", "Corridor", "Vault", "Chapel", "Armory",
            "Library", "Storage", "Barracks", "Hall", "Cellar",
            "Kitchen", "Workshop", "Gallery", "Watchtower", "Crypt"
    };

    private static final String[] ROOM_DESC_KEYS = {
            "room.desc.0", "room.desc.1", "room.desc.2", "room.desc.3",
            "room.desc.4", "room.desc.5", "room.desc.6", "room.desc.7"
    };

    private static final String[] DIRECTIONS = { "north", "south", "east", "west" };

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

        Room start = new Room(Languages.t("room.Entrance"), Languages.t("room.entranceDesc"));
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

        Room bossRoom = chooseBossRoom(grid, start);
        bossRoom.setBossRoom(true);

        Key key = new Key(Languages.t("item.bossKey"), Languages.t("item.bossKeyDesc"));
        lockAllEntrancesToBossRoom(grid, bossRoom, key);

        return new DungeonData(start, rooms, key, bossRoom);
    }

    private Room chooseBossRoom(Map<Pos, Room> grid, Room start) {
        Room bestRoom = null;
        int bestDistance = -1;

        for (Map.Entry<Pos, Room> entry : grid.entrySet()) {
            Room room = entry.getValue();

            if (room == start) {
                continue;
            }

            Pos pos = entry.getKey();
            int distance = Math.abs(pos.x) + Math.abs(pos.y);

            if (distance > bestDistance) {
                bestDistance = distance;
                bestRoom = room;
            }
        }

        if (bestRoom == null) {
            throw new IllegalStateException("No boss room candidate found.");
        }

        return bestRoom;
    }

    private void lockAllEntrancesToBossRoom(Map<Pos, Room> grid, Room bossRoom, Key key) {
        Pos bossPos = null;

        for (Map.Entry<Pos, Room> entry : grid.entrySet()) {
            if (entry.getValue() == bossRoom) {
                bossPos = entry.getKey();
                break;
            }
        }

        if (bossPos == null) {
            throw new IllegalStateException("Boss room position not found.");
        }

        for (String dirFromBossToNeighbor : DIRECTIONS) {
            Pos neighborPos = move(bossPos, dirFromBossToNeighbor);
            Room neighbor = grid.get(neighborPos);

            if (neighbor == null) {
                continue;
            }

            String dirFromNeighborToBoss = reverse(dirFromBossToNeighbor);

            if (neighbor.getExit(dirFromNeighborToBoss) != null
                    && neighbor.getExit(dirFromNeighborToBoss).getTarget() == bossRoom) {
                neighbor.getExits().put(
                        dirFromNeighborToBoss,
                        new common.map.LockedExit(bossRoom, key));
            }
        }
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
        String baseKey = ROOM_NAME_KEYS[random.nextInt(ROOM_NAME_KEYS.length)];
        return Languages.t("room." + baseKey) + " " + index;
    }

    private String randomDescription() {
        String key = ROOM_DESC_KEYS[random.nextInt(ROOM_DESC_KEYS.length)];
        return Languages.t(key);
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
            if (this == o)
                return true;
            if (!(o instanceof Pos pos))
                return false;
            return x == pos.x && y == pos.y;
        }

        @Override
        public int hashCode() {
            return Objects.hash(x, y);
        }
    }

}
