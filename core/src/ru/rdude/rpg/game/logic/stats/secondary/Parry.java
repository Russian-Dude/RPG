package ru.rdude.rpg.game.logic.stats.secondary;


import com.fasterxml.jackson.annotation.JsonIdentityReference;
import ru.rdude.rpg.game.logic.stats.Calculatable;
import ru.rdude.rpg.game.logic.stats.Stat;
import ru.rdude.rpg.game.logic.stats.primary.Agi;
import ru.rdude.rpg.game.logic.stats.primary.Dex;
import ru.rdude.rpg.game.logic.stats.primary.Lvl;
import ru.rdude.rpg.game.utils.jsonextension.JsonPolymorphicSubType;

@JsonPolymorphicSubType("parry")
public class Parry extends Stat implements Calculatable {

    private boolean calculatable;
    @JsonIdentityReference(alwaysAsId = true)
    private Agi agi;
    @JsonIdentityReference(alwaysAsId = true)
    private Dex dex;
    @JsonIdentityReference(alwaysAsId = true)
    private Lvl lvl;

    private Parry() { }

    public Parry(double value) {
        super(value);
        this.calculatable = false;
    }

    @Override
    public String getName() {
        return "Parry";
    }

    public Parry(double value, Agi agi, Dex dex, Lvl lvl) {
        super(value);
        this.calculatable = false;
        this.agi = agi;
        this.dex = dex;
        this.lvl = lvl;
        agi.subscribe(this);
        dex.subscribe(this);
        lvl.subscribe(this);
    }

    @Override
    public void setCalculatable(boolean calculatable) {
        this.calculatable = calculatable;
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
