// src/mvc/map/MapLayout.java
package mvc.map;

public final class MapLayout {

    private MapLayout() {
    }

    public static final double MAP_WIDTH = 1000;
    public static final double MAP_HEIGHT = 620;

    public static final double ROOM_X = 20;
    public static final double ROOM_Y = 20;
    public static final double ROOM_W = 900;
    public static final double ROOM_H = 520;

    public static final double HERO_RADIUS = 20;
    public static final double ITEM_RADIUS = 20;

    public static final int ITEM_COLUMNS = 3;

    public static double getRoomCenterX() {
        return ROOM_X + ROOM_W / 2.0;
    }

    public static double getRoomCenterY() {
        return ROOM_Y + ROOM_H / 2.0;
    }

    public static double getItemX(int index) {
        int col = index % ITEM_COLUMNS;

        double startX = ROOM_X + ROOM_W * 0.18;
        double gapX = ROOM_W * 0.22;

        return startX + col * gapX;
    }

    public static double getItemY(int index) {
        int row = index / ITEM_COLUMNS;

        double startY = ROOM_Y + ROOM_H * 0.28;
        double gapY = ROOM_H * 0.14;

        return startY + row * gapY;
    }

}