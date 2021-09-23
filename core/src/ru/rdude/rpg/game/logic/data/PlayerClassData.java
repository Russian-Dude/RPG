package ru.rdude.rpg.game.logic.data;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import ru.rdude.rpg.game.logic.data.resources.PlayerClassResources;
import ru.rdude.rpg.game.logic.entities.beings.BeingAction;
import ru.rdude.rpg.game.logic.enums.AttackType;
import ru.rdude.rpg.game.logic.enums.UsedByStatistics;
import ru.rdude.rpg.game.utils.jsonextension.JsonPolymorphicSubType;

import java.util.*;
import java.util.stream.Collectors;

@JsonPolymorphicSubType("playerClassData")
public class PlayerClassData extends EntityData {

    private static Map<Long, PlayerClassData> classes = new HashMap<>();

    private Set<PlayerClassOpenRequirement> openRequirements = new HashSet<>();
    private long requiredPoints = 0;
    private Set<AbilityEntry> abilities = new HashSet<>();
    private Set<Long> startItems = new HashSet<>();
    private long defaultMeleeAttack = 0;
    private long defaultRangeAttack = 0;
    private long defaultMagicAttack = 0;

    PlayerClassData() {
    }

    public PlayerClassData(long guid) {
        super(guid);
        setResources(new PlayerClassResources());
    }

    public static void storeClasses(Collection<PlayerClassData> collection) {
        collection.forEach(classData -> classes.put(classData.getGuid(), classData));
    }

    public static PlayerClassData getClassByGuid(long guid) {
        return classes.get(guid);
    }

    public static Map<Long, PlayerClassData> getClasses() {
        return classes;
    }

    public Set<PlayerClassOpenRequirement> getOpenRequirements() {
        return openRequirements;
    }

    public Set<AbilityEntry> getAbilityEntries() {
        return abilities;
    }

    public Set<Long> getAbilities() {
        return abilities.stream()
                .map(AbilityEntry::getAbilityData)
                .collect(Collectors.toSet());
    }

    public long getRequiredPoints() {
        return requiredPoints;
    }

    public void setRequiredPoints(long requiredPoints) {
        this.requiredPoints = requiredPoints;
    }

    public Set<Long> getStartItems() {
        return startItems;
    }

    public void setStartItems(Set<Long> startItems) {
        this.startItems = startItems;
    }

    public long getDefaultMeleeAttack() {
        return defaultMeleeAttack;
    }

    public void setDefaultMeleeAttack(long defaultMeleeAttack) {
        this.defaultMeleeAttack = defaultMeleeAttack;
    }

    public long getDefaultRangeAttack() {
        return defaultRangeAttack;
    }

    public void setDefaultRangeAttack(long defaultRangeAttack) {
        this.defaultRangeAttack = defaultRangeAttack;
    }

    public long getDefaultMagicAttack() {
        return defaultMagicAttack;
    }

    public void setDefaultMagicAttack(long defaultMagicAttack) {
        this.defaultMagicAttack = defaultMagicAttack;
    }

    public long getDefaultAttack(AttackType attackType) {
        switch (attackType) {
            case MELEE:
                return getDefaultMeleeAttack();
            case RANGE:
                return getDefaultRangeAttack();
            case MAGIC:
                return getDefaultMagicAttack();
            case WEAPON_TYPE:
            default:
                throw new IllegalArgumentException("getDefaultAttack method required concrete attack type");
        }
    }

    @Override
    public PlayerClassResources getResources() {
        return (PlayerClassResources) super.getResources();
    }

    @Override
    public boolean hasEntityDependency(long guid) {
        return abilities.stream()
                .anyMatch(abilityEntry -> abilityEntry.abilityData == guid);
    }

    @Override
    public void replaceEntityDependency(long oldValue, long newValue) {
        for (AbilityEntry ability : abilities) {
            if (ability.abilityData == oldValue) {
                ability.abilityData = newValue;
            }
        }
    }

    public static class AbilityEntry {

        private long abilityData;
        private Map<Long, Integer> requirements = new HashMap<>();

        private AbilityEntry() {}

        public AbilityEntry(long abilityData) {
            this.abilityData = abilityData;
        }

        public long getAbilityData() {
            return abilityData;
        }

        public void setAbilityData(long abilityData) {
            this.abilityData = abilityData;
        }

        public Map<Long, Integer> getRequirements() {
            return requirements;
        }

        public void setRequirements(Map<Long, Integer> requirements) {
            this.requirements = requirements;
        }

        public void addRequirement(long guid, int lvl) {
            this.requirements.put(guid, lvl);
        }

        public void removeRequirement(long guid) {
            this.requirements.remove(guid);
        }
    }

    public static class PlayerClassOpenRequirement {

        private final BeingAction.Action beingAction;
        private final UsedByStatistics statisticType;
        private final double pointsForValue;
        private final double pointsForEachUse;

        @JsonCreator
        public PlayerClassOpenRequirement(@JsonProperty("beingAction") BeingAction.Action beingAction,
                                          @JsonProperty("statisticType") UsedByStatistics statisticType,
                                          @JsonProperty("pointsForValue") double pointsForValue,
                                          @JsonProperty("pointsForEachUse") double pointsForEachUse) {
            this.beingAction = beingAction;
            this.statisticType = statisticType;
            this.pointsForValue = pointsForValue;
            this.pointsForEachUse = pointsForEachUse;
        }

        public BeingAction.Action getBeingAction() {
            return beingAction;
        }

        public UsedByStatistics getStatisticType() {
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
