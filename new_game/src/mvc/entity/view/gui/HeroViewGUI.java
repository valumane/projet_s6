package mvc.entity.view.gui;

import common.language.Language;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import mvc.entity.view.base.HeroView;

public class HeroViewGUI extends HeroView {

    private final VBox root = new VBox(8);

    private final Label titleLabel = new Label(Language.t("game.statsHero"));
    private final Label heroLabel = new Label(Language.t("gui.heroLabel") + "?");
    private final Label hpLabel = new Label("HP: ?");
    private final Label locationLabel = new Label(Language.t("gui.locationLabel") + "?");

    public HeroViewGUI() {
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        root.getChildren().addAll(titleLabel, heroLabel, hpLabel, locationLabel);
        root.setPadding(new Insets(12));
        root.setSpacing(8);
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
        Platform.runLater(() -> locationLabel.setText(Language.t("gui.locationLabel") + loc));
    }

    public void setHeroName(String name) {
        Platform.runLater(() -> heroLabel.setText(Language.t("gui.heroLabel") + name));
    }

    @Override
    public void showHealth(int hpHero) {
        Platform.runLater(() -> hpLabel.setText("HP: " + hpHero));
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
}