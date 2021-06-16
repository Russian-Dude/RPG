package ru.rdude.rpg.game.logic.data;

import ru.rdude.rpg.game.logic.entities.beings.BeingAction;
import ru.rdude.rpg.game.logic.enums.UsedByStatistics;

import java.util.*;

public class PlayerClassData extends EntityData {

    private static Map<Long, PlayerClassData> classes = new HashMap<>();

    private final Set<PlayerClassOpenRequirement<?>> openRequirements = new HashSet<>();
    private final Set<Long> abilities = new HashSet<>();

    PlayerClassData() {
    }

    public PlayerClassData(long guid) {
        super(guid);
    }

    public static void storeClasses(Collection<PlayerClassData> collection) {
        collection.forEach(classData -> classes.put(classData.getGuid(), classData));
    }

    public Set<PlayerClassOpenRequirement<?>> getOpenRequirements() {
        return openRequirements;
    }

    public Set<Long> getAbilities() {
        return abilities;
    }

    @Override
    public boolean hasEntityDependency(long guid) {
        return abilities.contains(guid);
    }

    @Override
    public void replaceEntityDependency(long oldValue, long newValue) {
        abilities.remove(oldValue);
        abilities.add(newValue);
    }

    public static class PlayerClassOpenRequirement<T extends UsedByStatistics> {

        private final BeingAction.Action beingAction;
        private final T statisticType;
        private final double pointsForValue;
        private final double pointsForEachUse;

        public PlayerClassOpenRequirement(BeingAction.Action beingAction, T statisticType, double pointsForValue, double pointsForEachUse) {
            this.beingAction = beingAction;
            this.statisticType = statisticType;
            this.pointsForValue = pointsForValue;
            this.pointsForEachUse = pointsForEachUse;
        }

        public BeingAction.Action getBeingAction() {
            return beingAction;
        }

        public T getStatisticType() {
            return statisticType;
        }

        public double getPointsForValue() {
            return pointsForValue;
        }

        public double getPointsForEachUse() {
            return pointsForEachUse;
        }
    }
}
