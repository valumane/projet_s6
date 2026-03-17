package entity.view.gui;

import common.entity.Hero;
import entity.view.base.HeroView;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import jfx.incubator.scene.control.richtext.model.PlainTextFormatHandler;

public class HeroViewGUI extends HeroView {

    private final Stage stage;

    private final Label heroLabel = new Label("Hero");
    private final Label locationLabel = new Label("Location: ?");
    private final TextArea logArea = new TextArea();
    private final Label HpLabel = new Label("health : " );

    public HeroViewGUI(Stage stage) {
        this.stage = stage;

        logArea.setEditable(false);
        logArea.setWrapText(true);

        VBox top = new VBox(6, heroLabel, locationLabel);
        top.setPadding(new Insets(10));

        BorderPane root = new BorderPane();
        root.setTop(top);
        root.setCenter(logArea);
        root.setPadding(new Insets(10));

        // bouton qui retire de la vie au hero car je l'aime pas
        Button buttonRemoveLife = new Button("remove 1hp");


        // div health
        VBox divHealth = new VBox(6,buttonRemoveLife,HpLabel);


        // place le bouton en bas
        root.setRight(divHealth);
        
        BorderPane.setMargin(buttonRemoveLife, new Insets(10, 0, 0, 0));
        Scene scene = new Scene(root, 700, 450);
        stage.setScene(scene);
    }

    // --- helpers ---
    private void log(String msg) {
        Platform.runLater(() -> {
            if (!logArea.getText().isEmpty()) {
                logArea.appendText("\n");
            } else {
                logArea.appendText(msg);
            }
        });
    }

    // --- mvc.View ---
    @Override
    public void show() {
        Platform.runLater(stage::show);
    }

    @Override
    public void hide() {
        Platform.runLater(stage::hide);
    }

    // --- HeroView API ---
    @Override
    public void showDropObject(String character, String item) {
        log(character + " dropped " + item);
    }

    @Override
    public void showObjectNotFindInInventory(String item) {
        log(item + " not in the inventory");
    }

    @Override
    public void showLocation(String loc) {
        Platform.runLater(() -> locationLabel.setText("Location: " + loc));
    }

    @Override
    public void showNoHealSpell() {
        log("You don't know how to use this spell yet.");
    }

    @Override
    public void receiveHealingPower() {
        log("You receive a healing power.");
    }

    @Override
    public void showDontKnowHealingSpell() {
        log("You don't know any healing spell.");
    }

    @Override
    public void useHealingPower() {
        log("You use your healing power!");
    }

    @Override
    public void showHealth(int HpHero){
        Platform.runLater(()-> heroLabel.setText("Health"+ HpHero));
    }

    // bonus (pratique)
    public void setHeroName(String name) {
        Platform.runLater(() -> heroLabel.setText("Hero Name" + name));
    }
}