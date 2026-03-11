package entity.view.cli;

import entity.view.base.HeroView;


public class HeroViewCLI extends HeroView {
	
	@Override
	public void showDropObject(String c, String i) {
		System.out.println(c + " drop " + i);
	}
	
	@Override
	public void showObjectNotFindInInventory(String i) {
		System.out.println(i + "not in the inventory");
	}


	@Override
	public void showLocation(String loc) {
    	System.out.println("Location: " + loc);
	}
}