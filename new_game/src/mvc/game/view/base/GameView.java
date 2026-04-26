package mvc.game.view.base;

import mvc.mvc.View;

public abstract class GameView implements View {

    public abstract void setOnMoveNorth(Runnable action);
    public abstract void setOnMoveSouth(Runnable action);
    public abstract void setOnMoveEast(Runnable action);
    public abstract void setOnMoveWest(Runnable action);

    public abstract void setOnInteract(Runnable action);

    public void setOnShowLogs(Runnable action) {
    }
}