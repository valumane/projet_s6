package game.tests;

import entity.*;
import location.*;
import game.*;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import java.util.List;

public class DropCommandIT{

    private DropCommand dropCmd;
    private Game game;
    private Hero hero;
    private BasicRoom room;
    private GameMap map;
    private Weapon sword;

    @Before
    public void setUp(){
        dropCmd = new DropCommand();
        map = new GameMap();
        room = new BasicRoom("Test Room", "A room");
        map.addRoom(room);
        Bag backpack = new Bag("Backpack", 10);
        hero = new Hero("Hero", 100, backpack);
        sword = new Weapon("Sword", 15);
        game = new Game(map, hero, room);
    }

    @Test
    public void testDropCommandConstruct(){
        assertEquals("DROP", dropCmd.getName());
    }

    @Test
    public void testDropNoArgs(){
        dropCmd.execute(game, List.of());
    }

    @Test
    public void testDropItemNotInInventory(){
        dropCmd.execute(game, List.of("NonExistentItem"));
        assertTrue(room.getItems().isEmpty());
    }

    @Test
    public void testDropItemSuccess(){
        hero.take(sword);
        assertTrue(hero.getInventory().contains(sword));
        dropCmd.execute(game, List.of("Sword"));
        assertFalse(hero.getInventory().contains(sword));
        assertTrue(room.getItems().contains(sword));
    }

    @Test
    public void testDropItemWithSpaces() {
        Weapon bigSword = new Weapon("Big Sword", 20);
        hero.take(bigSword);
        dropCmd.execute(game, List.of("Big", "Sword"));
        assertFalse(hero.getInventory().contains(bigSword));
        assertTrue(room.getItems().contains(bigSword));
    }
}
