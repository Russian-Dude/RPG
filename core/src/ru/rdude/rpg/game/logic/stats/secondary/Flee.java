package ru.rdude.rpg.game.logic.stats.secondary;

import ru.rdude.rpg.game.utils.Functions;
import ru.rdude.rpg.game.logic.stats.Calculatable;
import ru.rdude.rpg.game.logic.stats.Stat;
import ru.rdude.rpg.game.logic.stats.primary.Agi;
import ru.rdude.rpg.game.logic.stats.primary.Dex;
import ru.rdude.rpg.game.logic.stats.primary.Luck;
import ru.rdude.rpg.game.logic.stats.primary.Lvl;

public class Flee extends Stat implements Calculatable {

    private boolean calculatable;
    private Agi agi;
    private Dex dex;
    private Luck luck;
    private Lvl lvl;

    private LuckyDodgeChance luckyDodgeChance;
    private FleeWithLuckyDodge fleeWithLuckyDodge;

    public Flee(double value) {
        super(value);
        this.calculatable = false;
        luckyDodgeChance = new LuckyDodgeChance();
        fleeWithLuckyDodge = new FleeWithLuckyDodge();
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
        luckyDodgeChance = new LuckyDodgeChance();
        fleeWithLuckyDodge = new FleeWithLuckyDodge();
        luckyDodgeChance.calculate();
        fleeWithLuckyDodge.calculate();
    }

    public void setCalculatable(boolean calculatable) {
        this.calculatable = calculatable;
    }

    public double pureValue() {
        return value();
    }

    public LuckyDodgeChance luckyDodgeChance() { return luckyDodgeChance; }

    @Override
    public double increase(Stat stat) {
        super.increase(stat);
        if (stat instanceof Flee) {
            this.fleeWithLuckyDodge.increase(((Flee) stat).fleeWithLuckyDodge.value());
            this.luckyDodgeChance.increase(((Flee) stat).luckyDodgeChance.value());
        }
        return pureValue();
    }

    @Override
    public double value() {
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

    public class LuckyDodgeChance extends Stat implements Calculatable {
        @Override
        public double calculate() {
            double LUCK = luck.value();
            double LVL = lvl.value();
            this.set((LUCK*0.2 + LVL*0.1 + Math.floor(LUCK/2)*0.1 + Math.floor(LUCK/3)*0.1)
                    + Math.floor(LUCK/4)*0.1 + Math.floor(LUCK/5)*0.1);
            return this.value();
        }

        @Override
        public void setCalculatable(boolean calculatable) {

        }


    }

    private class FleeWithLuckyDodge extends Stat implements Calculatable {
        @Override
        public double calculate() {
            this.set(Flee.this.value() * 1.8);
            return value();
        }

        @Override
        public void setCalculatable(boolean calculatable) {

        }
    }

}
