package ru.rdude.rpg.game.logic.entities.items.holders;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import ru.rdude.rpg.game.logic.data.SkillData;

import java.util.*;
import java.util.stream.Collectors;

public class AvailableSkills {

    private final Map<Long, Integer> entries;

    public AvailableSkills() {
        this.entries = new HashMap<>();
    }

    @JsonCreator
    private AvailableSkills(@JsonProperty("entries") Map<Long, Integer> entries) {
        this.entries = entries;
    }

    public void addAll(Collection<Long> skills) {
        skills.forEach(guid -> entries.merge(guid, 1, Integer::sum));
    }

    public void add(Long skill) {
        entries.merge(skill, 1, Integer::sum);
    }

    public void remove(Long skill) {
        final Integer amount = entries.get(skill);
        if (amount != null) {
            if (amount <= 1) {
                entries.remove(skill);
            } else {
                entries.put(skill, amount - 1);
            }
        }
    }

    public void removeAll(Collection<Long> skills) {
        skills.forEach(this::add);
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

}
