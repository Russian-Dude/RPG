package ru.rdude.rpg.game.logic.playerClass;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import ru.rdude.rpg.game.logic.data.AbilityData;
import ru.rdude.rpg.game.logic.data.SkillData;
import ru.rdude.rpg.game.utils.SubscribersManager;
import ru.rdude.rpg.game.utils.jsonextension.JsonPolymorphicSubType;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@JsonPolymorphicSubType("ability")
public class Ability implements AbilityCell<Ability> {

    private SubscribersManager<AbilityObserver> subscribers = new SubscribersManager<>();

    private final AbilityData abilityData;
    private boolean open = false;
    private int lvl = 0;

    @JsonCreator
    public Ability(@JsonProperty("abilityData") long guid) {
        this.abilityData = AbilityData.getAbilityByGuid(guid);
    }

    public Ability(AbilityData abilityData) {
        this.abilityData = abilityData;
    }

    @JsonProperty("abilityData")
    private long getAbilityDataJson() {
        return abilityData.getGuid();
    }

    public boolean isOpen() {
        return open;
    }

    public void setOpen(boolean open) {
        this.open = open;
        subscribers.notifySubscribers(sub -> sub.updateAbility(this, open, lvl, lvl));
    }

    public int getLvl() {
        return lvl;
    }

    public void setLvl(int lvl) {
        final int oldLvl = this.lvl;
        this.lvl = lvl;
        subscribers.notifySubscribers(sub -> sub.updateAbility(this, open, oldLvl, lvl));
    }

    public void increaseLvl() {
        setLvl(lvl + 1);
    }

    public Set<SkillData> getBuffsForCurrentLvl() {
        return abilityData.getLvl(lvl)
                .map(abilityLevel -> abilityLevel.getBuffs().stream()
                        .map(SkillData::getSkillByGuid)
                        .collect(Collectors.toSet()))
                .orElse(new HashSet<>());
    }

    public Set<SkillData> getSkillsForCurrentLvl() {
        return abilityData.getLvl(lvl)
                .map(abilityLevel -> abilityLevel.getSkills().stream()
                        .map(SkillData::getSkillByGuid)
                        .collect(Collectors.toSet()))
                .orElse(new HashSet<>());
    }

    public AbilityData getAbilityData() {
        return abilityData;
    }

    public void subscribe(AbilityObserver subscriber) {
        subscribers.subscribe(subscriber);
    }

    public void unsubscribe(AbilityObserver subscriber) {
        subscribers.unsubscribe(subscriber);
    }
}
