package ru.rdude.rpg.game.logic.entities.items.holders;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import ru.rdude.rpg.game.logic.entities.skills.SkillsProvider;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class AvailableSkills {

    private final Map<SkillsProvider, Set<Long>> entries;

    public AvailableSkills() {
        this.entries = new HashMap<>();
    }

    @JsonCreator
    private AvailableSkills(@JsonProperty("entries") Map<SkillsProvider, Set<Long>> entries) {
        this.entries = entries;
    }

    public void add(SkillsProvider skillsProvider) {
        entries.put(skillsProvider, new HashSet<>(skillsProvider.getAvailableSkills()));
    }

    public void remove(SkillsProvider skillsProvider) {
        entries.remove(skillsProvider);
    }

    public Set<Long> get() {
        return entries.values().stream()
                .flatMap(Set::stream)
                .collect(Collectors.toSet());
    }

}
