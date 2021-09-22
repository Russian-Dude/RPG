package ru.rdude.rpg.game.logic.data;

import ru.rdude.rpg.game.logic.data.resources.AbilityResources;
import ru.rdude.rpg.game.utils.jsonextension.JsonPolymorphicSubType;

import java.util.*;
import java.util.stream.Stream;

@JsonPolymorphicSubType("abilityData")
public final class AbilityData extends EntityData implements AbilityDataCell<AbilityData> {

    private static final Map<Long, AbilityData> abilities = new HashMap<>();

    private List<AbilityLevel> levels = new LinkedList<>();

    AbilityData() {
        super();
    }

    public AbilityData(long guid) {
        super(guid);
        setResources(new AbilityResources());
    }

    public static AbilityData getAbilityByGuid(long guid) {
        return abilities.get(guid);
    }

    public static void storeAbilities(Collection<AbilityData> collection) {
        collection.forEach(abilityData -> abilities.put(abilityData.getGuid(), abilityData));
    }

    public Optional<AbilityLevel> getLvl(int lvl) {
        return levels.size() < lvl || lvl == 0 ? Optional.empty() : Optional.ofNullable(levels.get(lvl - 1));
    }

    public AbilityLevel createLvl(int lvl) {
        AbilityLevel abilityLevel = new AbilityLevel();
        levels.add(lvl - 1, abilityLevel);
        return abilityLevel;
    }

    public void clearLevels() {
        levels.clear();
    }

    public List<AbilityLevel> getLevels() {
        return levels;
    }

    public void setLevels(List<AbilityLevel> levels) {
        this.levels = levels;
    }

    @Override
    public AbilityResources getResources() {
        return (AbilityResources) super.getResources();
    }

    @Override
    public boolean hasEntityDependency(long guid) {
        return levels.stream()
                .anyMatch(abilityLevel -> abilityLevel.hasEntityDependency(guid));
    }

    @Override
    public void replaceEntityDependency(long oldValue, long newValue) {
        levels.forEach(abilityLevel -> abilityLevel.replaceEntityDependency(oldValue, newValue));
    }

    public static class AbilityLevel {

        private int lvlRequirement = 0;
        private int classLvlRequirement = 0;
        private Set<Long> buffs = new HashSet<>();
        private Set<Long> skills = new HashSet<>();
        private String description = "";

        public int getLvlRequirement() {
            return lvlRequirement;
        }

        public void setLvlRequirement(int lvlRequirement) {
            this.lvlRequirement = lvlRequirement;
        }

        public int getClassLvlRequirement() {
            return classLvlRequirement;
        }

        public void setClassLvlRequirement(int classLvlRequirement) {
            this.classLvlRequirement = classLvlRequirement;
        }

        public Set<Long> getBuffs() {
            return buffs;
        }

        public void setBuffs(Set<Long> buffs) {
            this.buffs = buffs;
        }

        public Set<Long> getSkills() {
            return skills;
        }

        public void setSkills(Set<Long> skills) {
            this.skills = skills;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        private boolean hasEntityDependency(long guid) {
            return skills.contains(guid) || buffs.contains(guid);
        }

        private void replaceEntityDependency(long oldValue, long newValue) {
            if (skills.contains(oldValue)) {
                skills.remove(oldValue);
                skills.add(newValue);
            }
            if (buffs.contains(oldValue)) {
                buffs.remove(oldValue);
                buffs.add(newValue);
            }
        }
    }
}
