package ru.rdude.rpg.game.logic.stats.secondary;


import ru.rdude.rpg.game.logic.stats.Calculatable;
import ru.rdude.rpg.game.logic.stats.Stat;
import ru.rdude.rpg.game.logic.stats.primary.*;

public class MagicResistance extends Stat implements Calculatable {

    private boolean calculatable;
    private Int intel;
    private Luck luck;
    private Str str;
    private Dex dex;
    private Agi agi;
    private Vit vit;
    private Lvl lvl;

    public MagicResistance(double value) {
        super(value);
        this.calculatable = false;
    }

    @Override
    public String getName() {
        return "Magic resistance";
    }

    public MagicResistance(double value, Int intel, Luck luck, Str str, Dex dex, Agi agi, Vit vit, Lvl lvl) {
        super(value);
        this.calculatable = false;
        this.intel = intel;
        this.luck = luck;
        this.str = str;
        this.dex = dex;
        this.agi = agi;
        this.vit = vit;
        this.lvl = lvl;
        intel.subscribe(this);
        luck.subscribe(this);
        str.subscribe(this);
        dex.subscribe(this);
        agi.subscribe(this);
        vit.subscribe(this);
        lvl.subscribe(this);
    }

    @Override
    public void setCalculatable(boolean calculatable) {
        this.calculatable = calculatable;
    }

    @Override
    public double calculate() {
        if (!calculatable) return value();
        double INT = intel.value();
        double LVL = lvl.value();
        double LUCK = luck.value();
        double STR = str.value();
        double DEX = dex.value();
        double AGI = agi.value();
        double VIT = vit.value();
        set(INT*0.2 + Math.floor(INT/3)*0.2 + Math.floor(INT/4)*0.1 + LVL*0.1 + LUCK*0.05 + Math.floor(STR/7 + DEX/7 + AGI/7 + VIT/7)*0.1);
        return value();
    }
}
