package location.tests;

import entity.*;
import location.*;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class SimpleExitIT{

    private BasicRoom targetRoom;
    private Hero hero;
    private SimpleExit exit;

    @Before
    public void setUp(){
        targetRoom = new BasicRoom("Target", "Target room");
        hero = new Hero("Hero",100,null);
        exit = new SimpleExit(targetRoom);
    }

    @Test
    public void testSimpleExitConstruct(){
        assertSame(targetRoom, exit.getTarget());
    }

    @Test
    public void testSimpleExitCanCross(){
        assertTrue(exit.canCross(hero));
    }

    @Test
    public void testSimpleExitCross(){
        Room result = exit.cross(hero);
        assertSame(targetRoom, result);
    }
}
