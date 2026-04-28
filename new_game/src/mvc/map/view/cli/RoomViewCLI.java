package mvc.map.view.cli;

import common.language.Language;
import common.item.Item;
import common.map.Room;
import mvc.map.view.base.RoomView;

import java.util.stream.Collectors;

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
            System.out.println(Language.t("cli.exitsNone"));
        } else {
            String translatedExits = room.getExits().keySet().stream()
                    .map(Language::dir)
                    .collect(Collectors.joining(", "));
            System.out.println(Language.t("cli.exits") + " : " + translatedExits);
        }
    }

    private void displayItems(Room room) {
        if (room.getItems().isEmpty()) {
            System.out.println(Language.t("cli.itemsNone"));
            return;
        }

        System.out.println(Language.t("cli.items") + " :");
        for (Item item : room.getItems()) {
            System.out.println("- " + item.getName());
        }
    }

    @Override
    public void displayMove(String direction, String roomName) {
        System.out.println(Language.tf("cli.youGo", Language.dir(direction), roomName));
    }

    @Override
    public void displayNoExit(String direction) {
        System.out.println(Language.tf("cli.noExit", Language.dir(direction)));
    }

    @Override
    public void displayMessage(String message) {
        System.out.println(message);
    }
}
