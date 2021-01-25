package ru.rdude.rpg.game.logic.data;

import ru.rdude.rpg.game.logic.data.resources.ModuleResources;
import ru.rdude.rpg.game.logic.data.resources.Resources;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Module extends EntityData implements Serializable {

    private Set<SkillData> skillData;
    private Set<ItemData> itemData;
    private Set<MonsterData> monsterData;

    // default constructor for Jackson json deserialization
    private Module() {
    }

    public Module(long guid) {
        super(guid);
        setResources(new ModuleResources());
        skillData = new HashSet<>();
        itemData = new HashSet<>();
        monsterData = new HashSet<>();
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

    @Override
    public ModuleResources getResources() {
        return (ModuleResources) super.getResources();
    }

    public void setResources(ModuleResources moduleResources) {
        super.setResources(moduleResources);
    }

    public Set<EntityData> getAllEntities() {
        return Stream.of(skillData, itemData, monsterData)
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
    }

    @Override
    public boolean hasEntityDependency(long guid) {
        return Stream.of(skillData, itemData, monsterData)
                .flatMap(Collection::stream)
                .anyMatch(entityData -> entityData.hasEntityDependency(guid));
    }

    @Override
    public void replaceEntityDependency(long oldValue, long newValue) {
        Stream.of(skillData, itemData, monsterData)
                .flatMap(Collection::stream)
                .forEach(entityData -> entityData.replaceEntityDependency(oldValue, newValue));
    }
}
