package mvc.game.view.gui;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class LogWindowGUI {

    private final Stage stage = new Stage();
    private final TextArea logArea = new TextArea();

    public LogWindowGUI() {
        logArea.setEditable(false);
        logArea.setWrapText(true);
        logArea.setFocusTraversable(false);

        BorderPane root = new BorderPane(logArea);
        root.setPadding(new Insets(10));

        Scene scene = new Scene(root, 520, 420);
        stage.setTitle("Logs");
        stage.setScene(scene);
    }

    public void append(String msg) {
        Platform.runLater(() -> {
            if (!logArea.getText().isEmpty()) {
                logArea.appendText("\n");
            }
            logArea.appendText(msg);
        });
    }

    public void showWindow() {
        Platform.runLater(() -> {
            stage.show();
            stage.toFront();
        });
    }
}