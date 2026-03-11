package entity.view.cli;

import entity.view.base.HeroView;


public class HeroViewCLI extends HeroView {
	
	@Override
	public void showDropObject(String character, String item) {
		System.out.println(character + " drop " + item);
	}
	
	@Override
	public void showObjectNotFindInInventory(String item) {
		System.out.println(item + "not in the inventory");
	}

	@Override
	public void showLocation(String loc) {
    	System.out.println("Location: " + loc);
	}

	@Override
	public void showNoHealSpell() {
		System.out.println("You don't know how to use this spell yet.");
	}
	
	@Override
	public void receiveHealingPower() {
		System.out.println("You receive a healing power.");
	}
	
	@Override
	public void showDontKnowHealingSpell() {
		System.out.println("You don't know any healing spell.");
	}
	
	@Override
	public void useHealingPower() {
		System.out.println("You use your healing power !");
	}
}