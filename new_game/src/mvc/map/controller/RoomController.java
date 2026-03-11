package map.controller;

import java.util.Scanner;

import common.item.Item;
import entity.model.HeroModel;
import map.model.Room;
import map.view.cli.RoomView;

public class RoomController {

    private final HeroModel hero;
    private final RoomView view;
    private boolean running = true;

    public RoomController(HeroModel hero, RoomView view) {
        this.hero = hero;
        this.view = view;
    }

    public void run() {
        view.displayRoom(hero.getRoom());
    }

}   