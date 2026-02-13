package location.tests;

import entity.*;
import location.*;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class LockedExitIT{

    private BasicRoom targetRoom;
    private Hero hero;
    private LockedExit exit;

    @Before
    public void setUp(){
        targetRoom = new BasicRoom("Target","Target room");
        hero = new Hero("Hero",100,null);
        exit = new LockedExit(targetRoom, true);
    }

    @Test
    public void testLockedExitConstruct(){
        assertSame(targetRoom, exit.getTarget());
        assertFalse(exit.canCross(hero));
    }

    @Test
    public void testLockedExitWhenLocked(){
        assertFalse(exit.canCross(hero));
        assertNull(exit.cross(hero));
    }

    @Test
    public void testLockedExitWhenUnlocked(){
        exit.unlock();
        assertTrue(exit.canCross(hero));
        assertSame(targetRoom, exit.cross(hero));
    }

    @Test
    public void testUnlocking(){
        exit.unlock();
        assertTrue(exit.canCross(hero));
    }
}
