package mvc.leveleditor.view.base;

import common.leveleditor.LevelRegistry.LevelSummary;
import mvc.mvc.View;
import java.nio.file.Path;
import java.util.List;
import java.util.function.Consumer;

public abstract class LevelListView implements View {
    public abstract void setSummaries(List<LevelSummary> summaries);
    public abstract void setOnNewLevel(Runnable action);
    public abstract void setOnEditLevel(Consumer<Path> action);
    public abstract void setOnDeleteLevel(Consumer<Path> action);
    public abstract void setOnBack(Runnable action);
    public abstract void showError(String message);
}