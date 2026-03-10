package map.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import entity.model.Item;
import entity.model.Character;
/**
 * MODEL - Représente l'état d'une salle.
 * Aucun System.out.println ici : le Model ne sait pas qu'il y a un affichage.
 */
public class Room {

    private final String name;
    private final String description;
    private final Map<String, Room> exits;   // "north" -> Room
    private final List<Item> items;
    private final List<Character> characters;

    public Room(String name, String description) {
        this.name = name;
        this.description = description;
        this.exits = new HashMap<>();
        this.items = new ArrayList<>();
        this.characters = new ArrayList<>();
    }

    // --- Exits ---
    public void addExit(String direction, Room room) {
        exits.put(direction.toLowerCase(), room);
    }


    /** @return la Room dans cette direction, ou null si inexistante */
    public Room getExit(String direction) {
        return exits.get(direction.toLowerCase());
    }

    public Map<String, Room> getExits() {
        return exits;
    }


    // --- Items ---
    public void addItem(Item item) {
        items.add(item);
    }


    /** @return true si l'item a été retiré, false s'il n'était pas là */
    public boolean removeItem(Item item) {
        return items.remove(item);
    }

    public List<Item> getItems() {
        return items;
    }


    /** Cherche un item par nom (insensible à la casse) */
    public Item findItemByName(String name) {
        for (Item item : items) {
            if (item.getName().equalsIgnoreCase(name)) {
                return item;
            }
        }
        return null;
    }
    

    // --- Characters ---
    public void addCharacter(Character c) {
        characters.add(c);
    }

    public boolean removeCharacter(Character c) {
        return characters.remove(c);
    }

    public List<Character> getCharacters() {
        return characters;
    }


    // --- Getters ---
    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
}