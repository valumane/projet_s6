package entity.view.base;

import mvc.View;

public abstract class HeroView implements View {

    @Override public void hide() {}
    @Override public void show() {}

    public abstract void showDropObject(String character, String item);
    public abstract void showObjectNotFindInInventory(String item);
    public abstract void showLocation(String loc);
    public abstract void showNoHealSpell();
	public abstract void receiveHealingPower();
	public abstract void showDontKnowHealingSpell();
	public abstract void useHealingPower();
}