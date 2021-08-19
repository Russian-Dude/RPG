package ru.rdude.rpg.game.logic.entities;

import ru.rdude.rpg.game.logic.entities.beings.MonsterFactory;
import ru.rdude.rpg.game.logic.entities.items.ItemFactory;
import ru.rdude.rpg.game.logic.entities.skills.SkillFactory;

public class EntityFactory {

    private final ItemFactory itemFactory = new ItemFactory();
    private final MonsterFactory monsterFactory = new MonsterFactory();
    private final SkillFactory skillFactory = new SkillFactory();

    public ItemFactory items() {
        return itemFactory;
    }

    public MonsterFactory monsters() {
        return monsterFactory;
    }

    public SkillFactory skills() {
        return skillFactory;
    }
}
