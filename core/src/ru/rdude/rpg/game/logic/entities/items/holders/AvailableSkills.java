package ru.rdude.rpg.game.logic.entities.items.holders;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import ru.rdude.rpg.game.logic.data.SkillData;
import ru.rdude.rpg.game.logic.entities.skills.AvailableSkillsObserver;
import ru.rdude.rpg.game.utils.SubscribersManager;

import java.util.*;
import java.util.stream.Collectors;

public class AvailableSkills {

    private final Map<Long, Integer> entries;
    private final SubscribersManager<AvailableSkillsObserver> subscribersManager = new SubscribersManager<>();

    public AvailableSkills() {
        this.entries = new HashMap<>();
    }

    @JsonCreator
    private AvailableSkills(@JsonProperty("entries") Map<Long, Integer> entries) {
        this.entries = entries;
    }

    public void addAllByGuid(Collection<Long> skills) {
        skills.forEach(this::add);
    }

    public void addAll(Collection<SkillData> skills) {
        skills.forEach(this::add);
    }

    public void add(Long skill) {
        entries.merge(skill, 1, Integer::sum);
        if (entries.get(skill) == 1) {
            subscribersManager.notifySubscribers(sub -> sub.update(AvailableSkillsObserver.Action.ADDED, SkillData.getSkillByGuid(skill)));
        }
    }

    public void add(SkillData skillData) {
        add(skillData.getGuid());
    }

    public void remove(Long skill) {
        final Integer amount = entries.get(skill);
        if (amount != null) {
            if (amount <= 1) {
                entries.remove(skill);
                subscribersManager.notifySubscribers(sub -> sub.update(AvailableSkillsObserver.Action.REMOVED, SkillData.getSkillByGuid(skill)));
            } else {
                entries.put(skill, amount - 1);
            }
        }
    }

    public void remove(SkillData skillData) {
        remove(skillData.getGuid());
    }

    public void removeAllByGuid(Collection<Long> skills) {
        skills.forEach(this::remove);
    }

    public void removeAll(Collection<SkillData> skills) {
        skills.forEach(this::remove);
    }

    public Set<Long> getGuids() {
        return entries.keySet();
    }

    public Set<SkillData> get() {
        return entries.keySet().stream()
                .map(SkillData::getSkillByGuid)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }

    public boolean isEmpty() {
        return entries.isEmpty();
    }

    public void subscribe(AvailableSkillsObserver subscriber) {
        subscribersManager.subscribe(subscriber);
    }

    public void unsubscribe(AvailableSkillsObserver subscriber) {
        subscribersManager.unsubscribe(subscriber);
    }

}
