package application;

import javafx.stage.Stage;
import mvc.leveleditor.controller.LevelEditorController;
import mvc.leveleditor.model.LevelEditorModel;
import mvc.leveleditor.view.gui.LevelListViewGUI;

public final class LevelEditorLauncher {

    private LevelEditorLauncher() {}

    public static void show(Stage stage) {
        LevelEditorModel model = new LevelEditorModel();
        LevelListViewGUI listView = new LevelListViewGUI(stage);
        new LevelEditorController(model, listView, stage);
    }
}