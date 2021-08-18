package ru.rdude.rpg.game.logic.stats.primary;


import ru.rdude.rpg.game.logic.stats.RoundStat;
import ru.rdude.rpg.game.logic.stats.Stat;
import ru.rdude.rpg.game.utils.jsonextension.JsonPolymorphicSubType;

@JsonPolymorphicSubType("luck")
public class Luck extends Stat implements RoundStat {
    @Override
    public String getName() {
        return "Luck";
    }
}
