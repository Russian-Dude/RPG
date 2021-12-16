package ru.rdude.rpg.game.logic.entities.beings;

import ru.rdude.rpg.game.logic.data.MonsterData;
import ru.rdude.rpg.game.logic.gameStates.Map;
import ru.rdude.rpg.game.logic.map.Cell;
import ru.rdude.rpg.game.logic.map.Point;
import ru.rdude.rpg.game.logic.stats.Bonus;
import ru.rdude.rpg.game.logic.stats.primary.Lvl;
import ru.rdude.rpg.game.logic.stats.secondary.Hp;
import ru.rdude.rpg.game.logic.stats.secondary.Stm;
import ru.rdude.rpg.game.utils.Functions;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public final class MonsterFactory {

    private MonsterStatsCreator monsterStatsCreator = new MonsterStatsCreator();

    public Minion createMinion(long guid, Integer turnsDuration, Integer timeDuration, Being<?> master) {
        return createMinion(MonsterData.getMonsterByGuid(guid), turnsDuration, timeDuration, master);
    }

    public Minion createMinion(MonsterData monsterData, Integer turnsDuration, Integer timeDuration, Being<?> master) {
        final MonsterData realData = describerToReal(monsterData);
        return createMinion(
                realData,
                Math.max((int) monsterData.getMinLvl(), (int) realData.getMinLvl()), Math.min((int) monsterData.getMaxLvl(), (int) realData.getMaxLvl()),
                turnsDuration,
                timeDuration,
                master);
    }

    public Minion createMinion(MonsterData monsterData, int lvlMin, int lvlMax, Integer turnsDuration, Integer timeDuration, Being<?> master) {
        final MonsterData realData = describerToReal(monsterData);
        if (realData == monsterData) {
            return createMinion(monsterData, Functions.random(lvlMin, lvlMax), turnsDuration, timeDuration, master);
        }
        else {
            return createMinion(
                    realData,
                    Functions.random(Math.max((int) monsterData.getMinLvl(), (int) realData.getMinLvl()), Math.min((int) monsterData.getMaxLvl(), (int) realData.getMaxLvl())),
                    timeDuration,
                    turnsDuration,
                    master);
        }
    }

    public Minion createMinion(MonsterData monsterData, int lvl, Integer turnsDuration, Integer timeDuration, Being<?> master) {
        final MonsterData realData = describerToReal(monsterData);
        Minion minion = new Minion(monsterData, turnsDuration, timeDuration, master);
        // if there is no need to calculate stats based on level
        if (minion.stats.lvl().value() == lvl) {
            return minion;
        }
        // calculate stats
        double mainLvl = minion.stats.lvlValue();
        minion.stats.lvl().set(lvl);
        minion.stats.forEachWithNestedStats(stat -> {
            if (!(stat instanceof Lvl)) {
                double bonus = stat.getBuffValue(Bonus.class);
                if (bonus != 0) {
                    double bonusPerLvl = bonus / mainLvl;
                    stat.setBuffValue(Bonus.class, bonusPerLvl * lvl);
                    if (stat instanceof Hp.Max) {
                        minion.stats.hp().set(stat.value());
                    }
                    else if (stat instanceof Stm.Max) {
                        minion.stats.stm().set(stat.value());
                    }
                }
            }
        });
        return minion;
    }

    public Monster createMonster(long guid) {
        return createMonster(MonsterData.getMonsterByGuid(guid));
    }

    public Monster createMonster(MonsterData monsterData) {
        final MonsterData realData = describerToReal(monsterData);
        return createMonster(realData, Math.max((int) monsterData.getMinLvl(), (int) realData.getMinLvl()), Math.min((int) monsterData.getMaxLvl(), (int) realData.getMaxLvl()));
    }

    public Monster createMonster(MonsterData monsterData, int lvlMin, int lvlMax) {
        final MonsterData realData = describerToReal(monsterData);
        if (realData == monsterData) {
            return createMonster(monsterData, Functions.random(lvlMin, lvlMax));
        }
        else {
            return createMonster(realData, Functions.random(Math.max((int) monsterData.getMinLvl(), (int) realData.getMinLvl()), Math.min((int) monsterData.getMaxLvl(), (int) realData.getMaxLvl())));
        }
    }

    public Monster createMonster(MonsterData monsterData, int lvl) {
        final MonsterData realData = describerToReal(monsterData);
        Monster monster = new Monster(MonsterData.getMonsterByGuid(realData.getGuid()));
        // if there is no need to calculate stats based on level
        if (monster.stats.lvl().value() == lvl) {
            return monster;
        }
        // calculate stats
        monsterStatsCreator.create(monster, lvl);
        return monster;
    }

    public Monster createMonster(Map.MonstersOnCell.Monster monsterOnCell) {
        return createMonster(MonsterData.getMonsterByGuid(monsterOnCell.guid), monsterOnCell.lvl);
    }

    public Map.MonstersOnCell.Monster createMonsterOnCell(int level, MonsterData data) {
        return new Map.MonstersOnCell.Monster(data.getGuid(), level);
    }

    public Party createParty(Map.MonstersOnCell monsters) {
        return monsters.getMonsters().stream()
                .map(this::createMonster)
                .collect(Collectors.collectingAndThen(Collectors.toList(), Party::new));
    }

    public Map.MonstersOnCell createMonstersOnCell(Cell cell) {
        int amount = Functions.random(1, 6);
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
            if (map.getGameMap().cell(randomPoint).getObject() != null
                    || map.getGameMap().cell(randomPoint) == map.getPlayerPosition()) {
                continue;
            }
            final Map.MonstersOnCell monstersOnCell = createMonstersOnCell(map.getGameMap().cell(randomPoint));
            if (monstersOnCell != null && !monstersOnCell.isEmpty()) {
                map.getCellProperties(map.getGameMap().cell(randomPoint)).setMonsters(monstersOnCell);
                amount--;
            }
        }
    }

    public MonsterData describerToReal(MonsterData monsterData) {
        if (!monsterData.isDescriber()) {
            return monsterData;
        }
        return MonsterData.getMonsters().values().stream()
                // filter out describers
                .filter(data -> !data.isDescriber())
                // levels
                .filter(data -> {
                    final double dataMin = data.getMinLvl();
                    final double dataMax = data.getMaxLvl();
                    final double minLvl = monsterData.getMinLvl();
                    final double maxLvl = monsterData.getMaxLvl();
                    return !((dataMin < minLvl && dataMax < minLvl) || (dataMin > maxLvl && dataMax > maxLvl));
                })
                // attack type
                .filter(data -> monsterData.getDefaultAttackType() == null || data.getDefaultAttackType() == monsterData.getDefaultAttackType())
                // size
                .filter(data -> monsterData.getSize() == null || data.getSize() == monsterData.getSize())
                // elements
                .filter(data -> monsterData.getElements() == null
                                || monsterData.getElements().isEmpty()
                                || data.getElements().containsAll(monsterData.getElements()))
                // types
                .filter(data -> monsterData.getBeingTypes() == null
                                || monsterData.getBeingTypes().isEmpty()
                                || data.getBeingTypes().containsAll(monsterData.getBeingTypes()))
                // spawn biomes
                .filter(data -> monsterData.getSpawnBioms() == null
                                || monsterData.getSpawnBioms().isEmpty()
                                || data.getSpawnBioms().containsAll(monsterData.getSpawnBioms()))
                // spawn reliefs
                .filter(data -> monsterData.getSpawnReliefs() == null
                        || monsterData.getSpawnReliefs().isEmpty()
                        || data.getSpawnReliefs().containsAll(monsterData.getSpawnReliefs()))
                .collect(Functions.randomCollector());
    }
}
