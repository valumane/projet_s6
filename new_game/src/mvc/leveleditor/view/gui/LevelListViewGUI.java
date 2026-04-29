package mvc.leveleditor.view.gui;

import common.leveleditor.LevelRegistry.LevelSummary;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import mvc.leveleditor.view.base.LevelListView;

import java.nio.file.Path;
import java.util.List;
import java.util.function.Consumer;

public class LevelListViewGUI extends LevelListView {

    private final Stage stage;
    private final VBox levelListBox = new VBox(8);
    private final Label errorLabel = new Label();

    private Runnable onNewLevel;
    private Consumer<Path> onEditLevel;
    private Consumer<Path> onDeleteLevel;
    private Runnable onBack;

    public LevelListViewGUI(Stage stage) {
        this.stage = stage;
        errorLabel.setStyle("-fx-text-fill: red;");
    }

    private void buildScene() {
        Label title = new Label("Level Editor");
        title.setStyle("-fx-font-size: 26px; -fx-font-weight: bold;");

        Button newBtn = new Button("+ Nouveau niveau");
        newBtn.setMaxWidth(Double.MAX_VALUE);
        newBtn.setOnAction(e -> { if (onNewLevel != null) onNewLevel.run(); });

        Button backBtn = new Button("Retour au menu");
        backBtn.setOnAction(e -> { if (onBack != null) onBack.run(); });

        ScrollPane scroll = new ScrollPane(levelListBox);
        scroll.setFitToWidth(true);
        scroll.setPrefHeight(400);

        VBox box = new VBox(16, title, newBtn, new Label("Niveaux custom :"), scroll, errorLabel, backBtn);
        box.setPadding(new Insets(28));
        box.setAlignment(Pos.TOP_LEFT);
        box.setStyle("-fx-background-color: #f4f4f4;");

        Scene scene = new Scene(box, 700, 580);
        stage.setTitle("Level Editor");
        stage.setScene(scene);
    }

    @Override
    public void setSummaries(List<LevelSummary> summaries) {
        Platform.runLater(() -> {
            levelListBox.getChildren().clear();
            if (summaries.isEmpty()) {
                levelListBox.getChildren().add(new Label("Aucun niveau custom pour l'instant."));
                return;
            }
            for (LevelSummary s : summaries) {
                HBox row = new HBox(12);
                row.setAlignment(Pos.CENTER_LEFT);
                row.setStyle("-fx-background-color: white; -fx-border-color: #ccc; -fx-border-width: 1; -fx-padding: 8;");

                Label nameLabel = new Label(s.name());
                nameLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
                Label dateLabel = new Label("Modifié le " + s.dateStr());
                dateLabel.setStyle("-fx-text-fill: #666;");

                VBox info = new VBox(2, nameLabel, dateLabel);
                HBox.setHgrow(info, Priority.ALWAYS);

                Button editBtn = new Button("Modifier");
                editBtn.setOnAction(e -> { if (onEditLevel != null) onEditLevel.accept(s.path()); });

                Button delBtn = new Button("Supprimer");
                delBtn.setStyle("-fx-text-fill: red;");
                delBtn.setOnAction(e -> {
                    Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                        "Supprimer \"" + s.name() + "\" ?", ButtonType.YES, ButtonType.NO);
                    confirm.showAndWait().ifPresent(btn -> {
                        if (btn == ButtonType.YES && onDeleteLevel != null) {
                            onDeleteLevel.accept(s.path());
                        }
                    });
                });

                row.getChildren().addAll(info, editBtn, delBtn);
                levelListBox.getChildren().add(row);
            }
        });
    }

    @Override public void setOnNewLevel(Runnable a) { this.onNewLevel = a; }
    @Override public void setOnEditLevel(Consumer<Path> a) { this.onEditLevel = a; }
    @Override public void setOnDeleteLevel(Consumer<Path> a) { this.onDeleteLevel = a; }
    @Override public void setOnBack(Runnable a) { this.onBack = a; }
    @Override public void showError(String msg) { Platform.runLater(() -> errorLabel.setText(msg)); }

    @Override
    public void show() { Platform.runLater(() -> { buildScene(); stage.show(); }); }

    @Override
    public void hide() { Platform.runLater(stage::hide); }
}