package entity.model;

import java.io.Serializable;

import common.item.Item;
import mvc.Model;
import common.entity.Character;
import common.entity.Hero;
import common.map.Room;

public class HeroModel implements Model {

    private Hero hero;

    public HeroModel(Hero hero) {
        this.hero = hero;
    }

    @Override
    public void run() {
    }
     
    public void drop(Item item) {
    	this.hero.dropItem(item);
    }    

    public Room getRoom(){
        return this.hero.getRoom();
        
    }
}
