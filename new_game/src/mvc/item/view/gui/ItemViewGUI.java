package mvc.item.view.gui;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import mvc.item.view.base.ItemView;

public class ItemViewGUI extends ItemView {

    private final Stage stage;

    private final Label nameLabel = new Label("Item: ?");
    private final Label ownerLabel = new Label("Location: ?");
    private final TextArea descriptionArea = new TextArea();
    private final TextArea logArea = new TextArea();

    private final Button takeButton = new Button("Take");
    private final Button dropButton = new Button("Drop");
    private final Button useButton = new Button("Use");
    private final Button refreshButton = new Button("Refresh");

    public ItemViewGUI(Stage stage, String title) {
        this.stage = stage;
        this.stage.setTitle(title);

        descriptionArea.setEditable(false);
        descriptionArea.setWrapText(true);
        descriptionArea.setPrefRowCount(4);

        logArea.setEditable(false);
        logArea.setWrapText(true);
        logArea.setPrefRowCount(8);

        HBox buttons = new HBox(8, takeButton, dropButton, useButton, refreshButton);
        VBox root = new VBox(10, nameLabel, ownerLabel, descriptionArea, buttons, logArea);
        root.setPadding(new Insets(10));

        Scene scene = new Scene(root, 460, 320);
        stage.setScene(scene);
    }

    private void log(String msg) {
        Platform.runLater(() -> {
            if (!logArea.getText().isEmpty()) {
                logArea.appendText("\n");
            }
            logArea.appendText(msg);
        });
    }

    @Override
    public void show() {
        Platform.runLater(stage::show);
    }

    @Override
    public void hide() {
        Platform.runLater(stage::hide);
    }

    @Override
    public void showItemName(String name) {
        Platform.runLater(() -> nameLabel.setText("Item: " + name));
    }

    @Override
    public void showItemDescription(String description) {
        Platform.runLater(() -> descriptionArea.setText(description));
    }

    @Override
    public void showOwnership(boolean inInventory) {
        Platform.runLater(() -> ownerLabel.setText("Location: " + (inInventory ? "inventory" : "room")));
    }

    @Override
    public void showMessage(String message) {
        log(message);
    }

    @Override
    public void setOnTake(Runnable action) {
        Platform.runLater(() -> takeButton.setOnAction(e -> action.run()));
    }

    @Override
    public void setOnDrop(Runnable action) {
        Platform.runLater(() -> dropButton.setOnAction(e -> action.run()));
    }

    @Override
    public void setOnUse(Runnable action) {
        Platform.runLater(() -> useButton.setOnAction(e -> action.run()));
    }

    @Override
    public void setOnRefresh(Runnable action) {
        Platform.runLater(() -> refreshButton.setOnAction(e -> action.run()));
    }

    @Override
    public void setActionsVisible(boolean takeVisible, boolean dropVisible, boolean useVisible) {
        Platform.runLater(() -> {
            takeButton.setVisible(takeVisible);
            takeButton.setManaged(takeVisible);

            dropButton.setVisible(dropVisible);
            dropButton.setManaged(dropVisible);

            useButton.setVisible(useVisible);
            useButton.setManaged(useVisible);
        });
    }
}