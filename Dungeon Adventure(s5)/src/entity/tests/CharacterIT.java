package entity.tests;

import entity.*;
import entity.Character;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class CharacterIT {

    private Character hero;
    private Character enemy;
    private Weapon sword;
    private Potion potion;

    @Before
    public void setUp() {
        hero = new Character("Hero", 100);
        enemy = new Character("Enemy", 80);
        sword = new Weapon("Sword", 5);
        potion = new Potion("Health Potion", 30);
    }

    @Test
    public void testCharConstructTwoParam() {
        assertEquals("Hero", hero.getName());
        assertEquals(100, hero.getHp());
        assertEquals(10, hero.getBaseDamage());
    }

    @Test
    public void testCharConstructThreeParam() {
        Character mage = new Character("Mage", 60, 8);
        assertEquals("Mage", mage.getName());
        assertEquals(60, mage.getHp());
        assertEquals(8, mage.getBaseDamage());
    }

    @Test
    public void testSetHp() {
        hero.setHp(75);
        assertEquals(75, hero.getHp());
    }

    @Test
    public void testSetBaseDamage() {
        hero.setBaseDamage(12);
        assertEquals(12, hero.getBaseDamage());
    }

    @Test
    public void testGetAtkDmgNoWeapon() {
        assertEquals(10, hero.getAttackDamage());
    }

    @Test
    public void testGetAtkDmgWithWeapon() {
        hero.setWeapon(sword);
        assertEquals(25, hero.getAttackDamage());
    }

    @Test
    public void testAtkReduceHp() {
        int initialHp = enemy.getHp();
        hero.attack(enemy);
        assertTrue(enemy.getHp() < initialHp);
    }

    @Test
    public void testSetWeapon() {
        hero.setWeapon(sword);
        assertSame(sword, hero.getWeapon());
    }

    @Test
    public void testSetWeaponAddsInventory() {
        hero.setWeapon(sword);
        assertTrue(hero.getInventory().contains(sword));
    }

    @Test
    public void testTakeItem() {
        hero.take(potion);
        assertTrue(hero.getInventory().contains(potion));
    }

    @Test
    public void testRemoveFromInventory() {
        hero.take(potion);
        assertTrue(hero.removeFromInventory(potion));
        assertFalse(hero.getInventory().contains(potion));
    }

    @Test
    public void testInventoryIsEmptyWhenThereIsNothingInInventoryWhichSeemsNormalButWeNeverKnow() {
        assertTrue(hero.getInventory().isEmpty());
    }
}
