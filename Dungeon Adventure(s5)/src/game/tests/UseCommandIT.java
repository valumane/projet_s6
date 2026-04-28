package game.tests;

import entity.*;
import location.*;
import game.*;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import java.util.List;

public class UseCommandIT {

    private UseCommand useCmd;
    private Game game;
    private Hero hero;
    private BasicRoom room;
    private GameMap map;

    @Before
    public void setUp() {
        useCmd = new UseCommand();
        map = new GameMap();
        room = new BasicRoom("Test Room", "A room");
        map.addRoom(room);
        Bag backpack = new Bag("Backpack", 10);
        hero = new Hero("Hero", 100, backpack);
        game = new Game(map, hero, room);
    }

    @Test
    public void testUseCommandConstruct() {
        assertEquals("USE", useCmd.getName());
    }

    @Test
    public void testUseNoArgs() {
        useCmd.execute(game, List.of());
    }

    @Test
    public void testUseItemNotInInventory() {
        useCmd.execute(game, List.of("Potion"));
    }

    @Test
    public void testUseWeapon() {
        Weapon sword = new Weapon("Sword", 15);
        hero.take(sword);
        useCmd.execute(game, List.of("Sword"));
        assertSame(sword, hero.getWeapon());
    }

    @Test
    public void testUseItemWithSpaces() {
        Weapon bigSword = new Weapon("Big Sword", 20);
        hero.take(bigSword);
        useCmd.execute(game, List.of("Big", "Sword"));
        assertSame(bigSword, hero.getWeapon());
    }
}
