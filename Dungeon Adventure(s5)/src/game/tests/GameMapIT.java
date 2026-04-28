package game.tests;

import location.*;
import game.*;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class GameMapIT{

    private GameMap gameMap;
    private BasicRoom room1;
    private BasicRoom room2;

    @Before
    public void setUp(){
        gameMap = new GameMap();
        room1 = new BasicRoom("Room1", "First room");
        room2 = new BasicRoom("Room2", "Second room");
    }

    @Test
    public void testGameMapConstruct(){
        assertNotNull(gameMap);
    }

    @Test
    public void testAddRoom() {
        gameMap.addRoom(room1);
    }

    @Test
    public void testGetRoomExists(){
        gameMap.addRoom(room1);
        Room result = gameMap.getRoom("Room1");
        assertSame(room1, result);
    }

    @Test
    public void testGetRoomNotExists(){
        Room result = gameMap.getRoom("NonExistent");
        assertNull(result);
    }

    @Test
    public void testAddMultipleRooms(){
        gameMap.addRoom(room1);
        gameMap.addRoom(room2);
        assertSame(room1, gameMap.getRoom("Room1"));
        assertSame(room2, gameMap.getRoom("Room2"));
    }
}
