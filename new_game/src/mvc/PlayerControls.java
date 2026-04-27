package mvc;

import java.util.HashSet;
import java.util.Set;

import javafx.scene.input.KeyCode;

public class PlayerControls {

    private final KeyCode moveUp;
    private final KeyCode moveDown;
    private final KeyCode moveLeft;
    private final KeyCode moveRight;
    private final KeyCode interact;
    private final KeyCode[] inventoryKeys;

    public PlayerControls(
            KeyCode moveUp,
            KeyCode moveDown,
            KeyCode moveLeft,
            KeyCode moveRight,
            KeyCode interact,
            KeyCode[] inventoryKeys
    ) {
        this.moveUp = moveUp;
        this.moveDown = moveDown;
        this.moveLeft = moveLeft;
        this.moveRight = moveRight;
        this.interact = interact;
        this.inventoryKeys = inventoryKeys.clone();
    }

    public KeyCode getMoveUp() {
        return moveUp;
    }

    public KeyCode getMoveDown() {
        return moveDown;
    }

    public KeyCode getMoveLeft() {
        return moveLeft;
    }

    public KeyCode getMoveRight() {
        return moveRight;
    }

    public KeyCode getInteract() {
        return interact;
    }

    public KeyCode[] getInventoryKeys() {
        return inventoryKeys.clone();
    }

    public int getInventorySlot(KeyCode code) {
        for (int i = 0; i < inventoryKeys.length; i++) {
            if (inventoryKeys[i] == code) {
                return i;
            }
        }

        return -1;
    }

    public Set<KeyCode> getAllKeys() {
        Set<KeyCode> keys = new HashSet<>();

        keys.add(moveUp);
        keys.add(moveDown);
        keys.add(moveLeft);
        keys.add(moveRight);
        keys.add(interact);

        for (KeyCode key : inventoryKeys) {
            keys.add(key);
        }

        return keys;
    }

    public boolean hasInternalConflict() {
        int expectedSize = 5 + inventoryKeys.length;
        return getAllKeys().size() != expectedSize;
    }

    public static boolean hasConflict(PlayerControls p1, PlayerControls p2) {
        if (p1.hasInternalConflict() || p2.hasInternalConflict()) {
            return true;
        }

        Set<KeyCode> keys = new HashSet<>(p1.getAllKeys());

        for (KeyCode key : p2.getAllKeys()) {
            if (keys.contains(key)) {
                return true;
            }
        }

        return false;
    }
}
