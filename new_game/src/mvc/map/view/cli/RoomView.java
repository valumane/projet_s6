package map.view.cli;

import common.map.Room;
/**
 * VIEW - Affiche les informations d'une salle.
 * C'est le SEUL endroit où System.out.println est utilisé pour la Room.
 */
public class RoomView {

    /**
     * Affiche la description complète d'une salle :
     * description, sorties, items, personnages présents.
     */
    public void displayRoom(Room room) {
        System.out.println();
        System.out.println("=== " + room.getName().toUpperCase() + " ===");

        displayExits(room);
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


    /** Message de déplacement du héros */
    public void displayMove(String direction, String roomName) {
        System.out.println("You go " + direction + " and enter : " + roomName);
    }

    /** Message d'erreur si la sortie n'existe pas */
    public void displayNoExit(String direction) {
        System.out.println("There is no exit to the " + direction + ".");
    }

}