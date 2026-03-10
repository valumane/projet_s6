package app;

import entity.controller.HeroController;
import entity.model.HeroModel;
import entity.model.Bag;
import map.Room;
import entity.view.HeroView;
import entity.view.HeroViewCLI;
import entity.view.HeroViewNull;
import entity.model.HealSpell;

public class MainHero {

    public static void main(String[] args) {

        //Adapte ces 2 lignes à TES constructeurs réels
        Room start = new Room("entrance");      // ou new BasicRoom(...)
        Bag bag = new Bag();                    // ou new Bag(capacity)

        HeroModel hero = new HeroModel("Hero", 100, bag, start);

        HeroView v1 = new HeroViewCLI();
        HeroView v2 = new HeroViewNull(); // GUI plus tard

        HeroController heroController = new HeroController(hero, v1, v2);

        // Test rapide
        heroController.onUseHealSpell();            // -> "You don't know any healing spell."
        heroController.onLearnSpell(new HealSpell(40)); // -> "You receive a healing power."
        heroController.onUseHealSpell();            // -> "You use your healing power !"
    }
}