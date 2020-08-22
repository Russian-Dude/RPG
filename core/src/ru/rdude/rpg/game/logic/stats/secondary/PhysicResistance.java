package ru.rdude.rpg.game.logic.stats.secondary;


import ru.rdude.rpg.game.logic.stats.Calculatable;
import ru.rdude.rpg.game.logic.stats.Stat;
import ru.rdude.rpg.game.logic.stats.primary.*;

public class PhysicResistance extends Stat implements Calculatable {

    private boolean calculatable;
    private Int intel;
    private Luck luck;
    private Str str;
    private Dex dex;
    private Agi agi;
    private Vit vit;
    private Lvl lvl;

    public PhysicResistance(double value) {
        super(value);
        this.calculatable = false;
    }

    public PhysicResistance(double value, Int intel, Luck luck, Str str, Dex dex, Agi agi, Vit vit, Lvl lvl) {
        super(value);
        this.calculatable = true;
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
    public double calculate() {
        if (!calculatable) return value();
        double INT = intel.value();
        double LVL = lvl.value();
        double LUCK = luck.value();
        double STR = str.value();
        double DEX = dex.value();
        double AGI = agi.value();
        double VIT = vit.value();
        set(VIT*0.2 + Math.floor(VIT/3)*0.2 + Math.floor(VIT/4)*0.1 + LVL*0.1 + LUCK*0.05 + Math.floor(STR/7 + DEX/7 + AGI/7 + INT/7)*0.1);
        return value();
    }
}
