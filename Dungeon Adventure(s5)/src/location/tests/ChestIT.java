package location.tests;

import entity.*;
import location.*;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class ChestIT{

    private Chest chest;
    private Hero hero;
    private Weapon sword;
    private Potion potion;

    @Before
    public void setUp(){
        chest = new Chest("Treasure Chest", true);
        hero = new Hero("Hero", 100, null);
        sword = new Weapon("Sword", 15);
        potion = new Potion("Health Potion", 30);
    }

    @Test
    public void testChestConstruct(){
        assertEquals("Treasure Chest", chest.getName());
        assertTrue(chest.getDescription().contains("locked"));
        assertTrue(chest.getContent().isEmpty());
    }

    @Test
    public void testAddItem(){
        chest.addItem(sword);
        assertTrue(chest.getContent().contains(sword));
        assertEquals(1, chest.getContent().size());
    }
    

    @Test
    public void testOpenLockedChest(){
        chest.addItem(sword);
        chest.open(hero);
        assertTrue(hero.getInventory().isEmpty());
        assertFalse(chest.getContent().isEmpty());
    }

    @Test
    public void testOpenUnlockedChest(){
        Chest unlockedChest = new Chest("Unlocked Chest", false);
        unlockedChest.addItem(sword);
        unlockedChest.addItem(potion);
        unlockedChest.open(hero);
        assertTrue(hero.getInventory().contains(sword));
        assertTrue(hero.getInventory().contains(potion));
        assertTrue(unlockedChest.getContent().isEmpty());
    }

    @Test
    public void testOpenEmptyChest(){
        Chest emptyChest = new Chest("Empty Chest", false);
        emptyChest.open(hero);
        assertTrue(hero.getInventory().isEmpty());
    }

    @Test
    public void testUseChest(){
        Chest unlockedChest = new Chest("Unlocked Chest", false);
        unlockedChest.addItem(sword);
        unlockedChest.use(hero);
        assertTrue(hero.getInventory().contains(sword));
        assertTrue(unlockedChest.getContent().isEmpty());
    }

    @Test
    public void testChestDesc(){
        chest.addItem(sword);
        chest.addItem(potion);
        String description = chest.getDescription();
        assertTrue(description.contains("Treasure Chest"));
        assertTrue(description.contains("locked"));
        assertTrue(description.contains("2 item(s)"));
    }
}
