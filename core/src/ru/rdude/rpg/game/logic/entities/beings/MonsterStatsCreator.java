package ru.rdude.rpg.game.logic.entities.beings;

import ru.rdude.rpg.game.logic.enums.StatName;
import ru.rdude.rpg.game.logic.stats.Bonus;
import ru.rdude.rpg.game.logic.stats.primary.Lvl;
import ru.rdude.rpg.game.logic.stats.secondary.Hp;
import ru.rdude.rpg.game.logic.stats.secondary.Stm;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class MonsterStatsCreator {

    private final List<StatName> defaultPattern;

    public MonsterStatsCreator() {
        defaultPattern = Arrays.stream(StatName.values())
                .filter(StatName::isPrimary)
                .collect(Collectors.toList());
    }

    public void create(Monster monster, int toLvl) {
        if (monster.stats.lvl().value() == toLvl) {
            return;
        }

        double mainLvl = monster.stats.lvlValue();
        monster.stats.lvl().set(toLvl);

        final List<StatName> pattern = monster.getEntityData().getStatsPattern().isEmpty()
                ? defaultPattern : monster.getEntityData().getStatsPattern();

        // stats
        int statPoints = (toLvl * 2) + (toLvl / 3) + (toLvl / 10) * 2;
        for (int patternIndex = 0; statPoints > 0; statPoints--) {
            monster.stats.get(pattern.get(patternIndex)).increase(1.0);
            patternIndex = patternIndex < pattern.size() - 1 ? patternIndex + 1 : 0;
        }

        // stat bonuses
        monster.stats.forEachWithNestedStats(stat -> {
            if (!(stat instanceof Lvl)) {
                double bonus = stat.getBuffValue(Bonus.class);
                if (bonus != 0) {
                    double bonusPerLvl = bonus / mainLvl;
                    stat.setBuffValue(Bonus.class, bonusPerLvl * toLvl);
                    if (stat instanceof Hp.Max) {
                        monster.stats.hp().set(stat.value());
                    }
                    else if (stat instanceof Stm.Max) {
                        monster.stats.stm().set(stat.value());
                    }
                }
            }
        });

    }

}
