package game;

import java.util.List;

import entity.Hero;

public class HealCommand extends Command {

    public HealCommand() {
        super("HEAL");
    }

    @Override
    public void execute(Game g, List<String> args) {
        Hero h = g.getHero();
        h.useHealSpell();
    }
}
