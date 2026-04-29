package mvc.leveleditor.controller;

import common.leveleditor.LevelData;
import common.leveleditor.RoomEditorData;
import mvc.leveleditor.model.LevelEditorModel;
import mvc.leveleditor.view.base.RoomLayoutView;
import mvc.mvc.Controller;

public class RoomLayoutController extends Controller {

    private final LevelData level;
    private final RoomLayoutView view;
    private final Runnable onDone;
    private final LevelEditorModel model;
    private int selectedIndex = -1;

    public RoomLayoutController(LevelData level, RoomLayoutView view,
                                 LevelEditorModel model, Runnable onDone) {
        super(null, null, view);
        this.level = level;
        this.view = view;
        this.model = model;
        this.onDone = onDone;

        if (!level.getRooms().isEmpty()) {
            selectedIndex = 0;
        }

        view.setOnSelectRoom(this::selectRoom);
        view.setOnAddRoom(this::addRoom);
        view.setOnDeleteRoom(this::deleteRoom);
        view.setOnRenameRoom(this::renameRoom);
        view.setOnSetDescription(this::setDescription);
        view.setOnSetStart(this::setStart);
        view.setOnSetBoss(this::setBoss);
        view.setOnAddItem((idx, desc) -> {
            level.getRooms().get(idx).addItemDescriptor(desc);
            refresh();
            if (idx == selectedIndex) view.showRoomPanel(level.getRooms().get(idx), idx);
        });
        view.setOnRemoveItem((idx, desc) -> {
            level.getRooms().get(idx).removeItemDescriptor(desc);
            refresh();
            if (idx == selectedIndex) view.showRoomPanel(level.getRooms().get(idx), idx);
        });
        view.setOnAddEnemy((idx, desc) -> {
            level.getRooms().get(idx).addEnemyDescriptor(desc);
            refresh();
            if (idx == selectedIndex) view.showRoomPanel(level.getRooms().get(idx), idx);
        });
        view.setOnRemoveEnemy((idx, desc) -> {
            level.getRooms().get(idx).removeEnemyDescriptor(desc);
            refresh();
            if (idx == selectedIndex) view.showRoomPanel(level.getRooms().get(idx), idx);
        });
        view.setOnSave(this::save);
        view.setOnCancel(() -> { view.hide(); onDone.run(); });

        refresh();
        if (selectedIndex >= 0) view.showRoomPanel(level.getRooms().get(selectedIndex), selectedIndex);
        else view.clearRoomPanel();
    }

    private void selectRoom(int index) {
        selectedIndex = index;
        refresh();
        view.showRoomPanel(level.getRooms().get(index), index);
    }

    private void addRoom(int fromIndex, String direction) {
        try {
            int newIdx = model.addRoomInDirection(level, fromIndex, direction);
            selectedIndex = newIdx;
            refresh();
            view.showRoomPanel(level.getRooms().get(newIdx), newIdx);
        } catch (Exception e) {
            view.showError(e.getMessage());
        }
    }

    private void deleteRoom(int index) {
        level.removeRoom(index);
        selectedIndex = level.getRooms().isEmpty() ? -1 : Math.min(index, level.getRooms().size() - 1);
        refresh();
        if (selectedIndex >= 0) view.showRoomPanel(level.getRooms().get(selectedIndex), selectedIndex);
        else view.clearRoomPanel();
    }

    private void renameRoom(int index, String newName) {
        level.getRooms().get(index).setName(newName.trim());
        refresh();
        view.showRoomPanel(level.getRooms().get(index), index);
    }

    private void setDescription(int index, String desc) {
        level.getRooms().get(index).setDescription(desc.trim());
    }

    private void setStart(int index) {
        for (RoomEditorData r : level.getRooms()) r.setStartRoom(false);
        level.getRooms().get(index).setStartRoom(true);
        level.setStartRoomIndex(index);
        refresh();
        view.showRoomPanel(level.getRooms().get(index), index);
    }

    private void setBoss(int index) {
        for (RoomEditorData r : level.getRooms()) r.setBossRoom(false);
        level.getRooms().get(index).setBossRoom(true);
        refresh();
        view.showRoomPanel(level.getRooms().get(index), index);
    }

    private void save() {
        try {
            model.saveLevel(level);
            view.hide();
            onDone.run();
        } catch (Exception e) {
            view.showError("Erreur de sauvegarde : " + e.getMessage());
        }
    }

    private void refresh() {
        view.renderGrid(level, selectedIndex);
    }
}