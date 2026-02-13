package location.tests;

import entity.*;
import location.*;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class RoomIT{

    private BasicRoom room;
    private Hero hero;
    private Item sword;
    private Monster monster;

    @Before
    public void setUp() {
        room = new BasicRoom("Test Room", "test room for test");
        hero = new Hero("Hero", 100, null);
        sword = new Weapon("Sword",  1000);
        monster = new Monster("Goblin", 30);
    }

    @Test
    public void testRoomConstruct(){
        assertEquals("Test Room", room.getName());
        assertTrue(room.getExits().isEmpty());
        assertTrue(room.getItems().isEmpty());
        assertTrue(room.getCharacters().isEmpty());
    }

    @Test
    public void testAddExit(){
        BasicRoom targetRoom = new BasicRoom("Target", "Target room");
        SimpleExit exit = new SimpleExit(targetRoom);

        room.addExit("north", exit);
        assertSame(exit, room.getExit("north"));
    }

    @Test
    public void testAddExitCase(){
        BasicRoom targetRoom = new BasicRoom("Target", "Target room");
        SimpleExit exit = new SimpleExit(targetRoom);
        room.addExit("NORTH", exit);
        assertSame(exit, room.getExit("north"));
    }

    @Test
    public void testGetExitNotExists(){
        assertNull(room.getExit("north"));
    }

    @Test
    public void testAddItem(){
        room.addItem(sword);
        assertTrue(room.getItems().contains(sword));
        assertEquals(1, room.getItems().size());
    }

    @Test
    public void testRemoveItem(){
        room.addItem(sword);
        room.removeItem(sword);
        assertFalse(room.getItems().contains(sword));
        assertTrue(room.getItems().isEmpty());
    }

    @Test
    public void testAddCharacter(){
        room.addCharacter(monster);
        assertTrue(room.getCharacters().contains(monster));
        assertEquals(1, room.getCharacters().size());
    }

    @Test
    public void testRemoveCharacter(){
        room.addCharacter(monster);
        room.removeCharacter(monster);
        assertFalse(room.getCharacters().contains(monster));
        assertTrue(room.getCharacters().isEmpty());
    }

    @Test
    public void testEnterHero(){
        room.enter(hero);
        room.addCharacter(hero);
        assertTrue(room.getCharacters().contains(hero));
    }

    @Test
    public void testDescEmptyRoom(){
        String description = room.describe();
        assertTrue(description.contains("test room for test"));
    }

    @Test
    public void testDescRoomNotEmpty(){
        BasicRoom targetRoom = new BasicRoom("Target", "Target room");
        room.addExit("north", new SimpleExit(targetRoom));

        String description = room.describe();
        assertTrue(description.contains("exits:"));
        assertTrue(description.contains("north"));
    }

    @Test
    public void testDescRoomWithItems(){
        room.addItem(sword);
        String description = room.describe();
        assertTrue(description.contains("items here:"));
        assertTrue(description.contains("Sword"));
    }

    @Test
    public void testDescRoomWithChar(){
        room.addCharacter(monster);
        String description = room.describe();
        assertTrue(description.contains("characters here:"));
        assertTrue(description.contains("Goblin"));
    }
}
