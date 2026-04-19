package mvc.map.controller;

import java.util.Scanner;

import common.item.Item;
import common.map.Room;
import mvc.entity.model.HeroModel;
import mvc.map.model.RoomModel;
import mvc.map.view.base.RoomView;
import mvc.map.view.cli.RoomViewCLI;
import mvc.mvc.Controller;


public class RoomController extends Controller {

    private final RoomModel roomModel;
    private final RoomView viewCLI;
    // viewGUI a ajouter

    public RoomController(RoomModel roomModel, RoomView viewGUI, RoomView viewCLI) {
        super(roomModel, viewGUI, viewCLI);
        this.roomModel = roomModel;
        this.viewCLI = viewCLI;
    }
    
    public void onEnterRoom() {
        viewCLI.displayRoom(roomModel.getRoom());
    }
    
    public void heroMove(String direction) {
        Room target = roomModel.getRoom().getExit(direction) != null
                ? roomModel.getRoom().getExit(direction).getTarget()
                : null;

        if (target == null) {
            viewCLI.displayNoExit(direction);
        } else {
            viewCLI.displayMove(direction, target.getName());
        }
    }

    /*public void run() {
        view.displayRoom(hero.getRoom());
    }*/

}   