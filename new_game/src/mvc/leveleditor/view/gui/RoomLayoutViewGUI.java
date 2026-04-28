package mvc.leveleditor.view.gui;

import common.leveleditor.LevelData;
import common.leveleditor.RoomEditorData;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import mvc.leveleditor.view.base.RoomLayoutView;

import java.util.List;
import java.util.function.*;

public class RoomLayoutViewGUI extends RoomLayoutView {

    private final Stage stage;
    private final Canvas gridCanvas = new Canvas(600, 420);
    private final VBox roomPanel = new VBox(8);
    private final Label errorLabel = new Label();

    // callbacks etc
    private Consumer<Integer> onSelectRoom;
    private BiConsumer<Integer, String> onAddRoom;
    private Consumer<Integer> onDeleteRoom;
    private BiConsumer<Integer, String> onRenameRoom;
    private BiConsumer<Integer, String> onSetDescription;
    private Consumer<Integer> onSetStart;
    private Consumer<Integer> onSetBoss;
    private BiConsumer<Integer, String> onAddItem;
    private BiConsumer<Integer, String> onRemoveItem;
    private BiConsumer<Integer, String> onAddEnemy;
    private BiConsumer<Integer, String> onRemoveEnemy;
    private Runnable onSave;
    private Runnable onCancel;

    private LevelData currentLevel;
    private int selectedIndex = -1;

    private static final double CELL = 70;
    private static final double ROOM_SZ = 46;

    public RoomLayoutViewGUI(Stage stage) {
        this.stage = stage;
        errorLabel.setStyle("-fx-text-fill: red;");
    }

    private void buildScene() {
        Label title = new Label("Agencement des salles");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        // clics sur la grille
        gridCanvas.setOnMouseClicked(e -> {
            if (currentLevel == null) return;
            int minX = currentLevel.getRooms().stream().mapToInt(RoomEditorData::getGridX).min().orElse(0);
            int minY = currentLevel.getRooms().stream().mapToInt(RoomEditorData::getGridY).min().orElse(0);
            double offX = (gridCanvas.getWidth() - (currentLevel.getRooms().stream().mapToInt(r -> r.getGridX() - minX).max().orElse(0) + 2) * CELL) / 2;
            double offY = (gridCanvas.getHeight() - (currentLevel.getRooms().stream().mapToInt(r -> r.getGridY() - minY).max().orElse(0) + 2) * CELL) / 2;

            for (int i = 0; i < currentLevel.getRooms().size(); i++) {
                RoomEditorData r = currentLevel.getRooms().get(i);
                double cx = offX + (r.getGridX() - minX) * CELL + CELL / 2 - ROOM_SZ / 2;
                double cy = offY + (r.getGridY() - minY) * CELL + CELL / 2 - ROOM_SZ / 2;
                if (e.getX() >= cx && e.getX() <= cx + ROOM_SZ && e.getY() >= cy && e.getY() <= cy + ROOM_SZ) {
                    if (onSelectRoom != null) onSelectRoom.accept(i);
                    break;
                }
            }
        });

        Button saveBtn = new Button("💾 Enregistrer");
        saveBtn.setOnAction(e -> { if (onSave != null) onSave.run(); });
        Button cancelBtn = new Button("✕ Annuler");
        cancelBtn.setOnAction(e -> { if (onCancel != null) onCancel.run(); });
        HBox bottomBar = new HBox(12, saveBtn, cancelBtn, errorLabel);
        bottomBar.setAlignment(Pos.CENTER_RIGHT);

        ScrollPane roomScroll = new ScrollPane(roomPanel);
        roomScroll.setFitToWidth(true);
        roomScroll.setPrefWidth(280);
        roomScroll.setPrefHeight(420);

        HBox content = new HBox(12, gridCanvas, roomScroll);
        content.setAlignment(Pos.TOP_LEFT);

        VBox root = new VBox(12, title, content, bottomBar);
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: #f4f4f4;");

        Scene scene = new Scene(root, 980, 580);
        stage.setTitle("Level Editor | Agencement");
        stage.setScene(scene);
    }

    @Override
    public void renderGrid(LevelData level, int selectedIndex) {
        this.currentLevel = level;
        this.selectedIndex = selectedIndex;
        Platform.runLater(() -> {
            GraphicsContext gc = gridCanvas.getGraphicsContext2D();
            gc.setFill(Color.web("#dde"));
            gc.fillRect(0, 0, gridCanvas.getWidth(), gridCanvas.getHeight());

            if (level == null || level.getRooms().isEmpty()) return;

            int minX = level.getRooms().stream().mapToInt(RoomEditorData::getGridX).min().orElse(0);
            int minY = level.getRooms().stream().mapToInt(RoomEditorData::getGridY).min().orElse(0);
            int maxX = level.getRooms().stream().mapToInt(RoomEditorData::getGridX).max().orElse(0);
            int maxY = level.getRooms().stream().mapToInt(RoomEditorData::getGridY).max().orElse(0);

            double offX = (gridCanvas.getWidth() - (maxX - minX + 2) * CELL) / 2;
            double offY = (gridCanvas.getHeight() - (maxY - minY + 2) * CELL) / 2;

            // connexions
            gc.setStroke(Color.DARKGRAY);
            gc.setLineWidth(2);
            for (int i = 0; i < level.getRooms().size(); i++) {
                RoomEditorData r = level.getRooms().get(i);
                double cx = offX + (r.getGridX() - minX) * CELL + CELL / 2;
                double cy = offY + (r.getGridY() - minY) * CELL + CELL / 2;
                for (int j = 0; j < r.getExitDirections().size(); j++) {
                    int tIdx = r.getExitTargetIndices().get(j);
                    if (tIdx > i) {
                        RoomEditorData t = level.getRooms().get(tIdx);
                        double tx = offX + (t.getGridX() - minX) * CELL + CELL / 2;
                        double ty = offY + (t.getGridY() - minY) * CELL + CELL / 2;
                        gc.setStroke(r.getExitLocked().get(j) ? Color.RED : Color.DARKGRAY);
                        gc.strokeLine(cx, cy, tx, ty);
                    }
                }
            }

            // rooms
            for (int i = 0; i < level.getRooms().size(); i++) {
                RoomEditorData r = level.getRooms().get(i);
                double cx = offX + (r.getGridX() - minX) * CELL + CELL / 2 - ROOM_SZ / 2;
                double cy = offY + (r.getGridY() - minY) * CELL + CELL / 2 - ROOM_SZ / 2;

                if (i == selectedIndex)        gc.setFill(Color.web("#ff9800"));
                else if (r.isStartRoom())      gc.setFill(Color.web("#4caf50"));
                else if (r.isBossRoom())       gc.setFill(Color.web("#e53935"));
                else                           gc.setFill(Color.web("#1976d2"));

                gc.fillRoundRect(cx, cy, ROOM_SZ, ROOM_SZ, 8, 8);

                if (i == selectedIndex) {
                    gc.setStroke(Color.ORANGE);
                    gc.setLineWidth(3);
                    gc.strokeRoundRect(cx - 1, cy - 1, ROOM_SZ + 2, ROOM_SZ + 2, 8, 8);
                }

                gc.setFill(Color.WHITE);
                gc.setFont(javafx.scene.text.Font.font(9));
                String label = r.getName().length() > 7 ? r.getName().substring(0, 6) + "…" : r.getName();
                gc.fillText(label, cx + 2, cy + ROOM_SZ / 2 + 4);
            }
        });
    }

    @Override
    public void showRoomPanel(RoomEditorData room, int index) {
        Platform.runLater(() -> {
            roomPanel.getChildren().clear();

            Label panelTitle = new Label("Salle sélectionnée");
            panelTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

            // nom
            TextField nameField = new TextField(room.getName());
            Button renameBtn = new Button("Renommer");
            renameBtn.setOnAction(e -> { if (onRenameRoom != null) onRenameRoom.accept(index, nameField.getText()); });

            // description
            TextArea descArea = new TextArea(room.getDescription());
            descArea.setPrefRowCount(2);
            Button descBtn = new Button("Définir description");
            descBtn.setOnAction(e -> { if (onSetDescription != null) onSetDescription.accept(index, descArea.getText()); });

            // type
            Button setStartBtn = new Button(room.isStartRoom() ? "Départ" : "Définir comme Départ");
            setStartBtn.setStyle(room.isStartRoom() ? "-fx-background-color: #4caf50; -fx-text-fill: white;" : "");
            setStartBtn.setOnAction(e -> { if (onSetStart != null) onSetStart.accept(index); });

            Button setBossBtn = new Button(room.isBossRoom() ? "Boss" : "Définir comme Boss");
            setBossBtn.setStyle(room.isBossRoom() ? "-fx-background-color: #e53935; -fx-text-fill: white;" : "");
            setBossBtn.setOnAction(e -> { if (onSetBoss != null) onSetBoss.accept(index); });

            // ajout rooms dans les 4 dir
            Label addRoomLabel = new Label("Ajouter une salle :");
            addRoomLabel.setStyle("-fx-font-weight: bold;");
            HBox dirButtons = new HBox(6);
            for (String dir : List.of("north", "south", "east", "west")) {
                Button b = new Button(dirIcon(dir));
                b.setDisable(room.hasExitIn(dir));
                b.setOnAction(e -> { if (onAddRoom != null) onAddRoom.accept(index, dir); });
                dirButtons.getChildren().add(b);
            }

            // suppr
            Button deleteBtn = new Button("Supprimer cette salle");
            deleteBtn.setStyle("-fx-text-fill: red;");
            deleteBtn.setOnAction(e -> {
                Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                    "Supprimer \"" + room.getName() + "\" ?", ButtonType.YES, ButtonType.NO);
                confirm.showAndWait().ifPresent(btn -> {
                    if (btn == ButtonType.YES && onDeleteRoom != null) onDeleteRoom.accept(index);
                });
            });

            // items
            Label itemsLabel = new Label("Items (" + room.getItemDescriptors().size() + ") :");
            itemsLabel.setStyle("-fx-font-weight: bold;");
            VBox itemsBox = new VBox(4);
            for (String desc : room.getItemDescriptors()) {
                HBox row = new HBox(6);
                Label l = new Label(desc);
                HBox.setHgrow(l, Priority.ALWAYS);
                Button rm = new Button("✕");
                rm.setOnAction(e -> { if (onRemoveItem != null) onRemoveItem.accept(index, desc); });
                row.getChildren().addAll(l, rm);
                itemsBox.getChildren().add(row);
            }

            ComboBox<String> itemCombo = new ComboBox<>();
            itemCombo.getItems().addAll("Item:Rubis:Une gemme", "Item:Pièce d'or:Une vieille pièce",
                "HealScroll:25", "Weapon:Épée:MELEE:10", "Weapon:Arc:RANGED:10");
            itemCombo.setPromptText("Choisir un item...");
            Button addItemBtn = new Button("+ Ajouter");
            addItemBtn.setOnAction(e -> {
                if (itemCombo.getValue() != null && onAddItem != null)
                    onAddItem.accept(index, itemCombo.getValue());
            });
            HBox itemAddRow = new HBox(6, itemCombo, addItemBtn);

            // ennemies
            Label enemiesLabel = new Label("Ennemis (" + room.getEnemyDescriptors().size() + ") :");
            enemiesLabel.setStyle("-fx-font-weight: bold;");
            VBox enemiesBox = new VBox(4);
            for (String desc : room.getEnemyDescriptors()) {
                HBox row = new HBox(6);
                Label l = new Label(desc);
                HBox.setHgrow(l, Priority.ALWAYS);
                Button rm = new Button("✕");
                rm.setOnAction(e -> { if (onRemoveEnemy != null) onRemoveEnemy.accept(index, desc); });
                row.getChildren().addAll(l, rm);
                enemiesBox.getChildren().add(row);
            }

            ComboBox<String> enemyCombo = new ComboBox<>();
            for (int diff = 1; diff <= 5; diff++) {
                enemyCombo.getItems().add("Berserker:" + diff);
                enemyCombo.getItems().add("Archer:" + diff);
            }
            enemyCombo.setPromptText("Choisir un ennemi...");
            Button addEnemyBtn = new Button("+ Ajouter");
            addEnemyBtn.setOnAction(e -> {
                if (enemyCombo.getValue() != null && onAddEnemy != null)
                    onAddEnemy.accept(index, enemyCombo.getValue());
            });
            HBox enemyAddRow = new HBox(6, enemyCombo, addEnemyBtn);

            Separator sep1 = new Separator(), sep2 = new Separator(), sep3 = new Separator();

            roomPanel.getChildren().addAll(
                panelTitle,
                new Label("Nom :"), nameField, renameBtn,
                new Label("Description :"), descArea, descBtn,
                sep1,
                new HBox(8, setStartBtn, setBossBtn),
                sep2,
                addRoomLabel, dirButtons,
                deleteBtn,
                sep3,
                itemsLabel, itemsBox, itemAddRow,
                new Separator(),
                enemiesLabel, enemiesBox, enemyAddRow
            );
        });
    }

    @Override
    public void clearRoomPanel() {
        Platform.runLater(() -> {
            roomPanel.getChildren().clear();
            roomPanel.getChildren().add(new Label("Cliquez sur une salle pour la modifier."));
        });
    }

    private String dirIcon(String dir) {
        return switch (dir) { case "north" -> "^ N"; case "south" -> "v S"; case "east" -> "> E"; default -> "< O"; };
    }

    @Override public void setOnSelectRoom(Consumer<Integer> a) { onSelectRoom = a; }
    @Override public void setOnAddRoom(BiConsumer<Integer, String> a) { onAddRoom = a; }
    @Override public void setOnDeleteRoom(Consumer<Integer> a) { onDeleteRoom = a; }
    @Override public void setOnRenameRoom(BiConsumer<Integer, String> a) { onRenameRoom = a; }
    @Override public void setOnSetDescription(BiConsumer<Integer, String> a) { onSetDescription = a; }
    @Override public void setOnSetStart(Consumer<Integer> a) { onSetStart = a; }
    @Override public void setOnSetBoss(Consumer<Integer> a) { onSetBoss = a; }
    @Override public void setOnAddItem(BiConsumer<Integer, String> a) { onAddItem = a; }
    @Override public void setOnRemoveItem(BiConsumer<Integer, String> a) { onRemoveItem = a; }
    @Override public void setOnAddEnemy(BiConsumer<Integer, String> a) { onAddEnemy = a; }
    @Override public void setOnRemoveEnemy(BiConsumer<Integer, String> a) { onRemoveEnemy = a; }
    @Override public void setOnSave(Runnable a) { onSave = a; }
    @Override public void setOnCancel(Runnable a) { onCancel = a; }
    @Override public void showError(String msg) { Platform.runLater(() -> errorLabel.setText(msg)); }
    @Override public void show() { Platform.runLater(() -> { buildScene(); stage.show(); }); }
    @Override public void hide() { Platform.runLater(stage::hide); }
}