package mvc.map.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import common.item.Item;
import common.entity.Character;
import common.map.Exit;
import common.map.Room;
import mvc.mvc.Model;

/**
 * MODEL - Représente l'état d'une salle.
 * Aucun System.out.println ici : le Model ne sait pas qu'il y a un affichage.
 */
public class RoomModel implements Model {
	
	private final Room room;

    public RoomModel(Room room) {
        this.room = room;
    }
    
    @Override
    public void run() {
    }
    
    public String getName() {
        return room.getName();
    }
    
    public String getDescription() {
        return room.getDescription();
    }
    
    public Map<String, Exit> getExits() {
        return room.getExits();
    }

    public List<Item> getItems() {
        return room.getItems();
    }

    public List<Character> getCharacters() {
        return room.getCharacters();
    }

    public Room getRoom() {
        return room;
    }

    
}