package entity.view;

import mvc.View;

public abstract class HeroView implements View {

    @Override public void hide() {}
    @Override public void show() {}

    public abstract void showDropObject(String c, String i);
    public abstract void showObjectNotFindInInventory(String i);
    public abstract void showNoHealSpell();
    public abstract void receiveHealingPower();
    public abstract void showDontKnowHealingSpell();
    public abstract void useHealingPower();
}