package mvc.map.view.cli;

import common.item.Item;
import common.map.Room;
import mvc.map.view.base.RoomView;

public class RoomViewCLI extends RoomView {

    @Override
    public void displayRoom(Room room) {
        System.out.println();
        System.out.println("=== " + room.getName().toUpperCase() + " ===");
        displayDescription(room);
        displayExits(room);
        displayItems(room);
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

    private void displayItems(Room room) {
        if (room.getItems().isEmpty()) {
            System.out.println("Items : none");
            return;
        }

        System.out.println("Items :");
        for (Item item : room.getItems()) {
            System.out.println("- " + item.getName());
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

    @Override
    public void displayMessage(String message) {
        System.out.println(message);
    }
}