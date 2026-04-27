package mvc.map.view.base;

import java.util.List;

import common.map.Room;
import mvc.map.model.RoomPlacement;
import mvc.mvc.View;
import mvc.entity.model.EnemySnapshot;
import mvc.entity.model.ProjectileSnapshot;

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

    public void displayHeroFacing(double dx, double dy) {
    }

    public void displayEnemies(List<EnemySnapshot> enemies) {
    }

    public void displayProjectiles(List<ProjectileSnapshot> projectiles) {
    }

    public void displayHeroAttackFlash() {
    }

    public void displaySecondHeroPosition(double x, double y) {
    }

    public void displaySecondHeroFacing(double dx, double dy) {
    }

    public void displaySecondHeroAttackFlash() {
    }

}