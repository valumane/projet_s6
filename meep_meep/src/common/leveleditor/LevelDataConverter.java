package common.leveleditor;

import common.dungeon.DungeonData;
import common.entity.Archer;
import common.entity.Berserker;
import common.item.Chest;
import common.item.HealSpell;
import common.item.Item;
import common.item.Key;
import common.item.Scroll;
import common.item.Weapon;
import common.map.LockedExit;
import common.map.Room;
import common.map.SimpleExit;

import java.util.ArrayList;
import java.util.List;

public final class LevelDataConverter {

    private LevelDataConverter() {}

    // convertit un LevelData en DungeonData
    public static DungeonData toDungeonData(LevelData level) {
        List<RoomEditorData> editorRooms = level.getRooms();

        if (editorRooms.isEmpty()) {
            throw new IllegalArgumentException(
                "Le niveau \"" + level.getLevelName() + "\" ne contient aucune salle.");
        }

        // creer les rooms sans les sorties
        List<Room> rooms = new ArrayList<>(editorRooms.size());
        for (RoomEditorData ed : editorRooms) {
            rooms.add(new Room(
                ed.getName().isBlank() ? "Salle" : ed.getName(),
                ed.getDescription()
            ));
        }

        // voir quelles salles sont start/boss etc...
        int startIdx = level.getStartRoomIndex();
        if (startIdx < 0 || startIdx >= rooms.size()) startIdx = 0;

        Room startRoom = rooms.get(startIdx);

        Room bossRoom = null;
        int bossIdx = -1;
        for (int i = 0; i < editorRooms.size(); i++) {
            if (editorRooms.get(i).isBossRoom()) {
                bossRoom = rooms.get(i);
                bossIdx = i;
                break;
            }
        }

        // clé boss
        Key goldenKey = null;
        if (bossRoom != null) {
            goldenKey = new Key("Clé du Boss", "Ouvre l'accès à la salle du boss");
            bossRoom.setBossRoom(true);
        }

        // connecer les sorties
        for (int i = 0; i < editorRooms.size(); i++) {
            RoomEditorData ed = editorRooms.get(i);
            Room from = rooms.get(i);

            for (int j = 0; j < ed.getExitDirections().size(); j++) {
                String dir = ed.getExitDirections().get(j);
                int targetIdx = ed.getExitTargetIndices().get(j);
                boolean locked = ed.getExitLocked().get(j);

                if (targetIdx < 0 || targetIdx >= rooms.size()) continue;

                Room target = rooms.get(targetIdx);

                if (locked && goldenKey != null && targetIdx == bossIdx) {
                    // locked exit
                    from.getExits().put(dir, new LockedExit(target, goldenKey));
                } else {
                    // exit normal
                    from.getExits().put(dir, new SimpleExit(target));
                }
            }
        }

        // remplir les rooms avec les items/ennemies
        for (int i = 0; i < editorRooms.size(); i++) {
            RoomEditorData ed = editorRooms.get(i);
            Room room = rooms.get(i);

            for (String descriptor : ed.getItemDescriptors()) {
                Item item = parseItem(descriptor);
                if (item != null) room.addItem(item);
            }

            for (String descriptor : ed.getEnemyDescriptors()) {
                parseEnemy(descriptor, room);
            }
        }

        // si pas de boss room, la plus loin
        if (bossRoom == null && rooms.size() > 1) {
            bossRoom = rooms.get(rooms.size() - 1);
            bossRoom.setBossRoom(true);
            goldenKey = new Key("Clé du Boss", "Ouvre l'accès à la salle du boss");
        }

        // goldenKey ne peut pas être null si on a au moins 2 rooms
        if (goldenKey == null) {
            goldenKey = new Key("Clé du Boss", "Ouvre l'accès à la salle du boss");
        }

        return new DungeonData(startRoom, rooms, goldenKey, bossRoom != null ? bossRoom : startRoom);
    }

    // parsers
    private static Item parseItem(String descriptor) {
        if (descriptor == null || descriptor.isBlank()) return null;
        String[] parts = descriptor.split(":", -1);

        try {
            return switch (parts[0].trim()) {
                case "HealScroll" -> {
                    int heal = parts.length > 1 ? Integer.parseInt(parts[1].trim()) : 25;
                    yield new Scroll("Parchemin de soin", new HealSpell(heal));
                }
                case "Weapon" -> {
                    // Weapon:Nom:TYPE:damage
                    String name   = parts.length > 1 ? parts[1].trim() : "Arme";
                    String type   = parts.length > 2 ? parts[2].trim() : "MELEE";
                    int damage    = parts.length > 3 ? Integer.parseInt(parts[3].trim()) : 10;
                    Weapon.WeaponType wt = "RANGED".equalsIgnoreCase(type)
                        ? Weapon.WeaponType.RANGED : Weapon.WeaponType.MELEE;
                    yield new Weapon(name, damage, wt);
                }
                case "Chest" -> {
                    // Chest:Nom:locked:description
                    String name  = parts.length > 1 ? parts[1].trim() : "Coffre";
                    boolean lock = parts.length > 2 && Boolean.parseBoolean(parts[2].trim());
                    String desc  = parts.length > 3 ? parts[3].trim() : "";
                    yield new Chest(name, lock, desc);
                }
                default -> {
                    // "Item:Nom:Description" ou juste "Item:Nom"
                    String name = parts.length > 1 ? parts[1].trim() : parts[0].trim();
                    String desc = parts.length > 2 ? parts[2].trim() : "";
                    yield new Item(name, desc);
                }
            };
        } catch (Exception e) {
            System.err.println("[LevelDataConverter] Item ignoré (format invalide) : " + descriptor);
            return null;
        }
    }

    private static void parseEnemy(String descriptor, Room room) {
        if (descriptor == null || descriptor.isBlank()) return;
        String[] parts = descriptor.split(":", -1);

        try {
            String type = parts[0].trim();
            int difficulty = parts.length > 1 ? Integer.parseInt(parts[1].trim()) : 1;
            difficulty = Math.max(1, Math.min(5, difficulty));

            switch (type) {
                case "Berserker" -> room.addCharacter(new Berserker(difficulty));
                case "Archer"    -> room.addCharacter(new Archer(difficulty));
                default -> System.err.println(
                    "[LevelDataConverter] Ennemi inconnu ignoré : " + descriptor);
            }
        } catch (Exception e) {
            System.err.println("[LevelDataConverter] Ennemi ignoré (format invalide) : " + descriptor);
        }
    }
}