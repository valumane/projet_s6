package mvc.leveleditor.controller;

import application.MenuLauncher;
import common.leveleditor.LevelData;
import common.leveleditor.LevelRegistry.LevelSummary;
import common.leveleditor.RoomEditorData;
import javafx.stage.Stage;
import mvc.leveleditor.model.LevelEditorModel;
import mvc.leveleditor.view.base.LevelEditorView;
import mvc.leveleditor.view.base.LevelListView;
import mvc.leveleditor.view.base.RoomLayoutView;
import mvc.leveleditor.view.gui.LevelEditorViewGUI;
import mvc.leveleditor.view.gui.RoomLayoutViewGUI;
import mvc.mvc.Controller;

import java.nio.file.Path;
import java.util.List;

public class LevelEditorController extends Controller {

    private final LevelEditorModel model;
    private final LevelListView listView;
    private final Stage stage;

    public LevelEditorController(LevelEditorModel model, LevelListView listView, Stage stage) {
        super(model, null, listView);
        this.model = model;
        this.listView = listView;
        this.stage = stage;

        refreshList();

        listView.setOnNewLevel(() -> {
            LevelData newLevel = new LevelData();
            // room de depart par defaut
            RoomEditorData startRoom = new RoomEditorData("Entrée", "Le début du level.", 0, 0);
            startRoom.setStartRoom(true);
            newLevel.addRoom(startRoom);
            openEditor(newLevel, null);
        });

        listView.setOnEditLevel(path -> {
            LevelData level = model.loadLevel(path);
            openEditor(level, path);
        });

        listView.setOnDeleteLevel(path -> {
            model.deleteLevel(path);
            refreshList();
        });

        listView.setOnBack(() -> {
            listView.hide();
            MenuLauncher.showMainMenu(stage);
        });

        listView.show();
    }

    private void refreshList() {
        List<LevelSummary> summaries = model.loadSummaries();
        listView.setSummaries(summaries);
    }

    private void openEditor(LevelData level, Path existingPath) {
        LevelEditorViewGUI editorView = new LevelEditorViewGUI(stage);
        editorView.setLevelName(level.getLevelName());
        editorView.setLevelDescription(level.getLevelDescription());
        editorView.renderMinimap(level);

        editorView.setOnEditLayout(() -> openRoomLayout(level, editorView));

        editorView.setOnSave(() -> {
            level.setLevelName(editorView.getLevelName());
            level.setLevelDescription(editorView.getLevelDescription());
            try {
                model.saveLevel(level);
                editorView.showSavedFeedback();
            } catch (Exception e) {
                editorView.showError(e.getMessage());
            }
        });

        editorView.setOnBack(() -> {
            editorView.hide();
            refreshList();
            listView.show();
        });

        editorView.show();
    }

    private void openRoomLayout(LevelData level, LevelEditorViewGUI parentView) {
        RoomLayoutViewGUI layoutView = new RoomLayoutViewGUI(stage);
        new RoomLayoutController(level, layoutView, model, () -> {
            parentView.renderMinimap(level);
            layoutView.hide();
            parentView.show();
        });
        layoutView.show();
    }
}