package entity.tests;

import entity.*;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class PotionIT {

    private Hero hero;
    private Potion potion;

    @Before
    public void setUp() {
        Bag backpack = new Bag("Backpack", 10);
        hero = new Hero("Healer", 50, backpack);
        potion = new Potion("Pot", 30);
    }

    @Test
    public void testPotionConstruct() {
        assertEquals("Pot", potion.getName());
    }

    @Test
    public void testUsePotion() {
        int initialHp = hero.getHp();
        potion.use(hero);
        assertEquals(initialHp + 30, hero.getHp());
    }

    @Test
    public void testPotionDesc() {
        assertEquals("Pot (potion, +30 hp)", potion.getDescription());
    }
}
