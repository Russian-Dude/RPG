package ru.rdude.rpg.game.logic.stats.secondary;


import ru.rdude.rpg.game.logic.stats.Calculatable;
import ru.rdude.rpg.game.logic.stats.Stat;
import ru.rdude.rpg.game.logic.stats.primary.Agi;
import ru.rdude.rpg.game.logic.stats.primary.Dex;
import ru.rdude.rpg.game.logic.stats.primary.Lvl;

public class Parry extends Stat implements Calculatable {

    private boolean calculatable;
    private Agi agi;
    private Dex dex;
    private Lvl lvl;

    public Parry(double value) {
        super(value);
        this.calculatable = false;
    }

    public Parry(double value, Agi agi, Dex dex, Lvl lvl) {
        super(value);
        this.calculatable = true;
        this.agi = agi;
        this.dex = dex;
        this.lvl = lvl;
        agi.subscribe(this);
        dex.subscribe(this);
        lvl.subscribe(this);
    }

    @Override
    public double calculate() {
        if (!calculatable) return value();
        double AGI = agi.value();
        double DEX = dex.value();
        double LVL = lvl.value();
        set(Math.floor(AGI*0.5 + Math.floor(DEX/3) + Math.floor(AGI/5)*0.5 + Math.floor(AGI/2)*0.1 + LVL*0.1 + Math.floor(LVL/3)*0.2));
        return value();
    }
}
