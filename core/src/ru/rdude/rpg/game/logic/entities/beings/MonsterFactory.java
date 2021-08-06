package ru.rdude.rpg.game.logic.entities.beings;

import ru.rdude.rpg.game.logic.data.MonsterData;
import ru.rdude.rpg.game.logic.gameStates.Map;
import ru.rdude.rpg.game.logic.map.Cell;
import ru.rdude.rpg.game.logic.map.Point;
import ru.rdude.rpg.game.logic.stats.Bonus;
import ru.rdude.rpg.game.utils.Functions;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public final class MonsterFactory {

    public Monster createMonster(Map.MonstersOnCell.Monster monster) {
        // TODO: 06.08.2021 create actual monster
        /*
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
         */
        return null;
    }

    public Map.MonstersOnCell.Monster createMonsterOnCell(int level, MonsterData data) {
        return new Map.MonstersOnCell.Monster(data.getGuid(), level);
    }

    public Party createParty(Map.MonstersOnCell monsters) {
        // TODO: 06.08.2021 create party when battle starts
        return null;
    }

    public Map.MonstersOnCell createMonstersOnCell(Cell cell) {
        int amount = Functions.random(1, 5);
        int minLvl = Math.max(1, cell.getLvl() - amount - 1);
        int maxLvl = cell.getLvl() + (5 - amount);
        return MonsterData.getMonsters().values().stream()
                // filter by describer
                .filter(data -> !data.isDescriber())
                // filter monsters by biom and relief
                .filter(data -> data.getSpawnBioms().contains(cell.getBiom())
                        && data.getSpawnReliefs().contains(cell.getRelief()))
                // filter monsters by level
                .filter(data -> {
                    final double monsterMin = data.getMinLvl();
                    final double monsterMax = data.getMaxLvl();
                    return !((monsterMin < minLvl && monsterMax < minLvl) || (monsterMin > maxLvl && monsterMax > maxLvl));
                })
                // pick random data
                .collect(Collectors.collectingAndThen(Collectors.toList(), list -> {
                    List<MonsterData> res = new ArrayList<>();
                    if (!list.isEmpty()) {
                        for (int i = 0; i < amount; i++) {
                            res.add(Functions.random(list));
                        }
                    }
                    return res.stream();
                }))
                // create monsters from data with available for each monster level
                .map(data -> {
                    final int max = Math.min((int) data.getMaxLvl(), maxLvl);
                    final int min = Math.max((int) data.getMinLvl(), minLvl);
                    int lvl = Functions.random(min, max);
                    return createMonsterOnCell(lvl, data);
                })
                // collect
                .collect(Collectors.collectingAndThen(Collectors.toList(), Map.MonstersOnCell::new));
    }

    public void createMonstersOnMap(Map map) {
        int amount = (map.getGameMap().getWidth() * map.getGameMap().getHeight()) / 8;
        final List<Point> points = new ArrayList<>();
        for (int x = 0; x < map.getGameMap().getWidth(); x++) {
            for (int y = 0; y < map.getGameMap().getHeight(); y++) {
                points.add(new Point(x, y));
            }
        }
        while (amount > 0 && !points.isEmpty()) {
            final Point randomPoint = Functions.random(points);
            points.remove(randomPoint);
            final Map.MonstersOnCell monstersOnCell = createMonstersOnCell(map.getGameMap().cell(randomPoint));
            if (monstersOnCell != null && !monstersOnCell.isEmpty()) {
                map.getCellProperties(map.getGameMap().cell(randomPoint)).setMonsters(monstersOnCell);
                amount--;
            }
        }
    }
}
