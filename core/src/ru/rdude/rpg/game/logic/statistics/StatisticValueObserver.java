package ru.rdude.rpg.game.logic.statistics;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import ru.rdude.rpg.game.logic.entities.beings.BeingAction;
import ru.rdude.rpg.game.logic.enums.UsedByStatistics;

@JsonIdentityInfo(generator = ObjectIdGenerators.UUIDGenerator.class)
public interface StatisticValueObserver {

    void update(BeingAction.Action action, UsedByStatistics statisticType, int times, double value);

}
