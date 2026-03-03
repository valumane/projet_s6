package item.view;

/*
 * Une classe abstraite dont héritent toutes les vues des Parchemins (=> Scroll).
 */
public abstract class ScrollView {

	/*
	 * Pour les parchemins (=> Scroll), on a besoin que d'afficher que l'utilisation.
	 */
    public abstract void displayOnUse(String name, String spellName);
    
}