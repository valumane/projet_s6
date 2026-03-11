package src.entity.model;

import java.io.Serializable;
import mvc.Model;
import src.common.Character;
import src.common.Hero;
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
    
    
}
