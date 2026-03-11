package common.item;

import common.entity.Hero;

public abstract class UsableItem extends Item {

	public UsableItem(String name) {
		super(name);
	}

	public abstract void use(Hero h);
}
