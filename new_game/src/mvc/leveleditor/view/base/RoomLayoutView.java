package mvc.leveleditor.view.base;

import common.leveleditor.LevelData;
import common.leveleditor.RoomEditorData;
import mvc.mvc.View;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public abstract class RoomLayoutView implements View {
    public abstract void renderGrid(LevelData level, int selectedIndex);
    public abstract void showRoomPanel(RoomEditorData room, int index);
    public abstract void clearRoomPanel();
    public abstract void setOnSelectRoom(Consumer<Integer> action);
    public abstract void setOnAddRoom(BiConsumer<Integer, String> action);
    public abstract void setOnDeleteRoom(Consumer<Integer> action);
    public abstract void setOnRenameRoom(BiConsumer<Integer, String> action);
    public abstract void setOnSetDescription(BiConsumer<Integer, String> action);
    public abstract void setOnSetStart(Consumer<Integer> action);
    public abstract void setOnSetBoss(Consumer<Integer> action);
    public abstract void setOnAddItem(BiConsumer<Integer, String> action);
    public abstract void setOnRemoveItem(BiConsumer<Integer, String> action);
    public abstract void setOnAddEnemy(BiConsumer<Integer, String> action);
    public abstract void setOnRemoveEnemy(BiConsumer<Integer, String> action);
    public abstract void setOnSave(Runnable action);
    public abstract void setOnCancel(Runnable action);
    public abstract void showError(String message);
}