package mvc.item.controller;

import java.util.List;

import common.language.Language;
import common.item.Chest;
import common.item.Item;
import common.map.Room;
import mvc.entity.model.HeroModel;
import mvc.item.model.ItemModel;
import mvc.item.view.cli.ItemViewCLI;
import mvc.item.view.gui.ItemViewLogGUI;
import mvc.map.MapLayout;
import mvc.map.model.RoomModel;
import mvc.map.view.base.RoomView;
import mvc.mvc.Controller;

public class RoomItemInteractionController extends Controller {

    private static final double INTERACT_DISTANCE = 35.0;

    private final RoomModel roomModel;
    private final HeroModel heroModel;
    private final RoomView viewCLI;
    private final RoomView viewGUI;
    private final Runnable refreshRoomViews;

    public RoomItemInteractionController(
            RoomModel roomModel,
            HeroModel heroModel,
            RoomView viewCLI,
            RoomView viewGUI,
            Runnable refreshRoomViews
    ) {
        super(roomModel, viewCLI, viewGUI);

        this.roomModel = roomModel;
        this.heroModel = heroModel;
        this.viewCLI = viewCLI;
        this.viewGUI = viewGUI;
        this.refreshRoomViews = refreshRoomViews;
    }

    public void interactNearby() {
        Room currentRoom = roomModel.getRoom();

        if (currentRoom == null) {
            return;
        }

        List<Item> items = currentRoom.getItems();

        if (items.isEmpty()) {
            viewCLI.displayMessage(Language.t("item.nothingToInteract"));
            viewGUI.displayMessage(Language.t("item.nothingToInteract"));
            return;
        }

        NearestItem nearest = findNearestItem(items);

        if (nearest == null) {
            viewCLI.displayMessage(Language.t("item.nothingToInteract"));
            viewGUI.displayMessage(Language.t("item.nothingToInteract"));
            return;
        }

        if (nearest.distance > INTERACT_DISTANCE) {
            viewCLI.displayMessage(Language.t("item.nothingNearby"));
            viewGUI.displayMessage(Language.t("item.nothingNearby"));
            return;
        }

        interactWithItem(nearest.item);
    }

    public void interactCli() {
        Room currentRoom = roomModel.getRoom();

        if (currentRoom == null) {
            return;
        }

        Item candidate = null;

        for (Item item : currentRoom.getItems()) {
            if (item.canBeTaken()) {
                candidate = item;
                break;
            }
        }

        if (candidate == null) {
            for (Item item : currentRoom.getItems()) {
                if (item instanceof Chest) {
                    candidate = item;
                    break;
                }
            }
        }

        if (candidate == null) {
            for (Item item : currentRoom.getItems()) {
                if (item.canBeUsed()) {
                    candidate = item;
                    break;
                }
            }
        }

        if (candidate == null) {
            viewCLI.displayMessage(Language.t("item.nothingToInteract"));
            return;
        }

        interactWithItem(candidate);
    }

    private NearestItem findNearestItem(List<Item> items) {
        Item nearestItem = null;
        double bestDistance = Double.MAX_VALUE;

        for (int i = 0; i < items.size(); i++) {
            double itemX = MapLayout.getItemX(i);
            double itemY = MapLayout.getItemY(i);

            double dx = heroModel.getX() - itemX;
            double dy = heroModel.getY() - itemY;

            double distance = Math.hypot(dx, dy);

            if (distance < bestDistance) {
                bestDistance = distance;
                nearestItem = items.get(i);
            }
        }

        if (nearestItem == null) {
            return null;
        }

        return new NearestItem(nearestItem, bestDistance);
    }

    private void interactWithItem(Item item) {
        ItemController itemController = new ItemController(
                new ItemModel(item, heroModel),
                new ItemViewCLI("Item"),
                new ItemViewLogGUI(viewGUI::displayMessage),
                heroModel
        );

        itemController.onInteractItem();

        if (refreshRoomViews != null) {
            refreshRoomViews.run();
        }
    }

    private static final class NearestItem {
        private final Item item;
        private final double distance;

        private NearestItem(Item item, double distance) {
            this.item = item;
            this.distance = distance;
        }
    }
}