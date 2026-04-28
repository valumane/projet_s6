package entity.tests;

import entity.*;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class HeroIT {

    private Hero hero;
    private Bag backpack;
    private Weapon sword;
    private Potion potion;

    @Before
    public void setUp() {
        backpack = new Bag("Backpack", 10);
        hero = new Hero("Adam", 100, backpack);
        sword = new Weapon("BigAhhSword", 20);
        potion = new Potion("Heal Potion", 50);
    }

    @Test
    public void testHeroConstruct() {
        assertEquals("Adam", hero.getName());
        assertEquals(100, hero.getHp());
        assertSame(backpack, hero.getBackpack());
    }

    @Test
    public void testGetBackpack() {
        assertSame(backpack, hero.getBackpack());
    }

    @Test
    public void testDropItem() {
        hero.take(sword);
        Item dropped = hero.drop("BigAhhSword");
        assertSame(sword, dropped);
        assertFalse(hero.getInventory().contains(sword));
    }

    @Test
    public void testDropItemNotInInventory() {
        Item dropped = hero.drop("BlipBlop");
        assertNull(dropped);
    }

    @Test
    public void testBinItem() {
        hero.take(potion);
        boolean binned = hero.bin("Heal Potion");
        assertTrue(binned);
        assertFalse(hero.getInventory().contains(potion));
    }

    @Test
    public void testBinItemNotInInventory() {
        boolean binned = hero.bin("BlopBlip");
        assertFalse(binned);
    }
}
