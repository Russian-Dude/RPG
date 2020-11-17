package ru.rdude.rpg.game.logic.data;

import ru.rdude.rpg.game.logic.entities.items.Item;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Module extends EntityData implements Serializable {

    private Set<SkillData> skillData;
    private Set<ItemData> itemData;
    private Set<MonsterData> monsterData;

    public Module(long guid) {
        super(guid);
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
            addDependency(entityData);
        }
    }
}
