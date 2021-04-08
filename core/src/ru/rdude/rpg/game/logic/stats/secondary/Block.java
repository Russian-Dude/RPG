package ru.rdude.rpg.game.logic.stats.secondary;


import ru.rdude.rpg.game.logic.stats.Calculatable;
import ru.rdude.rpg.game.logic.stats.Stat;
import ru.rdude.rpg.game.logic.stats.primary.Agi;
import ru.rdude.rpg.game.logic.stats.primary.Dex;
import ru.rdude.rpg.game.logic.stats.primary.Lvl;

public class Block extends Stat implements Calculatable {

    private boolean calculatable;
    private Dex dex;
    private Agi agi;
    private Lvl lvl;

    public Block(double value) {
        super(value);
        this.calculatable = false;
    }

    @Override
    public String getName() {
        return "Block";
    }

    public Block(Dex dex, Agi agi, Lvl lvl) {
        this(0, dex, agi, lvl);
    }

    public Block(double value, Dex dex, Agi agi, Lvl lvl) {
        super(value);
        this.calculatable = false;
        this.dex = dex;
        this.agi = agi;
        this.lvl = lvl;
        dex.subscribe(this);
        agi.subscribe(this);
        lvl.subscribe(this);
    }

    public void setCalculatable(boolean calculatable) {
        this.calculatable = calculatable;
    }

    @Override
    public double calculate() {
        if (!calculatable) return value();
        double DEX = dex.value();
        double AGI = agi.value();
        double LVL = lvl.value();
        this.set(DEX*0.5 + Math.floor(DEX/3) + Math.floor(DEX/5)*0.5 + Math.floor(DEX/7)*0.6
                + Math.floor(AGI/2)*0.1 + LVL*0.1 + Math.floor(LVL/3)*0.2);
        return value();
    }
}
