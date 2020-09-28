package ru.rdude.rpg.game.logic.stats.secondary;


import ru.rdude.rpg.game.logic.stats.Calculatable;
import ru.rdude.rpg.game.logic.stats.Stat;
import ru.rdude.rpg.game.logic.stats.primary.*;

public class Stm extends Stat implements Calculatable {

    private boolean calculatable;
    private Agi agi;
    private Vit vit;
    private Dex dex;
    private Str str;
    private Lvl lvl;

    private Max max;
    // stamina recovery every turn
    private Recovery recovery;
    // stamina usage per hit
    private PerHit perHit;
    // how hard to use equipped items
    private Hardness hardness;


    public Stm(double value) {
        super(value);
        this.calculatable = false;
        max = new Max();
        recovery = new Recovery();
        perHit = new PerHit();
        hardness = new Hardness();
    }

    public Stm(double value, Agi agi, Vit vit, Dex dex, Str str, Lvl lvl) {
        super(value);
        this.calculatable = false;
        this.agi = agi;
        this.vit = vit;
        this.dex = dex;
        this.str = str;
        this.lvl = lvl;
        agi.subscribe(this);
        vit.subscribe(this);
        dex.subscribe(this);
        str.subscribe(this);
        lvl.subscribe(this);
        max = new Max();
        recovery = new Recovery();
        perHit = new PerHit();
        hardness = new Hardness();
        calculate();
    }

    @Override
    public void setCalculatable(boolean calculatable) {
        this.calculatable = calculatable;
    }

    public Recovery recovery() {
        return recovery;
    }

    public double recoveryValue() {
        return recovery.value();
    }

    public PerHit perHit() {
        return perHit;
    }

    public double perHitValue() {
        return perHit.value();
    }

    public Hardness hardness() {
        return hardness;
    }

    public Max max() {
        return max;
    }

    public double maxValue() {
        return max.value();
    }

    public void setMaxValue(double max) {
        this.max.set(max);
    }


    @Override
    public double increase(Stat stat) {
        if (!(stat instanceof Stm))
            return super.increase(stat);
        else {
            super.increase(stat);
            this.max.increase(((Stm) stat).max.value());
            this.recovery.increase(((Stm) stat).recovery.value());
            this.perHit.increase(((Stm) stat).perHit.value());
            this.hardness.increase(((Stm) stat).hardness.value());
            return value();
        }
    }

    // calculate max Stm, Stm per hit, Stm recovery. Return max Stm.
    @Override
    public double calculate() {
        if (!calculatable) return max.value();
        perHit.calculate();
        max.calculate();
        recovery.calculate();
        return max.value();
    }


    public class Max extends Stat implements Calculatable {
        @Override
        public double calculate() {
            if (!calculatable) return value();
            double STR = str.value();
            double DEX = dex.value();
            double LVL = lvl.value();
            this.set(STR + Math.floor(STR / 3) + Math.floor(STR / 5) + Math.floor(DEX / 7) + LVL + Math.floor(LVL / 5) + Math.floor(LVL / 10) + 8);
            return value();
        }

        @Override
        public void setCalculatable(boolean calculatable) {

        }
    }

    public class Recovery extends Stat implements Calculatable {
        @Override
        public double calculate() {
            if (!calculatable) return value();
            double LVL = lvl.value();
            double VIT = vit.value();
            this.set(Math.floor(VIT / 3) + Math.floor(VIT / 5) + LVL + Math.floor(LVL / 3) + Math.floor(LVL / 7) + 3);
            return value();
        }

        @Override
        public void setCalculatable(boolean calculatable) {

        }
    }

    public class PerHit extends Stat implements Calculatable {
        @Override
        public double calculate() {
            if (!calculatable) return value();
            double LVL = lvl.value();
            double AGI = agi.value();
            this.set(3 + hardness.value() - Math.floor(Math.floor(AGI / 3) * 0.4 + Math.floor(AGI / 5) + Math.floor(AGI / 7) * 0.5 + Math.floor(LVL / 7)));
            return value();
        }

        @Override
        public void setCalculatable(boolean calculatable) {

        }
    }

    public class Hardness extends Stat {
    }
}
