package ru.rdude.rpg.game.logic.data;

import java.io.Serializable;
import java.util.List;

public class Module extends EntityData implements Serializable {

    private List<SkillData> skillData;
    private List<ItemData> itemData;
    private List<MonsterData> monsterData;

    public Module(long guid) {
        super(guid);
    }

    public List<SkillData> getSkillData() {
        return skillData;
    }

    public void setSkillData(List<SkillData> skillData) {
        this.skillData = skillData;
    }

    public List<ItemData> getItemData() {
        return itemData;
    }

    public void setItemData(List<ItemData> itemData) {
        this.itemData = itemData;
    }

    public List<MonsterData> getMonsterData() {
        return monsterData;
    }

    public void setMonsterData(List<MonsterData> monsterData) {
        this.monsterData = monsterData;
    }
}
