package entity;

import game.*;

public class Boss extends Monster{
    private final int maxHp;
    private boolean hasRegenerated;

    public Boss(String name, int hp) {
        super(name, hp);
        this.maxHp = hp;          // PV max = PV de départ
        this.hasRegenerated = false;
    }

    /**
     * si le boss est à <= 50% de sa vie max,
     * et qu'il n'a pas encore régénéré,
     * il regagne 30% de sa vie max (une seule fois).
     */
    public void maybeRegen() {
        int current = getHp();

        // mort -> pas de résurrection
        if (current <= 0) {
            return;
        }

        // déjà régénéré -> on ne refait pas
        if (hasRegenerated) {
            return;
        }

        // condition : à la moitié ou moins de sa vie max
        if (current <= maxHp / 2) {
            int regenAmount = (int) (maxHp * 0.3); // 30% des PV max
            int newHp = current + regenAmount;

            if (newHp > maxHp) {
                newHp = maxHp; // on ne dépasse pas les PV max
            }

            setHp(newHp);
            hasRegenerated = true;

            System.out.println(getName() + " regenerates " + regenAmount
                    + " HP! (" + current + " -> " + newHp + ")");
        }
    }

   

}
