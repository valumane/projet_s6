package game.tests;

import entity.*;
import location.*;
import game.*;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import java.util.List;

public class TakeCommandIT {

    private TakeCommand takeCmd;
    private Game game;
    private Hero hero;
    private BasicRoom room;
    private GameMap map;
    private Weapon sword;

    @Before
    public void setUp() {
        takeCmd = new TakeCommand();
        map = new GameMap();
        room = new BasicRoom("Test Room", "A room");
        map.addRoom(room);
        Bag backpack = new Bag("Backpack", 10);
        hero = new Hero("Hero", 100, backpack);
        sword = new Weapon("Sword", 15);
        game = new Game(map, hero, room);
    }

    @Test
    public void testTakeCommandConstruct() {
        assertEquals("TAKE", takeCmd.getName());
    }

    @Test
    public void testTakeNoArgs() {
        takeCmd.execute(game, List.of());
    }

    @Test
    public void testTakeItemNotInRoom() {
        takeCmd.execute(game, List.of("Sword"));
        assertTrue(hero.getInventory().isEmpty());
    }

    @Test
    public void testTakeItemSuccess() {
        room.addItem(sword);
        assertTrue(room.getItems().contains(sword));
        takeCmd.execute(game, List.of("Sword"));
        assertFalse(room.getItems().contains(sword));
        assertTrue(hero.getInventory().contains(sword));
    }

    @Test
    public void testTakeItemWithSpaces() {
        Weapon bigSword = new Weapon("Big Sword", 20);
        room.addItem(bigSword);
        takeCmd.execute(game, List.of("Big", "Sword"));
        assertFalse(room.getItems().contains(bigSword));
        assertTrue(hero.getInventory().contains(bigSword));
    }

    @Test
    public void testTakeItemCaseIns() {
        room.addItem(sword);
        takeCmd.execute(game, List.of("SWORD"));
        takeCmd.execute(game, List.of("sword"));
    }
}
