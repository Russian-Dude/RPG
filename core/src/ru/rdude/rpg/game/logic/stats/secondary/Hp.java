package ru.rdude.rpg.game.logic.stats.secondary;


import ru.rdude.rpg.game.logic.stats.Calculatable;
import ru.rdude.rpg.game.logic.stats.Stat;
import ru.rdude.rpg.game.logic.stats.primary.Lvl;
import ru.rdude.rpg.game.logic.stats.primary.Vit;

public class Hp extends Stat implements Calculatable {

    private boolean calculatable;
    private Vit vit;
    private Lvl lvl;

    private Recovery recovery;
    private Max max;

    public Hp(double value) {
        super(value);
        this.calculatable = false;
        recovery = new Recovery();
        max = new Max();
    }

    @Override
    public String getName() {
        return "Health";
    }

    public Hp(double value, Vit vit, Lvl lvl) {
        super(value);
        this.calculatable = false;
        this.vit = vit;
        this.lvl = lvl;
        vit.subscribe(this);
        lvl.subscribe(this);
        recovery = new Recovery();
        max = new Max();
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
        if (!(stat instanceof Hp))
            return super.increase(stat);
        else {
            super.increase(stat);
            this.max.increase(((Hp) stat).max.value());
            this.recovery.increase(((Hp) stat).recovery.value());
        }
        return value();
    }

    // calculate max hp and recovery. Return max hp
    @Override
    public double calculate() {
        if (!calculatable) return max.value();
        this.max.calculate();
        this.recovery.calculate();
        return max.value();
    }

    public class Max extends Stat implements Calculatable {
        @Override
        public double calculate() {
            if (!calculatable) return value();
            double LVL = lvl.value();
            double VIT = vit.value();
            this.set(20 + Math.floor(LVL * 0.7 + Math.floor(LVL / 2) * 0.7) + Math.floor(VIT * 0.7 + Math.floor(VIT / 2) * 0.7 + Math.floor(VIT / 3)));
            return value();
        }

        @Override
        public void setCalculatable(boolean calculatable) {

        }

        @Override
        public String getName() {
            return "Maximum health";
        }
    }

    public class Recovery extends Stat implements Calculatable {
        @Override
        public double calculate() {
            if (!calculatable) return value();
            double LVL = lvl.value();
            double VIT = vit.value();
            this.set(1 + Math.floor(VIT / 3) + Math.floor(VIT / 7) + LVL);
            return value();
        }

        @Override
        public void setCalculatable(boolean calculatable) {

        }

        @Override
        public String getName() {
            return "Health recovery";
        }
    }
}
