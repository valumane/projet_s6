package common;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Room implements Serializable {
    private static final long serialVersionUID = 1L;
    private final String name;
    private final Map<String, Exit> exits;
    private final List<Item> items;
    private final List<Character> characters;

    protected Room(String name) {
        this.name = name;
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
}
