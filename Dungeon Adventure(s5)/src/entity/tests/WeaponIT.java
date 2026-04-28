package entity.tests;

import entity.*;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class WeaponIT {

    private Hero hero;
    private Weapon sword;
    private Weapon bazooka;

    @Before
    public void setUp() {
        Bag backpack = new Bag("Backpack", 10);
        hero = new Hero("Warrior", 100, backpack);
        sword = new Weapon("Sword", 15);
        bazooka = new Weapon("Bazooka", 50);
    }

    @Test
    public void testWeaponConstruct() {
        assertEquals("Sword", sword.getName());
        assertEquals(15, sword.getDamage());
    }

    @Test
    public void testUseWeapon() {
        sword.use(hero);
        assertSame(sword, hero.getWeapon());
        assertTrue(hero.getInventory().contains(sword));
    }

    @Test
    public void testWeaponDesc() {
        assertEquals("Sword (weapon, +15 dmg)", sword.getDescription());
    }

    @Test
    public void testSwitchWeapon() {
        sword.use(hero);
        bazooka.use(hero);
        assertSame(bazooka, hero.getWeapon());
        assertTrue(hero.getInventory().contains(sword));
        assertTrue(hero.getInventory().contains(bazooka));
    }
}
