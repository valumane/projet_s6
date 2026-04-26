package mvc.entity.model;

import java.util.ArrayList;
import java.util.List;

import common.entity.Hero;
import common.item.Item;
import common.map.Room;
import mvc.mvc.Model;

public class HeroModel implements Model {

    public interface Listener {
        void onHealthChanged(int newHp);

        void onLocationChanged(String newLocation);
    }

    private static final double DEFAULT_X = 360;
    private static final double DEFAULT_Y = 250;

    private final Hero hero;
    private final List<Listener> listeners = new ArrayList<>();

    private double x = DEFAULT_X;
    private double y = DEFAULT_Y;

    public HeroModel(Hero hero) {
        this.hero = hero;
    }

    @Override
    public void run() {
    }

    public void addListener(Listener listener) {
        if (listener != null) {
            listeners.add(listener);
        }
    }

    private void notifyHealthChanged() {
        for (Listener listener : listeners) {
            listener.onHealthChanged(hero.getHp());
        }
    }

    private void notifyLocationChanged() {
        if (hero.getRoom() == null) {
            return;
        }

        String roomName = hero.getRoom().getName();
        for (Listener listener : listeners) {
            listener.onLocationChanged(roomName);
        }
    }

    public void syncState() {
        notifyHealthChanged();
        notifyLocationChanged();
    }

    public Hero getHero() {
        return this.hero;
    }

    public Item drop(String itemName) {
        return hero.dropItem(itemName);
    }

    public Room getRoom() {
        return this.hero.getRoom();
    }

    public void setRoom(Room room) {
        this.hero.setCurrentRoom(room);
        notifyLocationChanged();
    }

    public String getName() {
        return hero.getName();
    }

    public int getHealth() {
        return hero.getHp();
    }

    public int removeHp(int amount) {
        int newHp = Math.max(0, hero.getHp() - amount);
        hero.setHp(newHp);
        notifyHealthChanged();
        return newHp;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public void setPosition(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public void resetPosition() {
        this.x = DEFAULT_X;
        this.y = DEFAULT_Y;
    }
}