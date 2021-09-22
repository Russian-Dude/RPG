package ru.rdude.rpg.game.logic.playerClass;

import com.fasterxml.jackson.annotation.*;
import ru.rdude.rpg.game.logic.data.AbilityData;
import ru.rdude.rpg.game.logic.data.PlayerClassData;
import ru.rdude.rpg.game.logic.entities.beings.BeingAction;
import ru.rdude.rpg.game.logic.entities.beings.Player;
import ru.rdude.rpg.game.logic.enums.UsedByStatistics;
import ru.rdude.rpg.game.logic.statistics.StatisticValueObserver;
import ru.rdude.rpg.game.logic.stats.primary.Lvl;
import ru.rdude.rpg.game.utils.Pair;
import ru.rdude.rpg.game.utils.SubscribersManager;
import ru.rdude.rpg.game.utils.jsonextension.JsonPolymorphicSubType;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@JsonIdentityInfo(generator = ObjectIdGenerators.UUIDGenerator.class)
@JsonPolymorphicSubType("playerClass")
public class PlayerClass implements StatisticValueObserver, AbilityObserver {

    private SubscribersManager<PlayerClassObserver> subscribers = new SubscribersManager<>();

    private final PlayerClassData classData;
    private Set<Ability> abilities;
    private Lvl lvl;
    private long needToOpen;

    @JsonCreator
    private PlayerClass(@JsonProperty("classData") long guid) {
        classData = PlayerClassData.getClassByGuid(guid);
    }

    public PlayerClass(PlayerClassData classData) {
        this.classData = classData;
        lvl = new Lvl(Lvl.CLASS);
        lvl.setCalculatable(true);
        lvl.calculate();
        needToOpen = classData.getRequiredPoints();
        // create abilities
        abilities = classData.getAbilities().stream()
                .map(AbilityData::getAbilityByGuid)
                .map(Ability::new)
                .collect(Collectors.toSet());
        // subscribe to abilities
        abilities.forEach(ability -> ability.subscribe(this));
        // open first abilities
        classData.getAbilityEntries().stream()
                .filter(abilityEntry -> abilityEntry.getRequirements().isEmpty())
                .forEach(abilityEntry -> abilities.stream()
                        .filter(ability -> abilityEntry.getAbilityData() == ability.getAbilityData().getGuid())
                        .findAny()
                        .ifPresent(ability -> ability.setOpen(true)));
    }

    @JsonProperty("classData")
    private long getClassDataJson() {
        return classData.getGuid();
    }

    public boolean isOpen() {
        return needToOpen <= 0;
    }

    public PlayerClassData getClassData() {
        return classData;
    }

    public Set<Ability> getAbilities() {
        return abilities;
    }

    public Lvl getLvl() {
        return lvl;
    }

    public long getNeedToOpen() {
        return needToOpen;
    }

    public void setNeedToOpen(long needToOpen) {
        long before = this.needToOpen;
        this.needToOpen = needToOpen < 0 ? 0 : needToOpen;
        if (before != this.needToOpen) {
            subscribers.notifySubscribers(sub -> sub.updatePlayerClass(this));
        }
    }

    public void subscribe(PlayerClassObserver subscriber) {
        subscribers.subscribe(subscriber);
    }

    public void unsubscribe(PlayerClassObserver subscriber) {
        subscribers.unsubscribe(subscriber);
    }

    @Override
    public void update(BeingAction.Action action, UsedByStatistics statisticType, int times, double value) {
        classData.getOpenRequirements().stream()
                .filter(requirement -> requirement.getBeingAction().equals(action))
                .filter(requirement -> requirement.getStatisticType().equals(statisticType))
                .forEach(requirement -> setNeedToOpen((long) (needToOpen - (requirement.getPointsForEachUse() * times + requirement.getPointsForValue() * value))));
    }

    @Override
    public void updateAbility(Ability ability, boolean open, int oldLvl, int newLvl) {
        classData.getAbilityEntries().stream()
                .filter(abilityEntry -> abilityEntry.getRequirements().containsKey(ability.getAbilityData().getGuid()))
                .filter(abilityEntry -> abilityEntry.getRequirements().get(ability.getAbilityData().getGuid()) <= newLvl)
                .forEach(abilityEntry -> abilities.stream()
                        .filter(ab -> ab.getAbilityData().getGuid() == (abilityEntry.getAbilityData()))
                        .forEach(ab -> ab.setOpen(true)));
    }
}
