package location;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import entity.Character;
import entity.Hero;
import entity.Item;

public abstract class Room implements Serializable {
    private static final long serialVersionUID = 1L;
    private final String name;
    private final String description;
    private final Map<String, Exit> exits;
    private final List<Item> items;
    private final List<Character> characters;

    protected Room(String name, String description) {
        this.name = name;
        this.description = description;
        this.exits = new HashMap<>();
        this.items = new ArrayList<>();
        this.characters = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void addExit(String key, Exit exit) {
        exits.put(key.toLowerCase(), exit);
    }

    public Exit getExit(String key) {
        return exits.get(key.toLowerCase());
    }

    public Map<String, Exit> getExits() {
        return exits;
    }

    public void addItem(Item i) {
        items.add(i);
    }

    public void removeItem(Item i) {
        items.remove(i);
    }

    public List<Item> getItems() {
        return items;
    }

    public void addCharacter(Character c) {
        characters.add(c);
    }

    public void removeCharacter(Character c) {
        characters.remove(c);
    }

    public List<Character> getCharacters() {
        return characters;
    }

    public void enter(Hero h) {
        // comportement spécial si besoin
    }

    public String describe() {
        StringBuilder sb = new StringBuilder();
        sb.append(description).append("\n");

        if (!exits.isEmpty()) {
            sb.append("exits: ");
            for (String dir : exits.keySet()) {
                sb.append(dir).append(" ");
            }
            sb.append("\n");
        }

        if (!items.isEmpty()) {
            sb.append("items here:\n");
            for (Item i : items) {
                sb.append(" - ").append(i.getDescription()).append("\n");
            }
        }

        if (!characters.isEmpty()) {
            sb.append("characters here:\n");
            for (entity.Character c : characters) {
                sb.append(" - ").append(c.getName())
                        .append(" (").append(c.getHp()).append(" hp)\n");
            }
        }

        return sb.toString();
    }

}
