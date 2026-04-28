package application;

import common.dungeon.DungeonData;
import common.dungeon.DungeonGenerator;
import common.entity.Archer;
import common.entity.Berserker;
import common.entity.Enemy;
import common.entity.Hero;
import common.languages.Languages;
import common.item.Bag;
import common.item.Chest;
import common.item.HealSpell;
import common.item.Item;
import common.item.Scroll;
import common.item.Weapon;
import common.map.Room;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import javafx.stage.Stage;
import mvc.GameConfig;
import mvc.entity.controller.HeroController;
import mvc.entity.model.HeroModel;
import mvc.entity.view.cli.HeroViewCLI;
import mvc.entity.view.gui.HeroViewGUI;
import mvc.game.controller.GameController;
import mvc.game.model.GameModel;
import mvc.game.view.gui.GameViewGUI;
import mvc.game.view.gui.LogWindowGUI;
import mvc.map.controller.RoomController;
import mvc.map.model.RoomModel;
import mvc.map.view.cli.RoomViewCLI;
import mvc.map.view.gui.RoomViewGUI;

public final class GameLauncher {

    private static final int DEFAULT_HERO_DAMAGE = 0;
    private static final int DEFAULT_HERO_BAG_CAPACITY = 9;

    private GameLauncher() {
    }

    public static void startRandomGame(Stage stage) {
        startRandomGame(stage, GameConfig.getPlayerCount(), null);
    }

    public static void startRandomGame(Stage stage, int playerCount) {
        startRandomGame(stage, playerCount, null);
    }

    private static void startRandomGame(Stage stage, int playerCount, Hero existingHero) {
        int safePlayerCount = playerCount == 2 ? 2 : 1;
        GameConfig.setPlayerCount(safePlayerCount);

        DungeonGenerator generator = new DungeonGenerator(System.currentTimeMillis());
        DungeonData dungeon = generator.generate(12);

        Room startRoom = dungeon.getStartRoom();
        Room bossRoom = dungeon.getBossRoom();

        Hero hero;

        if (existingHero == null) {
            hero = new Hero(
                    Languages.t("hero.name"),
                    100,
                    new Bag(Languages.t("hero.backpack"), DEFAULT_HERO_BAG_CAPACITY),
                    startRoom,
                    DEFAULT_HERO_DAMAGE);

            Weapon basicSword = new Weapon(Languages.t("item.basicSword"), 10, Weapon.WeaponType.MELEE);
            Weapon basicBow = new Weapon(Languages.t("item.basicBow"), 10, Weapon.WeaponType.RANGED);

            hero.addItem(basicSword);
            hero.addItem(basicBow);
            hero.equipWeapon(basicSword);
        } else {
            hero = existingHero;
            hero.setCurrentRoom(startRoom);
        }

        Scroll healingScroll = new Scroll(Languages.t("item.healingScroll"), new HealSpell(25));
        Chest chest = new Chest(Languages.t("item.woodenChest"), false, Languages.t("item.chestDesc"));

        chest.addItem(new Item(Languages.t("item.ruby"), Languages.t("item.rubyDesc")));
        chest.addItem(new Item(Languages.t("item.coin"), Languages.t("item.coinDesc")));

        startRoom.addItem(healingScroll);
        startRoom.addItem(chest);

        List<Enemy> keyCandidates = new ArrayList<>();

        int i = 0;
        for (Room room : dungeon.getRooms()) {
            if (room == startRoom) {
                continue;
            }

            if (room == bossRoom) {
                room.addCharacter(new Berserker(5));
                room.addCharacter(new Archer(5));
                continue;
            }

            int difficulty = Math.min(5, 1 + i / 3);

            Enemy enemy;

            if (i % 2 == 0) {
                enemy = new Berserker(difficulty);
            } else {
                enemy = new Archer(difficulty);
            }

            room.addCharacter(enemy);
            keyCandidates.add(enemy);

            i++;
        }

        if (!keyCandidates.isEmpty()) {
            Enemy keyHolder = keyCandidates.get(ThreadLocalRandom.current().nextInt(keyCandidates.size()));
            keyHolder.addToInventory(dungeon.getGoldenKey());
        } else {
            startRoom.addItem(dungeon.getGoldenKey());
        }

        HeroModel heroModel = new HeroModel(hero);
        HeroModel secondHeroModel = null;

        RoomModel roomModel = new RoomModel(heroModel.getRoom());

        HeroViewCLI heroViewCLI = new HeroViewCLI();
        RoomViewCLI roomViewCLI = new RoomViewCLI();

        HeroViewGUI heroViewGUI = new HeroViewGUI();
        HeroViewGUI secondHeroViewGUI = null;

        if (safePlayerCount == 2) {
            Hero secondHero = new Hero(
                    Languages.t("hero.name2"),
                    100,
                    new Bag(Languages.t("hero.backpackP2"), DEFAULT_HERO_BAG_CAPACITY),
                    startRoom,
                    DEFAULT_HERO_DAMAGE);

            Weapon secondBasicSword = new Weapon(Languages.t("item.basicSword"), 10, Weapon.WeaponType.MELEE);
            Weapon secondBasicBow = new Weapon(Languages.t("item.basicBow"), 10, Weapon.WeaponType.RANGED);

            secondHero.addItem(secondBasicSword);
            secondHero.addItem(secondBasicBow);
            secondHero.equipWeapon(secondBasicSword);

            secondHeroModel = new HeroModel(secondHero);
            secondHeroModel.setPosition(heroModel.getX() + 55, heroModel.getY());

            secondHeroViewGUI = new HeroViewGUI();
        }

        LogWindowGUI logWindowGUI = new LogWindowGUI();
        RoomViewGUI roomViewGUI = new RoomViewGUI(logWindowGUI::append);

        GameViewGUI gameViewGUI = new GameViewGUI(stage, heroViewGUI, secondHeroViewGUI, roomViewGUI);
        gameViewGUI.setOnShowLogs(logWindowGUI::showWindow);

        gameViewGUI.setOnResetGame(() -> {
            gameViewGUI.hide();
            GameLauncher.startRandomGame(stage, safePlayerCount);
        });

        gameViewGUI.setOnSaveGame(() -> {
            logWindowGUI.append(Languages.t("game.saveNotReady"));
            logWindowGUI.showWindow();
        });

        gameViewGUI.setOnQuitToMenu(() -> {
            gameViewGUI.hide();
            MenuLauncher.showMainMenu(stage);
        });

        gameViewGUI.setOnQuitToDesktop(javafx.application.Platform::exit);

        gameViewGUI.setOnSettings(() -> {
            logWindowGUI.append(Languages.t("game.settingsNotReady"));
            logWindowGUI.showWindow();
        });

        new HeroController(heroModel, heroViewCLI, heroViewGUI);

        if (secondHeroModel != null && secondHeroViewGUI != null) {
            new HeroController(secondHeroModel, new HeroViewCLI(), secondHeroViewGUI);
        }

        RoomController roomController = new RoomController(
                roomModel,
                heroModel,
                secondHeroModel,
                roomViewCLI,
                roomViewGUI,
                () -> {
                    logWindowGUI.append(Languages.t("game.newLevel"));
                    logWindowGUI.showWindow();
                    gameViewGUI.hide();
                    GameLauncher.startRandomGame(stage, safePlayerCount, hero);
                });

        new GameController(new GameModel(), gameViewGUI, roomController, heroModel, secondHeroModel);

        heroViewGUI.setHeroName(hero.getName());
        heroModel.syncState();

        if (secondHeroModel != null && secondHeroViewGUI != null) {
            secondHeroViewGUI.setHeroName(secondHeroModel.getHero().getName());
            secondHeroModel.syncState();
        }

        roomController.onEnterRoom();

        gameViewGUI.show();
    }
}
