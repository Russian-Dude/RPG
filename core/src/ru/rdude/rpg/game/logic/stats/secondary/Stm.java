package ru.rdude.rpg.game.logic.stats.secondary;


import com.fasterxml.jackson.annotation.JsonIdentityReference;
import ru.rdude.rpg.game.logic.stats.Calculatable;
import ru.rdude.rpg.game.logic.stats.Stat;
import ru.rdude.rpg.game.logic.stats.primary.*;
import ru.rdude.rpg.game.utils.jsonextension.JsonPolymorphicSubType;

@JsonPolymorphicSubType("stm")
public class Stm extends Stat implements Calculatable {

    private boolean calculatable;
    @JsonIdentityReference(alwaysAsId = true)
    private Agi agi;
    @JsonIdentityReference(alwaysAsId = true)
    private Vit vit;
    @JsonIdentityReference(alwaysAsId = true)
    private Dex dex;
    @JsonIdentityReference(alwaysAsId = true)
    private Str str;
    @JsonIdentityReference(alwaysAsId = true)
    private Lvl lvl;

    private Max max;
    // stamina recovery every turn
    private Recovery recovery;
    // stamina usage per hit
    private PerHit perHit;
    // how hard to use equipped items
    private Hardness hardness;

    private Stm() { }

    public Stm(double value) {
        super(value);
        this.calculatable = false;
        max = new Max(this);
        recovery = new Recovery(this);
        perHit = new PerHit(this);
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
        max = new Max(this);
        recovery = new Recovery(this);
        perHit = new PerHit(this);
        hardness = new Hardness();
        calculate();
    }

    @Override
    public String getName() {
        return "Stamina";
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


    @JsonPolymorphicSubType("stmMax")
    public static class Max extends Stat implements Calculatable {

        @JsonIdentityReference(alwaysAsId = true)
        private Stm stm;

        private Max() { }

        public Max(Stm stm) {
            this.stm = stm;
        }

        @Override
        public double calculate() {
            if (!stm.calculatable) return value();
            double STR = stm.str.value();
            double DEX = stm.dex.value();
            double LVL = stm.lvl.value();
            this.set(STR + Math.floor(STR / 3) + Math.floor(STR / 5) + Math.floor(DEX / 7) + LVL + Math.floor(LVL / 5) + Math.floor(LVL / 10) + 8);
            return value();
        }

        @Override
        public void set(double value) {
            double oldValue = this.value();
            super.set(value);
            if (stm.calculatable && (stm.value() > this.value() || stm.value() == oldValue)) {
                stm.set(this.value());
            }
        }

        @Override
        public void setCalculatable(boolean calculatable) {
        }

        @Override
        public String getName() {
            return "Maximum stamina";
        }
    }

    @JsonPolymorphicSubType("stmRecovery")
    public static class Recovery extends Stat implements Calculatable {

        @JsonIdentityReference(alwaysAsId = true)
        private Stm stm;

        private Recovery() { }

        public Recovery(Stm stm) {
            this.stm = stm;
        }

        @Override
        public double calculate() {
            if (!stm.calculatable) return value();
            double LVL = stm.lvl.value();
            double VIT = stm.vit.value();
            this.set(Math.floor(VIT / 3) + Math.floor(VIT / 5) + Math.floor(LVL / 2) + Math.floor(LVL / 3) + Math.floor(LVL / 7) + 3);
            return value();
        }

        @Override
        public void setCalculatable(boolean calculatable) {
        }

        @Override
        public String getName() {
            return "Stamina recovery";
        }
    }

    @JsonPolymorphicSubType("stmPerHit")
    public static class PerHit extends Stat implements Calculatable {

        @JsonIdentityReference(alwaysAsId = true)
        private Stm stm;

        private PerHit() { }

        public PerHit(Stm stm) {
            this.stm = stm;
        }

        @Override
        public double calculate() {
            if (!stm.calculatable) return value();
            double LVL = stm.lvl.value();
            double AGI = stm.agi.value();
            this.set(3 + stm.hardness.value() - Math.floor(Math.floor(AGI / 3) * 0.4 + Math.floor(AGI / 5) + Math.floor(AGI / 7) * 0.5 + Math.floor(LVL / 7)));
            return value();
        }

        @Override
        public void setCalculatable(boolean calculatable) {
        }

        @Override
        public String getName() {
            return "Stamina to hit";
        }
    }

    @JsonPolymorphicSubType("stmHardness")
    public static class Hardness extends Stat {
        @Override
        public String getName() {
            return "Hardness";
        }
    }
}
