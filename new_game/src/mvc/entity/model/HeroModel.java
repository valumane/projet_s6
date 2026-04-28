package mvc.entity.model;

import java.util.ArrayList;
import java.util.List;

import common.entity.Hero;
import common.item.Item;
import common.map.Room;
import common.languages.Languages;
import mvc.mvc.Model;
import mvc.map.MapLayout;
import common.item.Scroll;
import common.item.Weapon;

public class HeroModel implements Model {

    public interface Listener {
        void onHealthChanged(int newHp);

        void onLocationChanged(String newLocation);
    }

    private static final double ROOM_X = MapLayout.ROOM_X;
    private static final double ROOM_Y = MapLayout.ROOM_Y;
    private static final double ROOM_W = MapLayout.ROOM_W;
    private static final double ROOM_H = MapLayout.ROOM_H;

    private static final double DEFAULT_X = ROOM_X + ROOM_W / 2.0;
    private static final double DEFAULT_Y = ROOM_Y + ROOM_H / 2.0;

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

    public void placeAfterCrossing(String direction) {
        double x = this.x;
        double y = this.y;

        double minX = ROOM_X + MapLayout.HERO_RADIUS;
        double maxX = ROOM_X + ROOM_W - MapLayout.HERO_RADIUS;
        double minY = ROOM_Y + MapLayout.HERO_RADIUS;
        double maxY = ROOM_Y + ROOM_H - MapLayout.HERO_RADIUS;

        double enterMargin = 35.0;

        switch (direction) {
            case "north" -> {
                x = clamp(x, minX, maxX);
                y = maxY - enterMargin;
            }
            case "south" -> {
                x = clamp(x, minX, maxX);
                y = minY + enterMargin;
            }
            case "east" -> {
                x = minX + enterMargin;
                y = clamp(y, minY, maxY);
            }
            case "west" -> {
                x = maxX - enterMargin;
                y = clamp(y, minY, maxY);
            }
            default -> resetPosition();
        }

        setPosition(x, y);
    }

    private double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
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

    public int getDamage() {
        return hero.getDamage();
    }

    public int getMaxHealth() {
        return hero.getMaxHp();
    }

    public void increaseMaxHp(int amount) {
        hero.increaseMaxHp(amount);
        notifyHealthChanged();
    }

    public void increaseBaseDamage(int amount) {
        hero.increaseBaseDamage(amount);
    }

    public void healToMax() {
        hero.setHp(hero.getMaxHp());
        notifyHealthChanged();
    }

    public List<Item> getInventory() {
        return hero.getInventory();
    }

    public Weapon getEquippedWeapon() {
        return hero.getEquippedWeapon();
    }

    public String getEquippedWeaponName() {
        return hero.getEquippedWeaponName();
    }

    public boolean isEquippedWeaponRanged() {
        return hero.isEquippedWeaponRanged();
    }

    public boolean isEquippedWeaponMelee() {
        return hero.isEquippedWeaponMelee();
    }

    public String useInventorySlot(int slotIndex) {
        List<Item> inventory = hero.getInventory();

        if (slotIndex < 0 || slotIndex >= 9) {
            return Languages.t("hero.invalidSlot");
        }

        if (slotIndex >= inventory.size()) {
            return Languages.t("hero.noItemInSlot");
        }

        Item item = inventory.get(slotIndex);

        if (item instanceof Weapon weapon) {
            boolean equipped = hero.equipWeapon(weapon);

            if (equipped) {
                syncState();
                return Languages.tf("hero.weaponEquipped", weapon.getName(), hero.getDamage());
            }

            return Languages.t("hero.cannotEquip");
        }

        if (item instanceof Scroll scroll) {
            scroll.use(hero);
            syncState();
            return Languages.tf("hero.spellUsed", scroll.getName(), hero.getHp(), hero.getMaxHp());
        }

        return Languages.tf("hero.noEffect", item.getName());
    }

    public void healPercent(int percent) {
        hero.healPercentOfMaxHp(percent);
        notifyHealthChanged();
    }

    public void increaseDamageByPercent(int percent) {
        hero.increaseDamagePercent(percent);
        syncState();
    }
}