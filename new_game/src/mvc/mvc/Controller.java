package mvc;

import java.util.List;
import java.util.ArrayList;

/// Generic Controller for the triple {Model-View-Controller}.
/// Every Controller can create some sub-controllers.
///
/// @author X. Skapin
/// @version 1.0.0
public abstract class Controller {
		/// Sub-controllers
		protected final List<Controller> subControllers = new ArrayList<>();
	
		/// Related [Model]
		protected final Model model;

		/// Related [View]
		protected final View viewGUI;
		protected final View viewCLI;

		/// Custom constructor
		///
		/// @param p_model Related [Model]. Should not be null.
		/// @param p_view Related [View]. Should not be null.
		protected Controller(Model p_model, View p_viewCLI, View p_viewGUI) {
				this.model = p_model;
				this.viewGUI = p_viewCLI;
				this.viewCLI = p_viewGUI;
		}

		/// {@return the related [Model]}
		public Model getModel() {
				return this.model;
		}

		/// {@return the related [View]}
		public View getViewCLI() {
				return this.viewCLI;
		}
		
		public View getViewGUI() {
			return this.viewGUI;
	}
}
