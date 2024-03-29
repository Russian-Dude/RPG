package ru.rdude.rpg.game.logic.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import ru.rdude.rpg.game.logic.data.resources.ModuleResources;
import ru.rdude.rpg.game.utils.jsonextension.JsonPolymorphicSubType;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@JsonPolymorphicSubType("moduleData")
public class Module extends EntityData implements Serializable {

    private Set<SkillData> skillData;
    private Set<ItemData> itemData;
    private Set<MonsterData> monsterData;
    private Set<EventData> eventData;
    private Set<QuestData> questData;
    private Set<PlayerClassData> playerClassData;
    private Set<AbilityData> abilityData;

    // default constructor for Jackson json deserialization
    private Module() {
    }

    public Module(long guid) {
        super(guid);
        setResources(new ModuleResources());
        skillData = new HashSet<>();
        itemData = new HashSet<>();
        monsterData = new HashSet<>();
        eventData = new HashSet<>();
        questData = new HashSet<>();
        playerClassData = new HashSet<>();
        abilityData = new HashSet<>();
    }

    public Set<SkillData> getSkillData() {
        return skillData;
    }

    public void setSkillData(Set<SkillData> skillData) {
        this.skillData = skillData;
    }

    public Set<ItemData> getItemData() {
        return itemData;
    }

    public void setItemData(Set<ItemData> itemData) {
        this.itemData = itemData;
    }

    public Set<MonsterData> getMonsterData() {
        return monsterData;
    }

    public void setMonsterData(Set<MonsterData> monsterData) {
        this.monsterData = monsterData;
    }

    public Set<EventData> getEventData() {
        return eventData;
    }

    public void setEventData(Set<EventData> eventData) {
        this.eventData = eventData;
    }

    public Set<QuestData> getQuestData() {
        return questData;
    }

    public void setQuestData(Set<QuestData> questData) {
        this.questData = questData;
    }

    public Set<PlayerClassData> getPlayerClassData() {
        return playerClassData;
    }

    public void setPlayerClassData(Set<PlayerClassData> playerClassData) {
        this.playerClassData = playerClassData;
    }

    public Set<AbilityData> getAbilityData() {
        return abilityData;
    }

    public void setAbilityData(Set<AbilityData> abilityData) {
        this.abilityData = abilityData;
    }

    @Override
    public ModuleResources getResources() {
        return (ModuleResources) super.getResources();
    }

    public void setResources(ModuleResources moduleResources) {
        super.setResources(moduleResources);
    }

    @JsonIgnore
    public Set<EntityData> getAllEntities() {
        return Stream.of(skillData, itemData, monsterData, eventData, questData, playerClassData, abilityData)
                .flatMap(Set::stream)
                .collect(Collectors.toSet());
    }

    public void addEntity(EntityData entityData) {
        if (entityData instanceof SkillData) {
            skillData.add((SkillData) entityData);
        }
        else if (entityData instanceof ItemData) {
            itemData.add((ItemData) entityData);
        }
        else if (entityData instanceof MonsterData) {
            monsterData.add((MonsterData) entityData);
        }
        else if (entityData instanceof Module) {
            addModuleDependency((Module) entityData);
        }
        else if (entityData instanceof EventData) {
            eventData.add((EventData) entityData);
        }
        else if (entityData instanceof QuestData) {
            questData.add((QuestData) entityData);
        }
        else if (entityData instanceof PlayerClassData) {
            playerClassData.add((PlayerClassData) entityData);
        }
        else if (entityData instanceof AbilityData) {
            abilityData.add((AbilityData) entityData);
        }
        else throw new IllegalArgumentException("Adding entity of a class " + entityData.getClass() + " is not implemented");
    }

    public void removeEntity(EntityData entityData) {
        if (entityData instanceof SkillData) {
            skillData.remove(entityData);
        }
        else if (entityData instanceof ItemData) {
            itemData.remove(entityData);
        }
        else if (entityData instanceof MonsterData) {
            monsterData.remove(entityData);
        }
        else if (entityData instanceof Module) {
            removeModuleDependency(entityData);
        }
        else if (entityData instanceof EventData) {
            eventData.remove(entityData);
        }
        else if (entityData instanceof QuestData) {
            questData.remove(entityData);
        }
        else if (entityData instanceof PlayerClassData) {
            playerClassData.remove(entityData);
        }
        else if (entityData instanceof AbilityData) {
            abilityData.remove(entityData);
        }
        else throw new IllegalArgumentException("Removing entity of a class " + entityData.getClass() + " is not implemented");
    }

    @Override
    public boolean hasEntityDependency(long guid) {
        return Stream.of(skillData, itemData, monsterData, eventData, questData, playerClassData, abilityData)
                .flatMap(Collection::stream)
                .anyMatch(entityData -> entityData.hasEntityDependency(guid));
    }

    @Override
    public void replaceEntityDependency(long oldValue, long newValue) {
        Stream.of(skillData, itemData, monsterData, eventData, questData, playerClassData, abilityData)
                .flatMap(Collection::stream)
                .forEach(entityData -> entityData.replaceEntityDependency(oldValue, newValue));
    }
}
