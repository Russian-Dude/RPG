package ru.rdude.rpg.game.logic.stats.secondary;


import ru.rdude.rpg.game.logic.stats.Calculatable;
import ru.rdude.rpg.game.logic.stats.Stat;
import ru.rdude.rpg.game.logic.stats.primary.Lvl;
import ru.rdude.rpg.game.logic.stats.primary.Vit;

public class Def extends Stat implements Calculatable {

    private boolean calculatable;
    private Vit vit;
    private Lvl lvl;

    public Def(double value) {
        super(value);
        this.calculatable = false;
    }

    public Def(double value, Vit vit, Lvl lvl) {
        super(value);
        this.calculatable = false;
        this.vit = vit;
        this.lvl = lvl;
        vit.subscribe(this);
        lvl.subscribe(this);
    }

    public void setCalculatable(boolean calculatable) {
        this.calculatable = calculatable;
    }

    @Override
    public double calculate() {
        if (!calculatable) return value();
        double LVL = lvl.value();
        double VIT = vit.value();
        set(LVL + Math.floor(VIT/2) + Math.floor(VIT/3) + Math.floor(VIT/7));
        return value();
    }
}
