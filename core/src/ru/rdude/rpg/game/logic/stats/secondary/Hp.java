package ru.rdude.rpg.game.logic.stats.secondary;


import com.fasterxml.jackson.annotation.JsonIdentityReference;
import ru.rdude.rpg.game.logic.stats.Calculatable;
import ru.rdude.rpg.game.logic.stats.RoundStat;
import ru.rdude.rpg.game.logic.stats.Stat;
import ru.rdude.rpg.game.logic.stats.primary.Lvl;
import ru.rdude.rpg.game.logic.stats.primary.Vit;
import ru.rdude.rpg.game.utils.jsonextension.JsonPolymorphicSubType;

import static java.lang.Math.floor;

@JsonPolymorphicSubType("hp")
public class Hp extends Stat implements Calculatable, RoundStat {

    private boolean calculatable;
    @JsonIdentityReference(alwaysAsId = true)
    private Vit vit;
    @JsonIdentityReference(alwaysAsId = true)
    private Lvl lvl;

    private Recovery recovery;
    private Max max;

    private Hp() { }

    public Hp(double value) {
        super(value);
        this.calculatable = false;
        recovery = new Recovery(this);
        max = new Max(this);
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
        recovery = new Recovery(this);
        max = new Max(this);
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
    public double increase(double value) {
        if (value == 0) return this.value();
        this.value += value;
        if (this.value < 0 && calculatable) {
            this.value = 0;
        }
        else if (this.value > maxValue() && calculatable) {
            this.value = maxValue();
        }
        notifySubscribers();
        return this.value();
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

    @Override
    public double calculate() {
        if (!calculatable) return max.value();
        this.max.calculate();
        this.recovery.calculate();
        return max.value();
    }

    @JsonPolymorphicSubType("hpMax")
    public static class Max extends Stat implements Calculatable, RoundStat {

        @JsonIdentityReference(alwaysAsId = true)
        private Hp hp;

        private Max() { }

        public Max(Hp hp) {
            this.hp = hp;
        }

        @Override
        public double calculate() {
            if (!hp.calculatable) return value();
            double LVL = hp.lvl.value();
            double VIT = hp.vit.value();
            this.set(20 + LVL*5 + floor(LVL / 3) + floor(LVL / 5) + floor(LVL / 7) + VIT + floor(VIT / 3)*2 + floor(VIT / 5) + floor(VIT / 7)*2 + floor(VIT / 10));
            //this.set(20 + Math.floor(LVL * 0.7 + Math.floor(LVL / 2) * 0.7) + Math.floor(VIT * 0.7 + Math.floor(VIT / 2) * 0.7 + Math.floor(VIT / 3)));
            return value();
        }

        @Override
        public void set(double value) {
            double oldValue = this.value();
            super.set(value);
            if (hp.calculatable && (hp.value() > this.value() || hp.value() == oldValue)) {
                hp.set(this.value());
            }
        }

        @Override
        public void increaseBuffValue(String className, double value) {
            double oldValue = value();
            super.increaseBuffValue(className, value);
            if (hp.calculatable && (hp.value() > this.value() || hp.value() == oldValue)) {
                hp.set(this.value());
            }
        }

        @Override
        public void setCalculatable(boolean calculatable) {
        }

        @Override
        public String getName() {
            return "Maximum health";
        }
    }

    @JsonPolymorphicSubType("hpRecovery")
    public static class Recovery extends Stat implements Calculatable, RoundStat {

        @JsonIdentityReference(alwaysAsId = true)
        private Hp hp;

        private Recovery() { }

        public Recovery(Hp hp) {
            this.hp = hp;
        }

        @Override
        public double calculate() {
            if (!hp.calculatable) return value();
            double LVL = hp.lvl.value();
            double VIT = hp.vit.value();
            this.set(1 + floor(LVL / 2) + floor(VIT / 2) + floor(VIT / 3) + floor(VIT / 5)*2 + floor(VIT / 7));
            //this.set(1 + Math.floor(VIT / 3) + Math.floor(VIT / 7) + LVL);
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
