package mvc.map.view.base;

import common.map.Room;
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
}