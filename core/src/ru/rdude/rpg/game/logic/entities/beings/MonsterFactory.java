package ru.rdude.rpg.game.logic.entities.beings;

import ru.rdude.rpg.game.logic.data.MonsterData;
import ru.rdude.rpg.game.logic.enums.Biom;
import ru.rdude.rpg.game.logic.enums.Relief;
import ru.rdude.rpg.game.logic.map.Cell;
import ru.rdude.rpg.game.logic.stats.Bonus;
import ru.rdude.rpg.game.utils.Functions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public final class MonsterFactory {

    public Monster create(int level, MonsterData data) {
        Monster monster = new Monster(data);
        // if there is no need to calculate stats based on level
        if (monster.stats.lvl().value() == level) {
            return monster;
        }
        // calculate stats
        double mainLvl = monster.stats.lvlValue();
        monster.stats.lvl().set(level);
        monster.stats.forEachWithNestedStats(stat -> {
            double bonus = stat.getBuffValue(Bonus.class);
            if (bonus != 0) {
                double bonusPerLvl = bonus / mainLvl;
                stat.setBuffValue(Bonus.class, bonusPerLvl * level);
            }
        });
        return monster;
    }


    public Party createParty(Cell cell) {
        int amount = Functions.random(1, 5);
        int minLvl = Math.max(1, cell.getLvl() - amount - 1);
        int maxLvl = cell.getLvl() + (5 - amount);
        return MonsterData.getMonsters().values().stream()
                // filter by describer
                .filter(data -> !data.isDescriber())
                // filter monsters by biom and relief
                .filter(data -> data.getSpawnBioms().contains(Biom.ofCellProperty(cell.getBiom()))
                        && data.getSpawnReliefs().contains(Relief.ofCellProperty(cell.getRelief())))
                // filter monsters by level
                .filter(data -> {
                    int lvl = (int) data.getMainLvl();
                    return lvl >= minLvl && lvl <= maxLvl;
                })
                // pick random data
                .collect(Collectors.collectingAndThen(Collectors.toList(), list -> {
                    List<MonsterData> res = new ArrayList<>();
                    for (int i = 0; i < amount; i++) {
                        res.add(Functions.random(list));
                    }
                    return res.stream();
                }))
                // create monsters from data with available for each monster level
                .map(data -> {
                    int lvl = Functions.random(Math.max((int) data.getMinLvl(), minLvl), Math.min((int) data.getMaxLvl(), maxLvl));
                    return create(lvl, data);
                })
                // collect
                .collect(Collectors.collectingAndThen(Collectors.toList(), Party::new));
    }
}
