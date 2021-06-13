package ru.rdude.rpg.game.logic.statistics;

import ru.rdude.rpg.game.logic.entities.beings.BeingAction;
import ru.rdude.rpg.game.logic.enums.UsedByStatistics;

public interface StatisticValueObserver {

    void update(BeingAction.Action action, UsedByStatistics statisticType, int times, double value);

}
