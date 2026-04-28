package mvc.menu.view.gui;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import mvc.GameConfig;
import mvc.PlayerControls;

public final class GameConfigurationDialog {

    public static final class Result {
        private final int playerCount;
        private final String resolution;
        private final PlayerControls player1Controls;
        private final PlayerControls player2Controls;

        public Result(int playerCount, String resolution, PlayerControls player1Controls, PlayerControls player2Controls) {
            this.playerCount = playerCount;
            this.resolution = resolution;
            this.player1Controls = player1Controls;
            this.player2Controls = player2Controls;
        }

        public int getPlayerCount() {
            return playerCount;
        }

        public String getResolution() {
            return resolution;
        }

        public PlayerControls getPlayer1Controls() {
            return player1Controls;
        }

        public PlayerControls getPlayer2Controls() {
            return player2Controls;
        }
    }

    private GameConfigurationDialog() {
    }

    public static void show(Stage owner, String titleText, boolean allowPlayerCountChange, Consumer<Result> onApply) {
        Stage dialog = new Stage();
        dialog.initOwner(owner);
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle(titleText);

        PlayerControls initialP1 = GameConfig.getPlayer1Controls();
        PlayerControls initialP2 = GameConfig.getPlayer2Controls();

        ToggleGroup playerCountGroup = new ToggleGroup();
        RadioButton onePlayerRadio = new RadioButton("1 joueur");
        RadioButton twoPlayersRadio = new RadioButton("2 joueurs");
        onePlayerRadio.setToggleGroup(playerCountGroup);
        twoPlayersRadio.setToggleGroup(playerCountGroup);

        if (GameConfig.getPlayerCount() == 2) {
            twoPlayersRadio.setSelected(true);
        } else {
            onePlayerRadio.setSelected(true);
        }

        onePlayerRadio.setDisable(!allowPlayerCountChange);
        twoPlayersRadio.setDisable(!allowPlayerCountChange);

        HBox playerChoiceBox = new HBox(18, onePlayerRadio, twoPlayersRadio);
        playerChoiceBox.setAlignment(Pos.CENTER_LEFT);

        Label resolutionLabel = new Label("Résolution");
        ComboBox<String> resolutionCombo = new ComboBox<>();
        resolutionCombo.getItems().addAll("1200x800", "1600x900");
        resolutionCombo.setValue(GameConfig.getResolution());

        Label hintLabel = new Label("Clique sur une touche pour la modifier, puis appuie sur la nouvelle touche.");
        hintLabel.setWrapText(true);

        Label errorLabel = new Label("");
        errorLabel.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");

        KeyCell[] p1Inventory = createInventoryCells(initialP1);
        KeyCell[] p2Inventory = createInventoryCells(initialP2);

        KeyCell p1Forward = new KeyCell(initialP1.getMoveUp());
        KeyCell p1Backward = new KeyCell(initialP1.getMoveDown());
        KeyCell p1Left = new KeyCell(initialP1.getMoveLeft());
        KeyCell p1Right = new KeyCell(initialP1.getMoveRight());
        KeyCell p1Interact = new KeyCell(initialP1.getInteract());

        KeyCell p2Forward = new KeyCell(initialP2.getMoveUp());
        KeyCell p2Backward = new KeyCell(initialP2.getMoveDown());
        KeyCell p2Left = new KeyCell(initialP2.getMoveLeft());
        KeyCell p2Right = new KeyCell(initialP2.getMoveRight());
        KeyCell p2Interact = new KeyCell(initialP2.getInteract());

        List<Region> player2Rows = new ArrayList<>();

        GridPane inventoryGrid = buildInventoryGrid(p1Inventory, p2Inventory, player2Rows);
        GridPane movementGrid = buildMovementGrid(
                p1Forward, p1Backward, p1Right, p1Left,
                p2Forward, p2Backward, p2Right, p2Left,
                player2Rows);
        GridPane interactGrid = buildInteractGrid(p1Interact, p2Interact, player2Rows);

        CaptureState captureState = new CaptureState();

        registerCapture(captureState, p1Inventory);
        registerCapture(captureState, p2Inventory);
        registerCapture(captureState, p1Forward, p1Backward, p1Left, p1Right, p1Interact);
        registerCapture(captureState, p2Forward, p2Backward, p2Left, p2Right, p2Interact);

        Runnable refreshPlayer2Visibility = () -> {
            boolean twoPlayers = twoPlayersRadio.isSelected();
            for (Region node : player2Rows) {
                node.setVisible(twoPlayers);
                node.setManaged(twoPlayers);
            }
        };
        playerCountGroup.selectedToggleProperty().addListener((obs, oldValue, newValue) -> refreshPlayer2Visibility.run());
        refreshPlayer2Visibility.run();

        Button resetDefaultButton = new Button("Touches par défaut");
        resetDefaultButton.setOnAction(e -> {
            PlayerControls defaultP1 = GameConfig.createDefaultPlayer1Controls();
            PlayerControls defaultP2 = GameConfig.createDefaultPlayer2Controls();

            applyControlsToCells(defaultP1, p1Forward, p1Backward, p1Left, p1Right, p1Interact, p1Inventory);
            applyControlsToCells(defaultP2, p2Forward, p2Backward, p2Left, p2Right, p2Interact, p2Inventory);

            errorLabel.setText("");
            hintLabel.setText("Touches par défaut rétablies.");
        });

        Button applyButton = new Button("Appliquer");
        applyButton.setDefaultButton(true);
        applyButton.setOnAction(e -> {
            int playerCount = twoPlayersRadio.isSelected() ? 2 : 1;

            PlayerControls p1 = buildControls(p1Forward, p1Backward, p1Left, p1Right, p1Interact, p1Inventory);
            PlayerControls p2 = buildControls(p2Forward, p2Backward, p2Left, p2Right, p2Interact, p2Inventory);

            String error = validateControls(playerCount, p1, p2);
            if (error != null) {
                errorLabel.setText(error);
                return;
            }

            if (onApply != null) {
                onApply.accept(new Result(playerCount, resolutionCombo.getValue(), p1, p2));
            }

            dialog.close();
        });

        Button cancelButton = new Button("Annuler");
        cancelButton.setCancelButton(true);
        cancelButton.setOnAction(e -> dialog.close());

        HBox buttonsBox = new HBox(12, resetDefaultButton, applyButton, cancelButton);
        buttonsBox.setAlignment(Pos.CENTER_RIGHT);

        Label title = new Label(titleText);
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        VBox root = new VBox(14,
                title,
                playerChoiceBox,
                resolutionLabel,
                resolutionCombo,
                hintLabel,
                sectionTitle("Inventaire"),
                inventoryGrid,
                sectionTitle("Mouvement"),
                movementGrid,
                sectionTitle("Interagir"),
                interactGrid,
                errorLabel,
                buttonsBox);

        root.setPadding(new Insets(22));
        root.setAlignment(Pos.CENTER_LEFT);
        root.setStyle("-fx-background-color: #f4f4f4;");

        Scene scene = new Scene(root, 920, 700);
        scene.setOnKeyPressed(event -> {
            if (captureState.waitingKeyCell == null) {
                return;
            }

            KeyCode code = event.getCode();

            if (code == KeyCode.ESCAPE) {
                captureState.waitingKeyCell.refreshText();
                captureState.waitingKeyCell = null;
                hintLabel.setText("Modification annulée.");
                event.consume();
                return;
            }

            if (isForbiddenKey(code)) {
                hintLabel.setText("Touche non acceptée pour une action de jeu.");
                event.consume();
                return;
            }

            captureState.waitingKeyCell.setKeyCode(code);
            captureState.waitingKeyCell = null;
            hintLabel.setText("Touche modifiée. Tu peux en modifier une autre ou appliquer.");
            errorLabel.setText("");
            event.consume();
        });

        dialog.setScene(scene);
        dialog.showAndWait();
    }

    private static Label sectionTitle(String text) {
        Label label = new Label(text);
        label.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        return label;
    }

    private static KeyCell[] createInventoryCells(PlayerControls controls) {
        KeyCode[] keys = controls.getInventoryKeys();
        KeyCell[] cells = new KeyCell[9];

        for (int i = 0; i < cells.length; i++) {
            cells[i] = new KeyCell(keys[i]);
        }

        return cells;
    }

    private static GridPane buildInventoryGrid(KeyCell[] p1Inventory, KeyCell[] p2Inventory, List<Region> player2Rows) {
        GridPane grid = createGrid();
        grid.add(createHeaderLabel(""), 0, 0);

        for (int i = 0; i < 9; i++) {
            grid.add(createHeaderLabel("Slot " + (i + 1)), i + 1, 0);
        }

        grid.add(createRowLabel("Joueur 1"), 0, 1);
        for (int i = 0; i < 9; i++) {
            grid.add(p1Inventory[i], i + 1, 1);
        }

        Label p2Label = createRowLabel("Joueur 2");
        player2Rows.add(p2Label);
        grid.add(p2Label, 0, 2);

        for (int i = 0; i < 9; i++) {
            player2Rows.add(p2Inventory[i]);
            grid.add(p2Inventory[i], i + 1, 2);
        }

        return grid;
    }

    private static GridPane buildMovementGrid(
            KeyCell p1Forward,
            KeyCell p1Backward,
            KeyCell p1Right,
            KeyCell p1Left,
            KeyCell p2Forward,
            KeyCell p2Backward,
            KeyCell p2Right,
            KeyCell p2Left,
            List<Region> player2Rows
    ) {
        GridPane grid = createGrid();
        grid.add(createHeaderLabel(""), 0, 0);
        grid.add(createHeaderLabel("Avancer"), 1, 0);
        grid.add(createHeaderLabel("Reculer"), 2, 0);
        grid.add(createHeaderLabel("Aller à droite"), 3, 0);
        grid.add(createHeaderLabel("Aller à gauche"), 4, 0);

        grid.add(createRowLabel("Joueur 1"), 0, 1);
        grid.add(p1Forward, 1, 1);
        grid.add(p1Backward, 2, 1);
        grid.add(p1Right, 3, 1);
        grid.add(p1Left, 4, 1);

        Label p2Label = createRowLabel("Joueur 2");
        player2Rows.add(p2Label);
        grid.add(p2Label, 0, 2);

        player2Rows.add(p2Forward);
        player2Rows.add(p2Backward);
        player2Rows.add(p2Right);
        player2Rows.add(p2Left);

        grid.add(p2Forward, 1, 2);
        grid.add(p2Backward, 2, 2);
        grid.add(p2Right, 3, 2);
        grid.add(p2Left, 4, 2);

        return grid;
    }

    private static GridPane buildInteractGrid(KeyCell p1Interact, KeyCell p2Interact, List<Region> player2Rows) {
        GridPane grid = createGrid();
        grid.add(createHeaderLabel(""), 0, 0);
        grid.add(createHeaderLabel("Interagir"), 1, 0);

        grid.add(createRowLabel("Joueur 1"), 0, 1);
        grid.add(p1Interact, 1, 1);

        Label p2Label = createRowLabel("Joueur 2");
        player2Rows.add(p2Label);
        player2Rows.add(p2Interact);
        grid.add(p2Label, 0, 2);
        grid.add(p2Interact, 1, 2);

        return grid;
    }

    private static GridPane createGrid() {
        GridPane grid = new GridPane();
        grid.setHgap(8);
        grid.setVgap(8);
        grid.setAlignment(Pos.CENTER_LEFT);
        return grid;
    }

    private static Label createHeaderLabel(String text) {
        Label label = new Label(text);
        label.setMinWidth(78);
        label.setAlignment(Pos.CENTER);
        label.setStyle("-fx-font-weight: bold;");
        return label;
    }

    private static Label createRowLabel(String text) {
        Label label = new Label(text);
        label.setMinWidth(78);
        label.setAlignment(Pos.CENTER_LEFT);
        label.setStyle("-fx-font-weight: bold;");
        return label;
    }

    private static void registerCapture(CaptureState captureState, KeyCell... cells) {
        for (KeyCell cell : cells) {
            cell.setOnAction(e -> {
                if (captureState.waitingKeyCell != null) {
                    captureState.waitingKeyCell.refreshText();
                }

                captureState.waitingKeyCell = cell;
                captureState.waitingKeyCell.armCapture();
            });
        }
    }

    private static PlayerControls buildControls(
            KeyCell forward,
            KeyCell backward,
            KeyCell left,
            KeyCell right,
            KeyCell interact,
            KeyCell[] inventory
    ) {
        KeyCode[] inventoryKeys = new KeyCode[inventory.length];

        for (int i = 0; i < inventory.length; i++) {
            inventoryKeys[i] = inventory[i].getKeyCode();
        }

        return new PlayerControls(
                forward.getKeyCode(),
                backward.getKeyCode(),
                left.getKeyCode(),
                right.getKeyCode(),
                interact.getKeyCode(),
                inventoryKeys
        );
    }

    private static void applyControlsToCells(
            PlayerControls controls,
            KeyCell forward,
            KeyCell backward,
            KeyCell left,
            KeyCell right,
            KeyCell interact,
            KeyCell[] inventory
    ) {
        forward.setKeyCode(controls.getMoveUp());
        backward.setKeyCode(controls.getMoveDown());
        left.setKeyCode(controls.getMoveLeft());
        right.setKeyCode(controls.getMoveRight());
        interact.setKeyCode(controls.getInteract());

        KeyCode[] inventoryKeys = controls.getInventoryKeys();
        for (int i = 0; i < inventory.length; i++) {
            inventory[i].setKeyCode(inventoryKeys[i]);
        }
    }

    private static String validateControls(int playerCount, PlayerControls p1, PlayerControls p2) {
        if (p1.hasInternalConflict()) {
            return "Conflit dans les touches du joueur 1.";
        }

        if (playerCount == 2) {
            if (p2.hasInternalConflict()) {
                return "Conflit dans les touches du joueur 2.";
            }

            if (PlayerControls.hasConflict(p1, p2)) {
                return "Conflit entre les touches du joueur 1 et du joueur 2.";
            }
        }

        return null;
    }

    private static boolean isForbiddenKey(KeyCode code) {
        return code == null
                || code == KeyCode.UNDEFINED
                || code == KeyCode.SHIFT
                || code == KeyCode.CONTROL
                || code == KeyCode.ALT
                || code == KeyCode.META;
    }

    private static String formatKey(KeyCode code) {
        if (code == null) {
            return "?";
        }

        return switch (code) {
            case DIGIT1 -> "& / 1";
            case DIGIT2 -> "é / 2";
            case DIGIT3 -> "\" / 3";
            case DIGIT4 -> "' / 4";
            case DIGIT5 -> "( / 5";
            case DIGIT6 -> "- / 6";
            case DIGIT7 -> "è / 7";
            case DIGIT8 -> "_ / 8";
            case DIGIT9 -> "ç / 9";
            case NUMPAD1 -> "Pavé 1";
            case NUMPAD2 -> "Pavé 2";
            case NUMPAD3 -> "Pavé 3";
            case NUMPAD4 -> "Pavé 4";
            case NUMPAD5 -> "Pavé 5";
            case NUMPAD6 -> "Pavé 6";
            case NUMPAD7 -> "Pavé 7";
            case NUMPAD8 -> "Pavé 8";
            case NUMPAD9 -> "Pavé 9";
            case UP -> "↑";
            case DOWN -> "↓";
            case LEFT -> "←";
            case RIGHT -> "→";
            case ENTER -> "Entrée";
            case SPACE -> "Espace";
            default -> code.getName();
        };
    }

    private static final class CaptureState {
        private KeyCell waitingKeyCell;
    }

    private static final class KeyCell extends Button {
        private KeyCode keyCode;

        private KeyCell(KeyCode keyCode) {
            super();
            setKeyCode(keyCode);
            setMinWidth(78);
            setMaxWidth(Double.MAX_VALUE);
            setFocusTraversable(false);
        }

        private KeyCode getKeyCode() {
            return keyCode;
        }

        private void setKeyCode(KeyCode keyCode) {
            this.keyCode = keyCode;
            refreshText();
            setStyle("-fx-background-color: white; -fx-border-color: #555; -fx-border-width: 1;");
        }

        private void armCapture() {
            setText("...");
            setStyle("-fx-background-color: #fff2aa; -fx-border-color: #cc8800; -fx-border-width: 2;");
        }

        private void refreshText() {
            setText(formatKey(keyCode));
            setStyle("-fx-background-color: white; -fx-border-color: #555; -fx-border-width: 1;");
        }
    }
}