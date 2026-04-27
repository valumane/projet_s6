package mvc.menu.view.base;

import java.util.List;
import mvc.mvc.View;

public abstract class MainMenuView implements View {

    public abstract void setOnNewGame(Runnable action);
    public abstract void setOnContinue(Runnable action);
    public abstract void setOnCreateLevel(Runnable action);
    public abstract void setOnSettings(Runnable action);
    public abstract void setOnQuit(Runnable action);

    public abstract void setScores(List<String> scores);
}