package application;

import javafx.stage.Stage;
import mvc.menu.controller.MainMenuController;
import mvc.menu.model.MainMenuModel;
import mvc.menu.view.gui.MainMenuViewGUI;

public final class MenuLauncher {

    private MenuLauncher() {
    }

    public static void showMainMenu(Stage stage) {
        MainMenuModel model = new MainMenuModel();
        MainMenuViewGUI view = new MainMenuViewGUI(stage);

        new MainMenuController(model, view, stage);

        view.show();
    }
}