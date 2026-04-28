package game;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import entity.Hero;
import location.Room;

public class Game implements Serializable {

    private Room currentRoom;
    private Hero hero;
    private boolean running;
    private transient Map<String, Command> commands;
    private GameMap map;

    public Game(GameMap map, Hero hero, Room startRoom) {
        this.map = map;
        this.hero = hero;
        this.currentRoom = startRoom;
        this.running = true;
        this.commands = new HashMap<>();
    }

    public void addCommand(Command cmd) {
        commands.put(cmd.getName(), cmd);
    }

    public void play() {
        System.out.println("Welcome to DUNGEON ADVENTURE!");
        System.out.println("You find yourself in the entrance of a dark dungeon, there is two corridors leading west and east.");
        System.out.println("To get out of here you will have to face many challenges and foes.");
        System.out.println("Good luck adventurer!");
        try (Scanner sc = new Scanner(System.in)) {
            while (running) {
                System.out.print("> ");
                if (!sc.hasNextLine()) {
                    break;
                }
                String line = sc.nextLine().trim();
                if (line.isEmpty()) {
                    continue;
                }
                executeCommand(line);
            }
        }
    }

       
    public void executeCommand(String input) {
        String[] parts = input.split("\s+");
        String cmdName = parts[0].toUpperCase();
        Command cmd = commands.get(cmdName);
        if (cmd == null) {
            System.out.println("I don't understand...");
            return;
        }
        List<String> args = Arrays.asList(parts).subList(1, parts.length);
        cmd.execute(this, args);
    }

    public Hero getHero() {
        return hero;
    }

    public Room getCurrentRoom() {
        return currentRoom;
    }

    public void setCurrentRoom(Room room) {
        this.currentRoom = room;
    }

    public void stop() {
        running = false;
    }

    public GameMap getMap() {
        return map;
    }

    public void save(String filename) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename))) {
            oos.writeObject(this);
            System.out.println("Game saved.");
        } catch (Exception e) {
            System.out.println("Error saving game : " + e.getMessage());
        }
    }

    public static Game load(String filename) {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filename))) {
            return (Game) ois.readObject();
        } catch (Exception e) {
            System.out.println("Error loading game : " + e.getMessage());
            return null;
        }
    }

    public void copyStateFrom(Game other) {
        this.map = other.map;
        this.hero = other.hero;
        this.currentRoom = other.currentRoom;
        this.running = other.running;
        // NE PAS copier commands : on garde celles déjà enregistrées dans le main
    }

}
