package mvc.map.view.base;

import java.util.List;

import common.map.Room;
import mvc.map.ItemPlacement;
import mvc.map.RoomPlacement;
import mvc.mvc.View;

public abstract class RoomView implements View {

    @Override
    public void hide() {
    }

    @Override
    public void show() {
    }

    public abstract void displayRoom(Room room);
    public abstract void displayMove(String direction, String roomName);
    public abstract void displayNoExit(String direction);
    public abstract void displayMessage(String message);

    public void displayHeroPosition(double x, double y) {
    }

    public void displayPlacedItems(List<ItemPlacement> placedItems) {
    }

    public void displayVisitedRooms(List<RoomPlacement> visitedRooms, int currentGridX, int currentGridY) {
    }

    public void setOnMoveNorth(Runnable action) {
    }

    public void setOnMoveSouth(Runnable action) {
    }

    public void setOnMoveEast(Runnable action) {
    }

    public void setOnMoveWest(Runnable action) {
    }
}