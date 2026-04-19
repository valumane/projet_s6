package mvc.map.view.cli;

import common.map.Room;
import mvc.map.view.base.RoomView;

public class RoomViewCLI extends RoomView {

    @Override
    public void displayRoom(Room room) {
        System.out.println();
        System.out.println("=== " + room.getName().toUpperCase() + " ===");
        displayExits(room);
        System.out.println();
    }

    private void displayDescription(Room room) {
    	System.out.println();
        System.out.println("--- " + room.getDescription() + " ---");
        System.out.println();
    }
    
    private void displayExits(Room room) {
        if (room.getExits().isEmpty()) {
            System.out.println("Exits : none");
        } else {
            System.out.print("Exits : ");
            System.out.println(String.join(", ", room.getExits().keySet()));
        }
    }
    
    @Override
    public void displayMove(String direction, String roomName) {
        System.out.println("You go " + direction + " and enter : " + roomName);
    }

    
    @Override
    public void displayNoExit(String direction) {
        System.out.println("There is no exit to the " + direction + ".");
    }
}
