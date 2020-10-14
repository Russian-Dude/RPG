package ru.rdude.rpg.game.logic.stats.secondary;


import ru.rdude.rpg.game.logic.stats.Calculatable;
import ru.rdude.rpg.game.logic.stats.Stat;
import ru.rdude.rpg.game.logic.stats.primary.Agi;
import ru.rdude.rpg.game.logic.stats.primary.Dex;
import ru.rdude.rpg.game.logic.stats.primary.Lvl;

public class Hit extends Stat implements Calculatable {

    private boolean calculatable;
    private Dex dex;
    private Agi agi;
    private Lvl lvl;

    public Hit(double value) {
        super(value);
        this.calculatable = false;
    }

    @Override
    public String getName() {
        return "Hit";
    }

    public Hit(double value, Dex dex, Agi agi, Lvl lvl) {
        super(value);
        this.calculatable = false;
        this.dex = dex;
        this.agi = agi;
        this.lvl = lvl;
        dex.subscribe(this);
        agi.subscribe(this);
        lvl.subscribe(this);
    }

    @Override
    public void setCalculatable(boolean calculatable) {
        this.calculatable = calculatable;
    }

    @Override
    public double calculate() {
        if (!calculatable) return value();
        double DEX = dex.value();
        double AGI = agi.value();
        double LVL = lvl.value();
        set(Math.floor(15 + DEX*1.5 + Math.floor(DEX/2) + Math.floor(DEX/5)*1.2 + AGI*0.3 + Math.floor(AGI/4) + LVL + Math.floor(LVL/3)));
        return value();
    }
}
