package game.tests;

import entity.*;
import location.*;
import game.*;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import java.util.List;

public class GameIT{

    private Game game;
    private Hero hero;
    private BasicRoom startRoom;
    private GameMap map;

    @Before
    public void setUp(){
        map = new GameMap();
        startRoom = new BasicRoom("Start", "Starting room");
        map.addRoom(startRoom);
        Bag backpack = new Bag("Backpack", 10);
        hero = new Hero("Hero", 100, backpack);
        game = new Game(map, hero, startRoom);
    }

    @Test
    public void testGameConstruct(){
        assertSame(startRoom, game.getCurrentRoom());
        assertSame(hero, game.getHero());
        assertSame(map, game.getMap());
        assertTrue(game.getHero().getHp() > 0);
    }

    @Test
    public void testAddCommand(){
        Command testCmd = new TakeCommand();
        game.addCommand(testCmd);
    }

    @Test
    public void testSetCurrRoom(){
        BasicRoom newRoom = new BasicRoom("New Room", "Another room");
        game.setCurrentRoom(newRoom);
        assertSame(newRoom, game.getCurrentRoom());
    }

    @Test
    public void testStop(){
        game.stop();
    }

    @Test
    public void testExecuteCommandUnknown(){
        game.executeCommand("UNKNOWN_COMMAND"); // test qu'une commande inconnue fait pas crash le jeu
    }

    @Test
    public void testExecuteCommand(){
        TakeCommand takeCmd = new TakeCommand();
        game.addCommand(takeCmd);
        game.executeCommand("TAKE sword");
    }
}
