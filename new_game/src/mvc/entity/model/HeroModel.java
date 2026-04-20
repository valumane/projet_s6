package mvc.entity.model;

import common.item.Item;
import common.item.Weapon;
import common.entity.Hero;
import common.map.Room;
import mvc.mvc.Model;

public class HeroModel implements Model {

    public interface Listener {
        void onHealthChanged(int newHp);

        void onLocationChanged(String newLocation);
    }

    private final Hero hero;
    private Listener listener;

    public HeroModel(Hero hero) {
        this.hero = hero;
    }

    @Override
    public void run() {
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    private void notifyHealthChanged() {
        if (listener != null) {
            listener.onHealthChanged(hero.getHp());
        }
    }

    private void notifyLocationChanged() {
        if (listener != null && hero.getRoom() != null) {
            listener.onLocationChanged(hero.getRoom().getName());
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

}
