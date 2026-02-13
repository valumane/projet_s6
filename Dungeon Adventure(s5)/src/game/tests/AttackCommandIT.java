package game.tests;

import entity.*;
import location.*;
import game.*;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import java.util.List;
import java.util.ArrayList;

public class AttackCommandIT{

    private AttackCommand attackCmd;
    private Game game;
    private Hero hero;
    private Monster monster;
    private BasicRoom room;
    private GameMap map;

    @Before
    public void setUp(){
        attackCmd = new AttackCommand();
        map = new GameMap();
        room = new BasicRoom("Combat Room", "A room for fighting");
        map.addRoom(room);
        Bag backpack = new Bag("Backpack", 10);
        hero = new Hero("Hero", 100, backpack);
        monster = new Monster("Goblin", 30);
        game = new Game(map, hero, room);
    }

    @Test
    public void testAttackCommandConstruct(){
        assertEquals("ATTACK", attackCmd.getName());
    }

    @Test
    public void testAttackNoEnemies(){
        attackCmd.execute(game, new ArrayList<>());
    }

    @Test
    public void testAttackSpecificEnemy(){
        room.addCharacter(monster);
        int initialMonsterHp = monster.getHp();
        attackCmd.execute(game, List.of("Goblin"));
        assertTrue(monster.getHp() < initialMonsterHp);
    }

    @Test
    public void testAttackFirstEnemyNoArg(){
        room.addCharacter(monster);
        int initialMonsterHp = monster.getHp();
        attackCmd.execute(game, new ArrayList<>());
        assertTrue(monster.getHp() < initialMonsterHp);
    }

    @Test
    public void testAttackNonExistentEnemy(){
        Monster otherMonster = new Monster("Orc", 40);
        room.addCharacter(otherMonster);
        attackCmd.execute(game, List.of("Goblin")); // pas de gob dans la room
        assertTrue(room.getCharacters().contains(otherMonster));
    }

    @Test
    public void testKillEnemy(){
        Monster weakMonster = new Monster("Slime", 5);
        room.addCharacter(weakMonster);
        attackCmd.execute(game, List.of("Slime"));
        assertTrue(weakMonster.getHp() <= 0);
        assertFalse(room.getCharacters().contains(weakMonster));
    }

    @Test
    public void testEnemyCounterAttack(){
        room.addCharacter(monster);
        int initialHeroHp = hero.getHp();
        attackCmd.execute(game, List.of("Goblin"));
        assertTrue(hero.getHp() < initialHeroHp);
    }

    @Test
    public void testHeroDeath(){
        Hero weakHero = new Hero("WeakHero", 1, new Bag("Bag", 5));
        Monster strongMonster = new Monster("Dragon", 100);
        room.addCharacter(strongMonster);
        Game testGame = new Game(map, weakHero, room);

        attackCmd.execute(testGame, List.of("Dragon"));
        assertTrue(weakHero.getHp() <= 0);
    }
}
