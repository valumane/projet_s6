package entity.model;

import java.io.Serializable;

import common.item.Item;
import mvc.Model;
import common.entity.Character;
import common.entity.Hero;
import map.model.Room;

public class HeroModel extends Character implements Serializable, Model {

    private Hero hero;

    public HeroModel(Hero hero) {
        this.hero = hero;
    }

    @Override
    public void run() {
    }
    
    public Item dropItem(String itemName) {
    	this.hero.dropItem(itemName);
    }

    public Room getCurrentRoom(){
        Room r= this.hero.getRoom();
        return r;
    }
    
    
}
