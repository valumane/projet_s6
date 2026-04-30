package mvc.leveleditor.view.base;

import common.leveleditor.LevelData;
import mvc.mvc.View;

public abstract class LevelEditorView implements View {
    public abstract void setLevelName(String name);
    public abstract void setLevelDescription(String description);
    public abstract String getLevelName();
    public abstract String getLevelDescription();
    public abstract void renderMinimap(LevelData level);
    public abstract void setOnEditLayout(Runnable action);
    public abstract void setOnSave(Runnable action);
    public abstract void setOnBack(Runnable action);
    public abstract void showSavedFeedback();
    public abstract void showError(String message);
}