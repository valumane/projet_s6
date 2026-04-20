package common.entity;

import java.util.List;

import common.item.Bag;
import common.item.Item;
import common.item.Weapon;
import common.map.Room;

public class Hero extends Character {

    private final Bag backpack;
    private Room room;
    private int damage;

    public Hero(String name, int hp, Bag backpack, Room room, int baseDamage) {
        super(name, hp);
        this.backpack = backpack;
        this.room = room;
        this.damage = baseDamage;
    }

    public Bag getBackpack() {
        return this.backpack;
    }

    public void addItem(Item item) {
        if (item instanceof Weapon) {
            Weapon weapon = (Weapon) item;
            this.damage += weapon.getDamage();
        }
        // Voir ici si il ne faudra pas retirer l'objet de la piece, au moment ou on le
        // récupère dans l'inventaire.
        this.addToInventory(item);
    }

    // changement de lucas :
    // changé car on supprimé un objet dans la boucle
    // donc java itere une liste d'objet et on supprime un objet de la meme liste en
    // meme temps
    // genre le foreach il s'attend pas que tu modifie la liste qu'il parcour
    // donc quand on remove on modifie la liste en dehors de l'iterateur
    // pas envie de savoir si sa aurait posé un probleme ou non mais par intuition
    // supprimé un objet d'une liste qui est entrain d'itéré c bof bof
    // enfin, dans un for sa pose pas de probleme, mais la c un foreach, c un
    // iterateur transformé chelou
    // donc solution ? on recup l'objet qu'on veut delete ET ENSUITE on le delete
    public Item dropItem(String itemName) {
        Item found = null;

        for (Item item : this.getInventory()) {
            if (item.getName().equalsIgnoreCase(itemName)) {
                found = item;
                break;
            }
        }

        if (found == null) {
            return null;
        }
        
        if (found instanceof Weapon) {
            Weapon weapon = (Weapon) found;
            this.damage -= weapon.getDamage();
        }

        this.removeFromInventory(found);
        this.room.addItem(found);
        return found;
    }

    // Getter and setters
    public Room getRoom() {
        return this.room;
    }

    public void setCurrentRoom(Room r) {
        this.room = r;
    }

    // Les items du héro sont dans le bagpack
    @Override
    public List<Item> getInventory() {
        return this.backpack.getContent();
    }

    // On pourra utiliser la limite du sac pour limiter le nombre d'objets possible
    // à avoir sur soi
    @Override
    public void addToInventory(Item item) {
        this.backpack.addItem(item);
    }

    @Override
    public void removeFromInventory(Item item) {
        this.backpack.removeItem(item);
    }
}
