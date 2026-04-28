package common.languages;

import java.util.HashMap;
import java.util.Map;

public final class Languages {

    public enum Language {
        FR("Français"),
        EN("English");

        private final String displayName;

        Language(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    private static Language current = Language.FR;

    private static final Map<Language, Map<String, String>> TRANSLATIONS = new HashMap<>();

    static {
        // ─────────────────────────────────────────────
        //  FRANÇAIS
        // ─────────────────────────────────────────────
        Map<String, String> fr = new HashMap<>();

        // ── Menu principal ──
        fr.put("menu.title", "JeuxQuiJeux");
        fr.put("menu.windowTitle", "JeuxQuiJeux - Menu principal");
        fr.put("menu.newGame", "Nouvelle partie");
        fr.put("menu.continue", "Continuer");
        fr.put("menu.createLevel", "Créer Niveau");
        fr.put("menu.settings", "Paramètres");
        fr.put("menu.quit", "Quitter le jeu");
        fr.put("menu.authors", "Auteurs : lucas, mathis, tom, leonard");
        fr.put("menu.highScores", "Meilleurs scores");
        fr.put("menu.noScores", "Aucun score pour le moment");

        // ── Fenêtre nouvelle partie ──
        fr.put("newGame.title", "Nouvelle partie");
        fr.put("newGame.1player", "1 joueur");
        fr.put("newGame.2players", "2 joueurs");
        fr.put("newGame.hint", "Clique sur une touche pour la modifier, puis appuie sur la nouvelle touche.");
        fr.put("newGame.resetDefault", "Touches par défaut");
        fr.put("newGame.resetDone", "Touches par défaut rétablies.");
        fr.put("newGame.start", "Lancer la partie");
        fr.put("newGame.cancel", "Annuler");
        fr.put("newGame.cancelCapture", "Modification annulée.");
        fr.put("newGame.forbiddenKey", "Touche non acceptée pour une action de jeu.");
        fr.put("newGame.keyModified", "Touche modifiée. Tu peux en modifier une autre ou lancer la partie.");
        fr.put("newGame.conflictP1", "Conflit dans les touches du joueur 1.");
        fr.put("newGame.conflictP2", "Conflit dans les touches du joueur 2.");
        fr.put("newGame.conflictBetween", "Conflit entre les touches du joueur 1 et du joueur 2.");
        fr.put("newGame.player1", "Joueur 1");
        fr.put("newGame.player2", "Joueur 2");
        fr.put("newGame.inventory", "Inventaire");
        fr.put("newGame.movement", "Mouvement");
        fr.put("newGame.interact", "Interagir");
        fr.put("newGame.forward", "Avancer");
        fr.put("newGame.backward", "Reculer");
        fr.put("newGame.moveRight", "Aller à droite");
        fr.put("newGame.moveLeft", "Aller à gauche");

        // ── Fenêtre paramètres ──
        fr.put("settings.title", "Paramètres");
        fr.put("settings.resolution", "Résolution");
        fr.put("settings.language", "Langue");
        fr.put("settings.apply", "Appliquer");
        fr.put("settings.close", "Fermer");

        // ── Menu pause ──
        fr.put("pause.title", "PAUSE");
        fr.put("pause.continue", "Continuer");
        fr.put("pause.reset", "Recommencer");
        fr.put("pause.save", "Sauvegarder");
        fr.put("pause.quit", "Quitter");
        fr.put("pause.quitDesktop", "Quitter le jeu");
        fr.put("pause.settings", "Paramètres");

        // ── Game Over ──
        fr.put("gameOver.title", "GAME OVER");
        fr.put("gameOver.subtitle", "Un héros est mort.");
        fr.put("gameOver.restart", "Recommencer");
        fr.put("gameOver.quitMenu", "Retour au menu");
        fr.put("gameOver.quitDesktop", "Quitter le jeu");

        // ── HUD en jeu ──
        fr.put("game.inventoryTitle.p1", "Inventaire J1");
        fr.put("game.inventoryTitle.p2", "Inventaire J2");
        fr.put("game.inventoryEmpty", "Inventaire vide");
        fr.put("game.weaponInfo", "Arme équipée");
        fr.put("game.weaponNone", "aucune");
        fr.put("game.damage", "dégâts");
        fr.put("game.emptySlot", "(vide)");
        fr.put("game.equipped", "[équipée]");
        fr.put("game.logs", "Logs");
        fr.put("game.statsHero", "Stats hero");

        // ── Messages de jeu ──
        fr.put("game.saveNotReady", "La sauvegarde n'est pas encore implémentée.");
        fr.put("game.settingsNotReady", "Les paramètres du menu pause seront connectés plus tard.");
        fr.put("game.newLevel", "Nouveau niveau aléatoire généré.");
        fr.put("game.continueNotReady", "Continuer sera ajouté plus tard.");
        fr.put("game.createLevelNotReady", "Créer Niveau sera ajouté plus tard.");

        // ── Items ──
        fr.put("item.basicSword", "Épée basique");
        fr.put("item.basicBow", "Arc basique");
        fr.put("item.healingScroll", "Parchemin de soin");
        fr.put("item.woodenChest", "Coffre en bois");
        fr.put("item.chestDesc", "Un petit coffre rempli de butin");
        fr.put("item.ruby", "Rubis");
        fr.put("item.rubyDesc", "Une gemme rouge brillante");
        fr.put("item.coin", "Pièce");
        fr.put("item.coinDesc", "Une vieille pièce d'or");
        fr.put("item.goldenKey", "Clé dorée");
        fr.put("item.goldenKeyDesc", "Une clé pour une porte spéciale");

        // ── Héros ──
        fr.put("hero.name", "Héros");
        fr.put("hero.name2", "Héros 2");
        fr.put("hero.backpack", "Sac à dos");
        fr.put("hero.backpackP2", "Sac à dos J2");

        // ── Messages d'interaction items ──
        fr.put("item.cannotTake", "%s ne peut pas être pris.");
        fr.put("item.alreadyInInventory", "%s est déjà dans l'inventaire.");
        fr.put("item.notInRoom", "%s n'est pas dans cette salle.");
        fr.put("item.inventoryFull", "Inventaire plein.");
        fr.put("item.heroTakes", "%s prend %s.");
        fr.put("item.nothingHappens", "Rien ne se passe.");
        fr.put("item.cannotDrop", "%s ne peut pas être lâché.");
        fr.put("item.notInInventory", "%s pas dans l'inventaire.");
        fr.put("item.heroDrops", "%s lâche %s.");
        fr.put("item.cannotUse", "%s ne peut pas être utilisé.");
        fr.put("item.youOpen", "Vous ouvrez %s.");
        fr.put("item.takeFirst", "Prenez %s d'abord.");
        fr.put("item.alreadyApplied", "%s est déjà appliqué tant que porté par le héros.");
        fr.put("item.youUse", "Vous utilisez %s.");
        fr.put("item.nothingToInteract", "Il n'y a rien avec quoi interagir.");
        fr.put("item.nothingNearby", "Rien à proximité pour interagir.");

        // ── HeroModel - inventory use ──
        fr.put("hero.invalidSlot", "Case invalide.");
        fr.put("hero.noItemInSlot", "Aucun objet dans cette case.");
        fr.put("hero.weaponEquipped", "Arme équipée : %s | dégâts : %d");
        fr.put("hero.cannotEquip", "Impossible d'équiper cette arme.");
        fr.put("hero.spellUsed", "Sort utilisé : %s | HP : %d/%d");
        fr.put("hero.noEffect", "Aucun effet : %s");

        // ── Combat ──
        fr.put("combat.noWeapon", "%s n'a aucune arme équipée.");
        fr.put("combat.shootsArrow", "%s tire une flèche sur %s.");
        fr.put("combat.arrowHits", "La flèche touche %s pour %d dégâts.");
        fr.put("combat.meleeAttack", "%s attaque %s avec %s pour %d dégâts.");
        fr.put("combat.defeated", "%s est vaincu.");
        fr.put("combat.killReward", "%s récompense : +10%% PV régénérés et +20%% dégâts. PV : %d/%d | Dégâts : %d.");
        fr.put("combat.dropped", "%s a lâché %s.");
        fr.put("combat.bossHP", "Boss room nettoyée ! Récompense : +%d PV max. PV actuels : %d/%d.");
        fr.put("combat.bossDamage", "Boss room nettoyée ! Récompense : +%d dégâts de base. Dégâts actuels : %d.");

        // ── Directions ──
        fr.put("dir.north", "nord");
        fr.put("dir.south", "sud");
        fr.put("dir.east", "est");
        fr.put("dir.west", "ouest");

        // ── Noms de salles (DungeonGenerator) ──
        fr.put("room.Entrance", "Entrée");
        fr.put("room.Corridor", "Couloir");
        fr.put("room.Vault", "Caveau");
        fr.put("room.Chapel", "Chapelle");
        fr.put("room.Armory", "Armurerie");
        fr.put("room.Library", "Bibliothèque");
        fr.put("room.Storage", "Réserve");
        fr.put("room.Barracks", "Caserne");
        fr.put("room.Hall", "Grande salle");
        fr.put("room.Cellar", "Cave");
        fr.put("room.Kitchen", "Cuisine");
        fr.put("room.Workshop", "Atelier");
        fr.put("room.Gallery", "Galerie");
        fr.put("room.Watchtower", "Tour de guet");
        fr.put("room.Crypt", "Crypte");

        // ── Descriptions de salles ──
        fr.put("room.entranceDesc", "Le début du donjon.");
        fr.put("room.desc.0", "Une salle froide et silencieuse.");
        fr.put("room.desc.1", "La poussière recouvre le sol.");
        fr.put("room.desc.2", "Vous entendez de l'eau goutter quelque part.");
        fr.put("room.desc.3", "Les murs sont fissurés et anciens.");
        fr.put("room.desc.4", "Un sentiment de malaise emplit l'air.");
        fr.put("room.desc.5", "Il n'y a presque rien ici.");
        fr.put("room.desc.6", "Un endroit oublié du donjon.");
        fr.put("room.desc.7", "L'atmosphère est étrangement calme.");

        // ── Clé du boss ──
        fr.put("item.bossKey", "Clé du boss");
        fr.put("item.bossKeyDesc", "Une clé pour la salle du boss");

        // ── Vue de la salle (GUI) ──
        fr.put("map.minimap", "Minicarte");
        fr.put("map.exits", "Sorties : %s");
        fr.put("map.exitsNone", "Sorties : aucune");
        fr.put("map.noItems", "Aucun objet dans cette salle");
        fr.put("map.heroLabel1", "Héros 1");
        fr.put("map.heroLabel2", "Héros 2");

        // ── CLI ──
        fr.put("cli.exits", "Sorties");
        fr.put("cli.exitsNone", "Sorties : aucune");
        fr.put("cli.items", "Objets");
        fr.put("cli.itemsNone", "Objets : aucun");
        fr.put("cli.youGo", "Vous allez vers %s et entrez dans : %s");
        fr.put("cli.noExit", "Il n'y a pas de sortie vers %s.");
        fr.put("cli.inventory", "Inventaire");
        fr.put("cli.inventoryEmpty", "Inventaire : vide");
        fr.put("cli.health", "Santé");
        fr.put("cli.goodbye", "Au revoir.");
        fr.put("cli.unknownCommand", "Commande inconnue. Tapez aide.");

        // ── Noms de touches (clavier FR) ──
        fr.put("key.numpad", "Pavé");
        fr.put("key.enter", "Entrée");
        fr.put("key.space", "Espace");

        // ── GUI labels ──
        fr.put("gui.heroLabel", "Héros : ");
        fr.put("gui.locationLabel", "Lieu : ");
        fr.put("gui.itemLabel", "Objet : ");

        TRANSLATIONS.put(Language.FR, fr);

        // ─────────────────────────────────────────────
        //  ENGLISH
        // ─────────────────────────────────────────────
        Map<String, String> en = new HashMap<>();

        // ── Main menu ──
        en.put("menu.title", "JeuxQuiJeux");
        en.put("menu.windowTitle", "JeuxQuiJeux - Main Menu");
        en.put("menu.newGame", "New Game");
        en.put("menu.continue", "Continue");
        en.put("menu.createLevel", "Create Level");
        en.put("menu.settings", "Settings");
        en.put("menu.quit", "Quit Game");
        en.put("menu.authors", "Authors: lucas, mathis, tom, leonard");
        en.put("menu.highScores", "High Scores");
        en.put("menu.noScores", "No scores yet");

        // ── New game window ──
        en.put("newGame.title", "New Game");
        en.put("newGame.1player", "1 player");
        en.put("newGame.2players", "2 players");
        en.put("newGame.hint", "Click on a key to modify it, then press the new key.");
        en.put("newGame.resetDefault", "Default keys");
        en.put("newGame.resetDone", "Default keys restored.");
        en.put("newGame.start", "Start game");
        en.put("newGame.cancel", "Cancel");
        en.put("newGame.cancelCapture", "Modification cancelled.");
        en.put("newGame.forbiddenKey", "Key not accepted for a game action.");
        en.put("newGame.keyModified", "Key modified. You can modify another one or start the game.");
        en.put("newGame.conflictP1", "Key conflict for player 1.");
        en.put("newGame.conflictP2", "Key conflict for player 2.");
        en.put("newGame.conflictBetween", "Key conflict between player 1 and player 2.");
        en.put("newGame.player1", "Player 1");
        en.put("newGame.player2", "Player 2");
        en.put("newGame.inventory", "Inventory");
        en.put("newGame.movement", "Movement");
        en.put("newGame.interact", "Interact");
        en.put("newGame.forward", "Forward");
        en.put("newGame.backward", "Backward");
        en.put("newGame.moveRight", "Move right");
        en.put("newGame.moveLeft", "Move left");

        // ── Settings window ──
        en.put("settings.title", "Settings");
        en.put("settings.resolution", "Resolution");
        en.put("settings.language", "Language");
        en.put("settings.apply", "Apply");
        en.put("settings.close", "Close");

        // ── Pause menu ──
        en.put("pause.title", "PAUSE");
        en.put("pause.continue", "Continue");
        en.put("pause.reset", "Reset");
        en.put("pause.save", "Save");
        en.put("pause.quit", "Quit");
        en.put("pause.quitDesktop", "Quit to desktop");
        en.put("pause.settings", "Settings");

        // ── Game Over ──
        en.put("gameOver.title", "GAME OVER");
        en.put("gameOver.subtitle", "A hero is dead.");
        en.put("gameOver.restart", "Restart");
        en.put("gameOver.quitMenu", "Quit to menu");
        en.put("gameOver.quitDesktop", "Quit to desktop");

        // ── In-game HUD ──
        en.put("game.inventoryTitle.p1", "Inventory P1");
        en.put("game.inventoryTitle.p2", "Inventory P2");
        en.put("game.inventoryEmpty", "Inventory empty");
        en.put("game.weaponInfo", "Equipped weapon");
        en.put("game.weaponNone", "none");
        en.put("game.damage", "damage");
        en.put("game.emptySlot", "(empty)");
        en.put("game.equipped", "[equipped]");
        en.put("game.logs", "Logs");
        en.put("game.statsHero", "Hero stats");

        // ── Game messages ──
        en.put("game.saveNotReady", "Save is not implemented yet.");
        en.put("game.settingsNotReady", "Settings from pause menu will be connected later.");
        en.put("game.newLevel", "New random level generated.");
        en.put("game.continueNotReady", "Continue will be added later.");
        en.put("game.createLevelNotReady", "Create Level will be added later.");

        // ── Items ──
        en.put("item.basicSword", "Basic Sword");
        en.put("item.basicBow", "Basic Bow");
        en.put("item.healingScroll", "Healing Scroll");
        en.put("item.woodenChest", "Wooden Chest");
        en.put("item.chestDesc", "A small chest full of loot");
        en.put("item.ruby", "Ruby");
        en.put("item.rubyDesc", "A shiny red gem");
        en.put("item.coin", "Coin");
        en.put("item.coinDesc", "An old gold coin");
        en.put("item.goldenKey", "Golden Key");
        en.put("item.goldenKeyDesc", "A key to a special door");

        // ── Hero ──
        en.put("hero.name", "Hero");
        en.put("hero.name2", "Hero 2");
        en.put("hero.backpack", "Backpack");
        en.put("hero.backpackP2", "Backpack P2");

        // ── Item interaction messages ──
        en.put("item.cannotTake", "%s cannot be taken.");
        en.put("item.alreadyInInventory", "%s is already in inventory.");
        en.put("item.notInRoom", "%s is not in the current room.");
        en.put("item.inventoryFull", "Inventory is full.");
        en.put("item.heroTakes", "%s takes %s.");
        en.put("item.nothingHappens", "Nothing happens.");
        en.put("item.cannotDrop", "%s cannot be dropped.");
        en.put("item.notInInventory", "%s not in the inventory.");
        en.put("item.heroDrops", "%s drops %s.");
        en.put("item.cannotUse", "%s cannot be used.");
        en.put("item.youOpen", "You open %s.");
        en.put("item.takeFirst", "Take %s first.");
        en.put("item.alreadyApplied", "%s is already applied while carried by the hero.");
        en.put("item.youUse", "You use %s.");
        en.put("item.nothingToInteract", "There is nothing to interact with.");
        en.put("item.nothingNearby", "Nothing nearby to interact with.");

        // ── HeroModel - inventory use ──
        en.put("hero.invalidSlot", "Invalid slot.");
        en.put("hero.noItemInSlot", "No item in this slot.");
        en.put("hero.weaponEquipped", "Weapon equipped: %s | damage: %d");
        en.put("hero.cannotEquip", "Cannot equip this weapon.");
        en.put("hero.spellUsed", "Spell used: %s | HP: %d/%d");
        en.put("hero.noEffect", "No effect: %s");

        // ── Combat ──
        en.put("combat.noWeapon", "%s has no weapon equipped.");
        en.put("combat.shootsArrow", "%s shoots an arrow at %s.");
        en.put("combat.arrowHits", "Arrow hits %s for %d damage.");
        en.put("combat.meleeAttack", "%s attacks %s with %s for %d damage.");
        en.put("combat.defeated", "%s is defeated.");
        en.put("combat.killReward", "%s kill reward: +10%% HP regenerated and +20%% damage. HP: %d/%d | Damage: %d.");
        en.put("combat.dropped", "%s dropped %s.");
        en.put("combat.bossHP", "Boss room cleared! Reward: +%d max HP. Current HP: %d/%d.");
        en.put("combat.bossDamage", "Boss room cleared! Reward: +%d base damage. Current damage: %d.");

        // ── Directions ──
        en.put("dir.north", "north");
        en.put("dir.south", "south");
        en.put("dir.east", "east");
        en.put("dir.west", "west");

        // ── Room names (DungeonGenerator) ──
        en.put("room.Entrance", "Entrance");
        en.put("room.Corridor", "Corridor");
        en.put("room.Vault", "Vault");
        en.put("room.Chapel", "Chapel");
        en.put("room.Armory", "Armory");
        en.put("room.Library", "Library");
        en.put("room.Storage", "Storage");
        en.put("room.Barracks", "Barracks");
        en.put("room.Hall", "Hall");
        en.put("room.Cellar", "Cellar");
        en.put("room.Kitchen", "Kitchen");
        en.put("room.Workshop", "Workshop");
        en.put("room.Gallery", "Gallery");
        en.put("room.Watchtower", "Watchtower");
        en.put("room.Crypt", "Crypt");

        // ── Room descriptions ──
        en.put("room.entranceDesc", "The beginning of the dungeon.");
        en.put("room.desc.0", "A cold and silent room.");
        en.put("room.desc.1", "Dust covers the floor.");
        en.put("room.desc.2", "You hear water dripping somewhere.");
        en.put("room.desc.3", "The walls are cracked and old.");
        en.put("room.desc.4", "An uneasy feeling fills the air.");
        en.put("room.desc.5", "There is almost nothing here.");
        en.put("room.desc.6", "A forgotten place of the dungeon.");
        en.put("room.desc.7", "The atmosphere is strangely calm.");

        // ── Boss key ──
        en.put("item.bossKey", "Boss Key");
        en.put("item.bossKeyDesc", "A key to the boss room");

        // ── Room view (GUI) ──
        en.put("map.minimap", "Minimap");
        en.put("map.exits", "Exits: %s");
        en.put("map.exitsNone", "Exits: none");
        en.put("map.noItems", "No item in this room");
        en.put("map.heroLabel1", "Hero 1");
        en.put("map.heroLabel2", "Hero 2");

        // ── CLI ──
        en.put("cli.exits", "Exits");
        en.put("cli.exitsNone", "Exits: none");
        en.put("cli.items", "Items");
        en.put("cli.itemsNone", "Items: none");
        en.put("cli.youGo", "You go %s and enter: %s");
        en.put("cli.noExit", "There is no exit to the %s.");
        en.put("cli.inventory", "Inventory");
        en.put("cli.inventoryEmpty", "Inventory: empty");
        en.put("cli.health", "Health");
        en.put("cli.goodbye", "Goodbye.");
        en.put("cli.unknownCommand", "Unknown command. Type help.");

        // ── Key names (EN keyboard) ──
        en.put("key.numpad", "Numpad");
        en.put("key.enter", "Enter");
        en.put("key.space", "Space");

        // ── GUI labels ──
        en.put("gui.heroLabel", "Hero: ");
        en.put("gui.locationLabel", "Location: ");
        en.put("gui.itemLabel", "Item: ");

        TRANSLATIONS.put(Language.EN, en);
    }

    private Languages() {
    }

    /** Change la langue courante. */
    public static void setLanguage(Language lang) {
        if (lang != null) {
            current = lang;
        }
    }

    /** @return la langue courante. */
    public static Language getLanguage() {
        return current;
    }

    /**
     * Retourne la traduction de la clé dans la langue courante.
     * Si la clé n'existe pas, retourne la clé elle-même.
     */
    public static String t(String key) {
        Map<String, String> map = TRANSLATIONS.get(current);
        if (map != null && map.containsKey(key)) {
            return map.get(key);
        }
        return key;
    }

    /**
     * Retourne la traduction avec arguments formatés (String.format).
     * Exemple : {@code I18n.tf("item.heroTakes", heroName, itemName)}
     */
    public static String tf(String key, Object... args) {
        return String.format(t(key), args);
    }

    /**
     * Traduit un nom de direction interne ("north", "south", "east", "west")
     * en texte affiché dans la langue courante.
     */
    public static String dir(String direction) {
        return t("dir." + direction);
    }
}
