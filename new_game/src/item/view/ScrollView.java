package item.view;

import item.model.ScrollModel;

/*
 * Une classe abstraite dont héritent toutes les vues des Scroll.
 */
public abstract class ScrollView {

	/*
	 * Pour les Scroll, on a besoin que d'afficher que l'utilisation.
	 */
    public abstract void displayOnUse(ScrollModel model);
    
}