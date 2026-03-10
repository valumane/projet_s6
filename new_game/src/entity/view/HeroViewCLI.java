package entity.view;

import src.mvc.View;

public class HeroViewCLI extends HeroView implements View {
	
	@Override
	public void showDropObject(String c, String i) {
		System.out.println(c + " drop " + i);
	}
	
	@Override
	public void showObjectNotFindInInventory(String i) {
		System.out.println(i + "not in the inventory");
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