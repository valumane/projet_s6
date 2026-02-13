package location.tests;

import entity.*;
import location.*;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class BasicRoomIT {

    private BasicRoom room;
    private Hero hero;

    @Before
    public void setUp() {
        room = new BasicRoom("Basic Room", "A basic room");
        hero = new Hero("Hero", 100, null);
    }

    @Test
    public void testBasicRoomConstruct() {
        assertEquals("Basic Room", room.getName());
        assertTrue(room.describe().contains("A basic room"));
    }

    @Test
    public void testBasicRoomEnter() {
        room.enter(hero);
        room.addCharacter(hero);
        assertTrue(room.getCharacters().contains(hero));
    }
}
