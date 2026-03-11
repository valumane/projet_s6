package entity.model;

import java.io.Serializable;

public class BagModel extends Item {
    
    private Bag bag;

    public BagModel(Bag bag) {
        this.bag = bag;
    }

    @Override
    public void run() {
    }
        
}