package src.entity.model;

import java.io.Serializable;

import common.item.Item;
import mvc.Model;
import src.common.Character;
import src.common.Hero;
import map.model.Room;

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
}
