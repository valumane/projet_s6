package mvc.menu.view.gui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import mvc.GameConfig;

public final class PauseSettingsDialog {

    private PauseSettingsDialog() {
    }

    public static void show(Stage owner, Runnable onApplied) {
        Stage dialog = new Stage();
        dialog.initOwner(owner);
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Paramètres");

        Label resolutionLabel = new Label("Résolution");

        ComboBox<String> resolutionCombo = new ComboBox<>();
        resolutionCombo.getItems().addAll("1200x800", "1600x900");
        resolutionCombo.setValue(GameConfig.getResolution());

        Button applyButton = new Button("Appliquer");
        Button closeButton = new Button("Fermer");

        applyButton.setOnAction(e -> {
            GameConfig.setResolution(resolutionCombo.getValue());

            if (onApplied != null) {
                onApplied.run();
            }

            dialog.close();
        });

        closeButton.setOnAction(e -> dialog.close());

        VBox root = new VBox(12,
                resolutionLabel,
                resolutionCombo,
                applyButton,
                closeButton);
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.CENTER_LEFT);

        Scene scene = new Scene(root, 320, 180);
        dialog.setScene(scene);
        dialog.showAndWait();
    }
}