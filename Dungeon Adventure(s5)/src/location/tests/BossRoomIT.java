package location.tests;

import entity.*;
import location.*;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class BossRoomIT{

    private BossRoom room;
    private Hero hero;
    private Boss boss;

    @Before
    public void setUp(){
        room = new BossRoom("Boss Lair", "The final boss room");
        hero = new Hero("Hero", 100, null);
        boss = new Boss("ImmortalSnail", 1000000);
    }

    @Test
    public void testBossRoomConstruct(){
        assertEquals("Boss Lair", room.getName());
        assertTrue(room.describe().contains("The final boss room"));
    }

    @Test
    public void testBossRoomWithBoss(){
        room.addCharacter(boss);
        String description = room.describe();
        assertTrue(description.contains("ImmortalSnail"));
        assertTrue(description.contains("1000000 hp"));
    }

    @Test
    public void testBossRoomEnter(){
        room.enter(hero);
        room.addCharacter(hero);
        assertTrue(room.getCharacters().contains(hero));
    }
}
