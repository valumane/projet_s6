package mvc.game.view.base;

import java.util.List;
import java.util.function.IntConsumer;
import java.util.function.LongConsumer;

import mvc.mvc.View;

public abstract class GameView implements View {

    public abstract void setOnMoveNorth(Runnable action);

    public abstract void setOnMoveSouth(Runnable action);

    public abstract void setOnMoveEast(Runnable action);

    public abstract void setOnMoveWest(Runnable action);

    public abstract void setOnInteract(Runnable action);

    public void setOnPlayer2MoveNorth(Runnable action) {
    }

    public void setOnPlayer2MoveSouth(Runnable action) {
    }

    public void setOnPlayer2MoveEast(Runnable action) {
    }

    public void setOnPlayer2MoveWest(Runnable action) {
    }

    public void setOnPlayer2Interact(Runnable action) {
    }

    public void setOnShowLogs(Runnable action) {
    }

    public void setOnToggleInventory(Runnable action) {
    }

    public void displayInventory(List<String> items) {
    }

    public void displayPlayer2Inventory(List<String> items) {
    }

    public void toggleInventoryOverlay() {
    }

    public void setOnResetGame(Runnable action) {
    }

    public void setOnSaveGame(Runnable action) {
    }

    public void setOnQuitToMenu(Runnable action) {
    }

    public void setOnQuitToDesktop(Runnable action) {
    }

    public void setOnSettings(Runnable action) {
    }

    public void setOnGameTick(LongConsumer action) {
    }

    public void setOnAttack(Runnable action) {
    }

    public void displayGameOver() {
    }

    public void setOnUseInventorySlot(IntConsumer action) {
    }

    public void setOnPlayer2UseInventorySlot(IntConsumer action) {
    }

    public void displayInfo(String message) {
    }

    public void displayPlayer2Info(String message) {
    }
}
