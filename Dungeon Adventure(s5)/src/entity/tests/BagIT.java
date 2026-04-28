package entity.tests;

import entity.*;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class BagIT {

    private Bag bag;
    private Weapon sword;
    private Potion potion;

    @Before
    public void setUp() {
        bag = new Bag("Adv Bag", 5);
        sword = new Weapon("Sword", 15);
        potion = new Potion("Potion", 20);
    }

    @Test
    public void testBagConstruct() {
        assertEquals("Adv Bag", bag.getName());
        assertEquals(5, bag.getCapacity());
        assertTrue(bag.getContent().isEmpty());
    }

    @Test
    public void testAddItem() {
        assertTrue(bag.addItem(sword));
        assertTrue(bag.getContent().contains(sword));
    }

    @Test
    public void testRemoveItem() {
        bag.addItem(potion);
        assertTrue(bag.removeItem(potion));
        assertFalse(bag.getContent().contains(potion));
    }

    @Test
    public void testBagDesc() {
        assertEquals("Adv Bag (bag, capacity 5)", bag.getDescription());
    }
}
