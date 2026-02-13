package location.tests;

import entity.*;
import location.*;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class TreasureRoomIT {

    private TreasureRoom room;
    private Hero hero;
    private Item treasure;

    @Before
    public void setUp() {
        room = new TreasureRoom("Treasure Room", "A room full of gold and snails");
        hero = new Hero("Hero", 100, null);
        treasure = new Weapon("Golden Sword", 25);
    }

    @Test
    public void testTreasureRoomConstruct() {
        assertEquals("Treasure Room", room.getName());
        assertTrue(room.describe().contains("A room full of gold and snails"));
    }

    @Test
    public void testTreasureRoomWithItems() {
        room.addItem(treasure);
        String description = room.describe();
        assertTrue(description.contains("items here:"));
        assertTrue(description.contains("Golden Sword"));
    }

    @Test
    public void testTreasureRoomEnter() {
        room.enter(hero);
        room.addCharacter(hero);
        assertTrue(room.getCharacters().contains(hero));
    }
}
