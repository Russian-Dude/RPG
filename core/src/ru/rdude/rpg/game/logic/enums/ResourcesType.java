package ru.rdude.rpg.game.logic.enums;

import ru.rdude.rpg.game.logic.data.*;
import ru.rdude.rpg.game.logic.data.Module;
import ru.rdude.rpg.game.logic.data.resources.*;

public enum ResourcesType {
    MODULE,
    SKILL,
    MONSTER,
    ITEM,
    EVENT,
    QUEST;

    public static ResourcesType of(EntityData entityData) {
        if (entityData instanceof Module) {
            return MODULE;
        }
        else if (entityData instanceof SkillData) {
            return SKILL;
        }
        else if (entityData instanceof MonsterData) {
            return MONSTER;
        }
        else if (entityData instanceof ItemData) {
            return ITEM;
        }
        else {
            throw new IllegalArgumentException("entity data of " + entityData.getClass() + " is not implemented in ResourcesType.of() method");
        }
    }

    public static ResourcesType of(Resources resources) {
        if (resources instanceof ModuleResources) {
            return MODULE;
        }
        else if (resources instanceof SkillResources) {
            return SKILL;
        }
        else if (resources instanceof MonsterResources) {
            return MONSTER;
        }
        else if (resources instanceof ItemResources) {
            return ITEM;
        }
        else {
            throw new IllegalArgumentException("resources of " + resources.getClass() + " is not implemented in ResourcesType.of() method");
        }
    }

    public Resources createInstance() {
        Resources resources;
        switch (this) {
            case ITEM:
                resources = new ItemResources();
                break;
            case MODULE:
                resources = new ModuleResources();
                break;
            case SKILL:
                resources = new SkillResources();
                break;
            case MONSTER:
                resources = new MonsterResources();
                break;
            case EVENT:
                resources = new EventResources();
                break;
            case QUEST:
                resources = new QuestResources();
                break;
            default:
                throw new IllegalArgumentException("creating instance of " + this + " not implemented");
        }
        return resources;
    }
}
