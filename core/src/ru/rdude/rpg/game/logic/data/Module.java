package ru.rdude.rpg.game.logic.data;

import java.io.Serializable;
import java.util.List;

public class Module implements Serializable {

    private List<SkillData> skillData;
    private List<ItemData> itemData;
    private List<MonsterData> monsterData;
}
