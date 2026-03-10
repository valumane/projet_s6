package src.entity.controller;

import src.mvc.Controller;
import src.entity.model.HeroModel;
import src.entity.view.HeroView;

public class HeroController extends Controller {
	
	private final HeroModel hero;
	private final HeroView view1;
	private final HeroView view2;
	
	public HeroController(HeroModel hero, HeroView view1, HeroView view2) {
		super();
		this.hero = hero;
		this.view1 = view1;
		this.view2 = view2;
	}
	
	// Déposer un item
	public void onDropItem(Item itemName) {
		
		Item item = hero.drop(itemName.getName());
		
		// Si l'item n'existe pas : message qui en informe l'utilisateur
		// Sinon : le héro a perdu l'item, et on l'ajoute dans la salle dans laquelle il se trouve
		if (item == null) {
			view1.showObjectNotFindInInventory(item.getName());
			view2.showObjectNotFindInInventory(item.getName());
		} else {
			view1.showDropObject(hero.getName(), item.getName());
			view2.showDropObject(hero.getName(), item.getName());
			hero.getLocation().addItem(item);	
		}
	}
	
	public void onLearnSpell(Spell s) {
		boolean happendSpell = hero.learnSpell(s);
		
		// Si le booléen est vrai -> le héro a appris la compétence
		// Si le booléen est faux -> il ne l'a pas appris
		if (happendSpell) {
			view1.receiveHealingPower();
			view2.receiveHealingPower();
		} else {
			view1.showNoHealSpell();
			view2.showNoHealSpell();
		}
	}
	
	public void onUseHealSpell() {
		boolean useHealSpell = hero.useHealSpell();
		
		// Si le booléen est vrai -> le héro a utilisé la compétence
		// Si le booléen est faux -> il ne la connait pas
		if (useHealSpell) {
			view1.useHealingPower();
			view2.useHealingPower();
		} else {
			view1.showDontKnowHealingSpell();
			view2.showDontKnowHealingSpell();
		}
	}
}