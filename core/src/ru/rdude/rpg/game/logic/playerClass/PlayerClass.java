package ru.rdude.rpg.game.logic.playerClass;

import ru.rdude.rpg.game.logic.data.PlayerClassData;
import ru.rdude.rpg.game.logic.entities.beings.BeingAction;
import ru.rdude.rpg.game.logic.entities.beings.Player;
import ru.rdude.rpg.game.logic.enums.UsedByStatistics;
import ru.rdude.rpg.game.logic.statistics.StatisticValueObserver;
import ru.rdude.rpg.game.logic.stats.primary.Lvl;
import ru.rdude.rpg.game.utils.Pair;

import java.util.HashMap;
import java.util.Map;

public class PlayerClass implements StatisticValueObserver {

    private final PlayerClassData classData;
    private final Map<BeingAction.Action, Map<UsedByStatistics, Pair<Double, Double>>> pointCoefficients;
    private final Player player;
    private Lvl lvl;
    private double needToOpen;
    private boolean open;

    public PlayerClass(PlayerClassData classData, Player player) {
        this.classData = classData;
        this.player = player;
        lvl = new Lvl(Lvl.CLASS);
        open = false;
        needToOpen = 10000;

        pointCoefficients = new HashMap<>();
        classData.getOpenRequirements().forEach(req -> {
            pointCoefficients.putIfAbsent(req.getBeingAction(), new HashMap<>());
            pointCoefficients.get(req.getBeingAction())
                    .put(req.getStatisticType(), new Pair<>(req.getPointsForEachUse(), req.getPointsForValue()));
        });
    }


    @Override
    public void update(BeingAction.Action action, UsedByStatistics statisticType, int times, double value) {
        Map<UsedByStatistics, Pair<Double, Double>> beingActionPoints = pointCoefficients.get(action);
        if (beingActionPoints == null) {
            return;
        }
        Pair<Double, Double> statisticTypePoints = beingActionPoints.get(statisticType);
        if (statisticTypePoints == null) {
            return;
        }
        needToOpen -= times * statisticTypePoints.getFirst();
        needToOpen -= value * statisticTypePoints.getSecond();
        if (needToOpen <= 0d) {
            open = true;
        }
    }
}
