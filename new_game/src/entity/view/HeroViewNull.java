package entity.view;

public class HeroViewNull extends HeroView {
    @Override public void hide() {}
    @Override public void show() {}

    @Override public void showDropObject(String c, String i) {}
    @Override public void showObjectNotFindInInventory(String i) {}
    @Override public void showNoHealSpell() {}
    @Override public void receiveHealingPower() {}
    @Override public void showDontKnowHealingSpell() {}
    @Override public void useHealingPower() {}
}