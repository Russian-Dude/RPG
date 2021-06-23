package ru.rdude.rpg.game.logic.stats.secondary;


import com.fasterxml.jackson.annotation.JsonIdentityReference;
import ru.rdude.rpg.game.logic.stats.Calculatable;
import ru.rdude.rpg.game.logic.stats.Stat;
import ru.rdude.rpg.game.logic.stats.primary.Lvl;
import ru.rdude.rpg.game.logic.stats.primary.Vit;
import ru.rdude.rpg.game.utils.jsonextension.JsonPolymorphicSubType;

@JsonPolymorphicSubType("def")
public class Def extends Stat implements Calculatable {

    private boolean calculatable;
    @JsonIdentityReference(alwaysAsId = true)
    private Vit vit;
    @JsonIdentityReference(alwaysAsId = true)
    private Lvl lvl;

    private Def() { }

    public Def(double value) {
        super(value);
        this.calculatable = false;
    }

    @Override
    public String getName() {
        return "Defence";
    }

    public Def(double value, Vit vit, Lvl lvl) {
        super(value);
        this.calculatable = false;
        this.vit = vit;
        this.lvl = lvl;
        vit.subscribe(this);
        lvl.subscribe(this);
    }

    public void setCalculatable(boolean calculatable) {
        this.calculatable = calculatable;
    }

    @Override
    public double calculate() {
        if (!calculatable) return value();
        double LVL = lvl.value();
        double VIT = vit.value();
        set(LVL + Math.floor(VIT/2) + Math.floor(VIT/3) + Math.floor(VIT/7));
        return value();
    }
}
