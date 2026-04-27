package mvc;

import javafx.scene.input.KeyCode;

public final class GameConfig {

    private static String controlScheme = "ZQSD + E";
    private static int windowWidth = 1200;
    private static int windowHeight = 800;

    private GameConfig() {
    }

    public static void setControlScheme(String value) {
        if (value == null) {
            return;
        }

        controlScheme = value;
    }

    public static String getControlScheme() {
        return controlScheme;
    }

    public static KeyCode getMoveUpKey() {
        return controlScheme.startsWith("WASD") ? KeyCode.W : KeyCode.Z;
    }

    public static KeyCode getMoveLeftKey() {
        return controlScheme.startsWith("WASD") ? KeyCode.A : KeyCode.Q;
    }

    public static KeyCode getMoveDownKey() {
        return controlScheme.startsWith("WASD") ? KeyCode.S : KeyCode.S;
    }

    public static KeyCode getMoveRightKey() {
        return controlScheme.startsWith("WASD") ? KeyCode.D : KeyCode.D;
    }

    public static KeyCode getInteractKey() {
        return KeyCode.E;
    }

    public static String getControlHelpText() {
        return controlScheme.startsWith("WASD")
                ? "Déplacements : WASD | Interagir : E"
                : "Déplacements : ZQSD | Interagir : E";
    }

    public static void setResolution(String value) {
        if ("1600x900".equals(value)) {
            windowWidth = 1600;
            windowHeight = 900;
            return;
        }

        windowWidth = 1200;
        windowHeight = 800;
    }

    public static String getResolution() {
        return windowWidth + "x" + windowHeight;
    }

    public static int getWindowWidth() {
        return windowWidth;
    }

    public static int getWindowHeight() {
        return windowHeight;
    }
}