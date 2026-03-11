package common.item;

import common.entity.Hero;

public class Key extends UsableItem {

	public Key(String name) {
		super(name);
	}

	@Override
	public void use(Hero h) {
		// TODO Auto-generated method stub
		// Vérifier les exits autour du héro.
		// Si une des exits est une LockedExit, faire son getKey()
		// Si son getKey == this alors on fais unlock().
	}

}
