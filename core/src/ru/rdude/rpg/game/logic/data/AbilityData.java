package ru.rdude.rpg.game.logic.data;

import ru.rdude.rpg.game.utils.jsonextension.JsonPolymorphicSubType;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

@JsonPolymorphicSubType("abilityData")
public final class AbilityData extends EntityData {

    public Map<Long, Integer> requirements = new HashMap<>();
    public Set<Long> buffs = new HashSet<>();
    public Set<Long> skills = new HashSet<>();

    AbilityData() {
        super();
    }

    public AbilityData(long guid) {
        super(guid);
    }

    @Override
    public boolean hasEntityDependency(long guid) {
        return Stream.of(requirements.keySet(), buffs, skills)
                .flatMap(Set::stream)
                .mapToLong(Long::longValue)
                .anyMatch(g -> g == guid);
    }

    @Override
    public void replaceEntityDependency(long oldValue, long newValue) {
        // requirements
        if (requirements.containsKey(oldValue)) {
            requirements.put(newValue, requirements.get(oldValue));
        }
        // buffs
        if (buffs.contains(oldValue)) {
            buffs.remove(oldValue);
            buffs.add(newValue);
        }
        // skills
        if (skills.contains(oldValue)) {
            buffs.remove(oldValue);
            buffs.add(newValue);
        }
    }
}
