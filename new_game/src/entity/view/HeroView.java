package src.entity.view;

public abstract class HeroView {
	
	public abstract void showDropObject(String character, String item);
	
	public abstract void showObjectNotFindInInventory(String item);
	
	public abstract void showNoHealSpell();
	
	public abstract void receiveHealingPower();
	
	public abstract void showDontKnowHealingSpell();
	
	public abstract void useHealingPower();
}