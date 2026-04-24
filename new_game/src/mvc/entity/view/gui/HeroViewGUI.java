package mvc.entity.view.gui;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import mvc.entity.view.base.HeroView;

public class HeroViewGUI extends HeroView {

    private final VBox root = new VBox(6);

    private final Label heroLabel = new Label("Hero: ?");
    private final Label locationLabel = new Label("Location: ?");

    public HeroViewGUI() {
        root.getChildren().addAll(heroLabel, locationLabel);
        root.setPadding(new Insets(10));
        root.setPrefWidth(220);
        root.setMinWidth(220);
    }

    public VBox getRoot() {
        return root;
    }

    @Override
    public void show() {
        Platform.runLater(() -> {
            root.setVisible(true);
            root.setManaged(true);
        });
    }

    @Override
    public void hide() {
        Platform.runLater(() -> {
            root.setVisible(false);
            root.setManaged(false);
        });
    }

    @Override
    public void showLocation(String loc) {
        Platform.runLater(() -> locationLabel.setText("Location: " + loc));
    }

    public void setHeroName(String name) {
        Platform.runLater(() -> heroLabel.setText("Hero: " + name));
    }

    @Override
    public void showDropObject(String character, String item) {
    }

    @Override
    public void showObjectNotFindInInventory(String item) {
    }

    @Override
    public void showNoHealSpell() {
    }

    @Override
    public void receiveHealingPower() {
    }

    @Override
    public void showDontKnowHealingSpell() {
    }

    @Override
    public void useHealingPower() {
    }

    @Override
    public void showHealth(int hpHero) {
    }
}