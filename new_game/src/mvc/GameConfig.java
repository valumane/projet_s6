package mvc;

import javafx.scene.input.KeyCode;

public final class GameConfig {

    private static int windowWidth = 1200;
    private static int windowHeight = 800;

    private static int playerCount = 1;
    private static String controlScheme = "1 joueur";

    private static PlayerControls player1Controls = createDefaultPlayer1Controls();
    private static PlayerControls player2Controls = createDefaultPlayer2Controls();

    private GameConfig() {
    }

    public static PlayerControls createDefaultPlayer1Controls() {
        return new PlayerControls(
                KeyCode.Z,
                KeyCode.S,
                KeyCode.Q,
                KeyCode.D,
                KeyCode.E,
                new KeyCode[] {
                        KeyCode.DIGIT1,
                        KeyCode.DIGIT2,
                        KeyCode.DIGIT3,
                        KeyCode.DIGIT4,
                        KeyCode.DIGIT5,
                        KeyCode.DIGIT6,
                        KeyCode.DIGIT7,
                        KeyCode.DIGIT8,
                        KeyCode.DIGIT9
                }
        );
    }

    public static PlayerControls createDefaultPlayer2Controls() {
        return new PlayerControls(
                KeyCode.UP,
                KeyCode.DOWN,
                KeyCode.LEFT,
                KeyCode.RIGHT,
                KeyCode.ENTER,
                new KeyCode[] {
                        KeyCode.NUMPAD1,
                        KeyCode.NUMPAD2,
                        KeyCode.NUMPAD3,
                        KeyCode.NUMPAD4,
                        KeyCode.NUMPAD5,
                        KeyCode.NUMPAD6,
                        KeyCode.NUMPAD7,
                        KeyCode.NUMPAD8,
                        KeyCode.NUMPAD9
                }
        );
    }

    public static PlayerControls getPlayer1Controls() {
        return player1Controls;
    }

    public static PlayerControls getPlayer2Controls() {
        return player2Controls;
    }

    public static boolean setPlayer1Controls(PlayerControls p1) {
        if (p1 == null || p1.hasInternalConflict()) {
            return false;
        }

        player1Controls = p1;
        return true;
    }

    public static boolean setPlayerControls(PlayerControls p1, PlayerControls p2) {
        if (p1 == null || p2 == null) {
            return false;
        }

        if (PlayerControls.hasConflict(p1, p2)) {
            return false;
        }

        player1Controls = p1;
        player2Controls = p2;
        return true;
    }

    public static int getPlayerCount() {
        return playerCount;
    }

    public static void setPlayerCount(int value) {
        playerCount = value == 2 ? 2 : 1;
        controlScheme = playerCount == 2 ? "2 joueurs" : "1 joueur";
    }

    public static boolean isTwoPlayers() {
        return playerCount == 2;
    }

    public static String getControlHelpText() {
        if (isTwoPlayers()) {
            return "J1 : ZQSD / E / &é\"'(-è_ç | J2 : Flèches / Entrée / Pavé num. 1-9 | Attaque : Espace";
        }

        return "J1 : ZQSD / E / &é\"'(-è_ç | Attaque : Espace";
    }

    public static String getControlScheme() {
        return controlScheme;
    }

    public static void setControlScheme(String value) {
        if (value == null) {
            return;
        }

        controlScheme = value;

        if (value.contains("2")) {
            playerCount = 2;
        } else if (value.contains("1")) {
            playerCount = 1;
        }
    }

    public static KeyCode getMoveUpKey() {
        return player1Controls.getMoveUp();
    }

    public static KeyCode getMoveDownKey() {
        return player1Controls.getMoveDown();
    }

    public static KeyCode getMoveLeftKey() {
        return player1Controls.getMoveLeft();
    }

    public static KeyCode getMoveRightKey() {
        return player1Controls.getMoveRight();
    }

    public static KeyCode getInteractKey() {
        return player1Controls.getInteract();
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