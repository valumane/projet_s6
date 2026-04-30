package mvc.leveleditor.view.gui;

import common.leveleditor.LevelData;
import common.leveleditor.RoomEditorData;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import mvc.leveleditor.view.base.LevelEditorView;

public class LevelEditorViewGUI extends LevelEditorView {

    private final Stage stage;
    private final TextField nameField = new TextField();
    private final TextArea descField = new TextArea();
    private final Canvas minimap = new Canvas(500, 320);
    private final Label statusLabel = new Label();

    private Runnable onEditLayout;
    private Runnable onSave;
    private Runnable onBack;

    public LevelEditorViewGUI(Stage stage) {
        this.stage = stage;
        descField.setPrefRowCount(3);
        descField.setWrapText(true);
    }

    private void buildScene() {
        Label title = new Label("Éditeur de niveau");
        title.setStyle("-fx-font-size: 22px; -fx-font-weight: bold;");

        Label nameLabel = new Label("Nom du niveau :");
        nameLabel.setStyle("-fx-font-weight: bold;");
        Label descLabel = new Label("Description :");
        descLabel.setStyle("-fx-font-weight: bold;");

        Button editLayoutBtn = new Button("Modifier les salles");
        editLayoutBtn.setMaxWidth(Double.MAX_VALUE);
        editLayoutBtn.setStyle("-fx-font-size: 14px;");
        editLayoutBtn.setOnAction(e -> { if (onEditLayout != null) onEditLayout.run(); });

        Button saveBtn = new Button("Enregistrer");
        saveBtn.setOnAction(e -> { if (onSave != null) onSave.run(); });

        Button backBtn = new Button("Retour");
        backBtn.setOnAction(e -> { if (onBack != null) onBack.run(); });

        statusLabel.setStyle("-fx-text-fill: green;");

        HBox buttons = new HBox(10, saveBtn, backBtn);
        buttons.setAlignment(Pos.CENTER_RIGHT);

        Label minimapTitle = new Label("Minimap du niveau :");
        minimapTitle.setStyle("-fx-font-weight: bold;");

        VBox form = new VBox(10,
            title,
            nameLabel, nameField,
            descLabel, descField,
            minimapTitle,
            minimap,
            editLayoutBtn,
            statusLabel,
            buttons);
        form.setPadding(new Insets(24));
        form.setStyle("-fx-background-color: #f4f4f4;");

        Scene scene = new Scene(form, 620, 660);
        stage.setTitle("Level Editor | " + nameField.getText());
        stage.setScene(scene);
    }

    @Override
    public void renderMinimap(LevelData level) {
        Platform.runLater(() -> {
            GraphicsContext gc = minimap.getGraphicsContext2D();
            gc.setFill(Color.web("#e8e8e8"));
            gc.fillRect(0, 0, minimap.getWidth(), minimap.getHeight());

            if (level == null || level.getRooms().isEmpty()) return;

            // calcul offset pour centrer la minimap
            int minX = level.getRooms().stream().mapToInt(RoomEditorData::getGridX).min().orElse(0);
            int minY = level.getRooms().stream().mapToInt(RoomEditorData::getGridY).min().orElse(0);
            int maxX = level.getRooms().stream().mapToInt(RoomEditorData::getGridX).max().orElse(0);
            int maxY = level.getRooms().stream().mapToInt(RoomEditorData::getGridY).max().orElse(0);

            double cellSize = Math.min(
                minimap.getWidth() / Math.max(maxX - minX + 2, 1),
                minimap.getHeight() / Math.max(maxY - minY + 2, 1)
            );
            cellSize = Math.min(cellSize, 60);
            double roomSize = cellSize * 0.6;

            double offsetX = (minimap.getWidth() - (maxX - minX + 1) * cellSize) / 2;
            double offsetY = (minimap.getHeight() - (maxY - minY + 1) * cellSize) / 2;

            //dessiner les connexions
            gc.setStroke(Color.GRAY);
            gc.setLineWidth(2);
            for (int i = 0; i < level.getRooms().size(); i++) {
                RoomEditorData r = level.getRooms().get(i);
                double cx = offsetX + (r.getGridX() - minX) * cellSize + cellSize / 2;
                double cy = offsetY + (r.getGridY() - minY) * cellSize + cellSize / 2;
                for (int j = 0; j < r.getExitDirections().size(); j++) {
                    int targetIdx = r.getExitTargetIndices().get(j);
                    if (targetIdx > i) { // Dessiner une seule fois
                        RoomEditorData t = level.getRooms().get(targetIdx);
                        double tx = offsetX + (t.getGridX() - minX) * cellSize + cellSize / 2;
                        double ty = offsetY + (t.getGridY() - minY) * cellSize + cellSize / 2;
                        gc.strokeLine(cx, cy, tx, ty);
                    }
                }
            }

            // ... les rooms
            for (int i = 0; i < level.getRooms().size(); i++) {
                RoomEditorData r = level.getRooms().get(i);
                double cx = offsetX + (r.getGridX() - minX) * cellSize + cellSize / 2 - roomSize / 2;
                double cy = offsetY + (r.getGridY() - minY) * cellSize + cellSize / 2 - roomSize / 2;

                if (r.isStartRoom())      gc.setFill(Color.web("#4caf50"));
                else if (r.isBossRoom()) gc.setFill(Color.web("#e53935"));
                else                     gc.setFill(Color.web("#1976d2"));

                gc.fillRoundRect(cx, cy, roomSize, roomSize, 6, 6);
                gc.setFill(Color.WHITE);
                gc.setFont(javafx.scene.text.Font.font(10));
                gc.fillText(r.getName().length() > 8
                    ? r.getName().substring(0, 7) + "…"
                    : r.getName(), cx + 2, cy + roomSize / 2 + 4);
            }
        });
    }

    @Override public void setLevelName(String name) { Platform.runLater(() -> nameField.setText(name)); }
    @Override public void setLevelDescription(String d) { Platform.runLater(() -> descField.setText(d)); }
    @Override public String getLevelName() { return nameField.getText().trim(); }
    @Override public String getLevelDescription() { return descField.getText().trim(); }
    @Override public void setOnEditLayout(Runnable a) { this.onEditLayout = a; }
    @Override public void setOnSave(Runnable a) { this.onSave = a; }
    @Override public void setOnBack(Runnable a) { this.onBack = a; }
    @Override public void showSavedFeedback() {
        Platform.runLater(() -> {
            statusLabel.setText("Niveau enregistré !");
            statusLabel.setStyle("-fx-text-fill: green;");
        });
    }
    @Override public void showError(String msg) {
        Platform.runLater(() -> {
            statusLabel.setText("✗ " + msg);
            statusLabel.setStyle("-fx-text-fill: red;");
        });
    }
    @Override public void show() { Platform.runLater(() -> { buildScene(); stage.show(); }); }
    @Override public void hide() { Platform.runLater(stage::hide); }
}