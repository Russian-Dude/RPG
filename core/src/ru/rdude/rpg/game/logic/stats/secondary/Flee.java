package ru.rdude.rpg.game.logic.stats.secondary;

import com.fasterxml.jackson.annotation.JsonIdentityReference;
import ru.rdude.rpg.game.logic.stats.RoundStat;
import ru.rdude.rpg.game.utils.Functions;
import ru.rdude.rpg.game.logic.stats.Calculatable;
import ru.rdude.rpg.game.logic.stats.Stat;
import ru.rdude.rpg.game.logic.stats.primary.Agi;
import ru.rdude.rpg.game.logic.stats.primary.Dex;
import ru.rdude.rpg.game.logic.stats.primary.Luck;
import ru.rdude.rpg.game.logic.stats.primary.Lvl;
import ru.rdude.rpg.game.utils.jsonextension.JsonPolymorphicSubType;

@JsonPolymorphicSubType("flee")
public class Flee extends Stat implements Calculatable, RoundStat {

    private boolean calculatable;
    @JsonIdentityReference(alwaysAsId = true)
    private Agi agi;
    @JsonIdentityReference(alwaysAsId = true)
    private Dex dex;
    @JsonIdentityReference(alwaysAsId = true)
    private Luck luck;
    @JsonIdentityReference(alwaysAsId = true)
    private Lvl lvl;

    private LuckyDodgeChance luckyDodgeChance;
    private FleeWithLuckyDodge fleeWithLuckyDodge;

    private Flee() { }

    public Flee(double value) {
        super(value);
        this.calculatable = false;
        luckyDodgeChance = new LuckyDodgeChance(this);
        fleeWithLuckyDodge = new FleeWithLuckyDodge(this);
    }

    @Override
    public String getName() {
        return "Flee";
    }

    public Flee(double value, Agi agi, Dex dex, Luck luck, Lvl lvl) {
        super(value);
        this.calculatable = false;
        this.agi = agi;
        this.dex = dex;
        this.luck = luck;
        this.lvl = lvl;
        agi.subscribe(this);
        dex.subscribe(this);
        luck.subscribe(this);
        lvl.subscribe(this);
        luckyDodgeChance = new LuckyDodgeChance(this);
        fleeWithLuckyDodge = new FleeWithLuckyDodge(this);
        luckyDodgeChance.calculate();
        fleeWithLuckyDodge.calculate();
    }

    public void setCalculatable(boolean calculatable) {
        this.calculatable = calculatable;
    }

    public LuckyDodgeChance luckyDodgeChance() { return luckyDodgeChance; }

    @Override
    public double increase(Stat stat) {
        super.increase(stat);
        if (stat instanceof Flee) {
            this.fleeWithLuckyDodge.increase(((Flee) stat).fleeWithLuckyDodge.value());
            this.luckyDodgeChance.increase(((Flee) stat).luckyDodgeChance.value());
        }
        return value();
    }

    public double valueWithLuckyDodgeChance() {
        float chance = Functions.random(100f);
        boolean isLuckyDodge = luckyDodgeChance.value() >= chance;
        return isLuckyDodge ? fleeWithLuckyDodge.value() : super.value();
    }

    @Override
    public double calculate() {
        if (!calculatable) return value();
        double AGI = agi.value();
        double DEX = dex.value();
        double LVL = lvl.value();
        this.set(Math.floor(AGI*0.5 + Math.floor(AGI/3) + Math.floor(AGI/5)*0.3 + Math.floor(AGI/7)*0.5
                + Math.floor(DEX/2)*0.2 + Math.floor(DEX/7)*0.2 + LVL*0.1 + Math.floor(LVL/4)*0.2));
        luckyDodgeChance.calculate();
        fleeWithLuckyDodge.calculate();
        return value();
    }

    @JsonPolymorphicSubType("LuckyDodgeChange")
    public static class LuckyDodgeChance extends Stat implements Calculatable {

        @JsonIdentityReference(alwaysAsId = true)
        private Flee flee;

        private LuckyDodgeChance() { }

        public LuckyDodgeChance(Flee flee) {
            this.flee = flee;
        }

        @Override
        public double calculate() {
            if (flee.calculatable) {
                double LUCK = flee.luck.value();
                double LVL = flee.lvl.value();
                this.set((LUCK*0.2 + LVL*0.1 + Math.floor(LUCK/2)*0.1 + Math.floor(LUCK/3)*0.1)
                        + Math.floor(LUCK/4)*0.1 + Math.floor(LUCK/5)*0.1);
            }
            return this.value();
        }

        @Override
        public void setCalculatable(boolean calculatable) {
        }

        @Override
        public String getName() {
            return "Lucky dodge chance";
        }
    }

    @JsonPolymorphicSubType("fleeWithLuckyDodge")
    public static class FleeWithLuckyDodge extends Stat implements Calculatable {

        @JsonIdentityReference(alwaysAsId = true)
        private Flee flee;

        private FleeWithLuckyDodge() { }

        public FleeWithLuckyDodge(Flee flee) {
            this.flee = flee;
        }

        @Override
        public double calculate() {
            if (flee.calculatable) {
                this.set(flee.value() * 1.8);
            }
            return value();
        }

        @Override
        public void setCalculatable(boolean calculatable) {
        }

        @Override
        public String getName() {
            return "Flee with lucky dodge";
        }
    }

}
