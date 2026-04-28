package entity.tests;

import entity.*;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class KeyIT {

    private Key key;
    private Hero hero;

    @Before
    public void setUp() {
        key = new Key("Boss Key");
        Bag backpack = new Bag("Backpack", 10);
        hero = new Hero("Warrior", 100, backpack);
    }

    @Test
    public void testKeyConstruct() {
        assertEquals("Boss Key", key.getName());
    }

    @Test
    public void testKeyDesc() {
        assertEquals("Boss Key (key)", key.getDescription());
        // key.getDescription();
    }

    @Test
    public void testUseKey() {
        key.use(hero);
        assertEquals(100, hero.getHp());
        assertNull(hero.getWeapon());
        // on verifie juste que utiliser une cle sa modifie pas le heros
    }
}
