package ru.rdude.rpg.game.logic.entities.checkers;

import ru.rdude.rpg.game.logic.data.*;
import ru.rdude.rpg.game.logic.entities.Entity;

public class SameEntityChecker {

    private final ItemDescriberChecker itemDescriberChecker = new ItemDescriberChecker();
    private final SkillDescriberChecker skillDescriberChecker = new SkillDescriberChecker();
    private final MonsterDescriberChecker monsterDescriberChecker = new MonsterDescriberChecker();

    public boolean checkItems(long a, long b) {
        ItemData entityDataA = ItemData.getItemDataByGuid(a);
        ItemData entityDataB = ItemData.getItemDataByGuid(b);
        return entityDataA != null && entityDataB != null && checkConcreteType(entityDataA, entityDataB);
    }

    public boolean checkSkills(long a, long b) {
        SkillData entityDataA = SkillData.getSkillByGuid(a);
        SkillData entityDataB = SkillData.getSkillByGuid(b);
        return entityDataA != null && entityDataB != null && checkConcreteType(entityDataA, entityDataB);
    }

    public boolean checkMonsters(long a, long b) {
        MonsterData entityDataA = MonsterData.getMonsterByGuid(a);
        MonsterData entityDataB = MonsterData.getMonsterByGuid(b);
        return entityDataA != null && entityDataB != null && checkConcreteType(entityDataA, entityDataB);
    }

    public boolean checkEvents(long a, long b) {
        EventData entityDataA = EventData.getEventByGuid(a);
        EventData entityDataB = EventData.getEventByGuid(b);
        return entityDataA != null && entityDataB != null && checkConcreteType(entityDataA, entityDataB);
    }

    public boolean checkQuests(long a, long b) {
        QuestData entityDataA = QuestData.getQuestByGuid(a);
        QuestData entityDataB = QuestData.getQuestByGuid(b);
        return entityDataA != null && entityDataB != null && checkConcreteType(entityDataA, entityDataB);
    }

    public boolean check(long a, long b) {
        return checkSkills(a, b) || checkItems(a, b) || checkEvents(a, b) || checkMonsters(a, b) || checkQuests(a, b);
    }

    public boolean check(Entity<?> a, long b) {
        return a != null && check(a.getEntityData(), b);
    }

    public boolean check(EntityData a, long b) {
        if (a instanceof SkillData) {
            return checkConcreteType(a, SkillData.getSkillByGuid(b));
        }
        else if (a instanceof ItemData) {
            return checkConcreteType(a, ItemData.getItemDataByGuid(b));
        }
        else if (a instanceof MonsterData) {
            return checkConcreteType(a, MonsterData.getMonsterByGuid(b));
        }
        else if (a instanceof EventData) {
            return checkConcreteType(a, EventData.getEventByGuid(b));
        }
        else if (a instanceof QuestData) {
            return checkConcreteType(a, QuestData.getQuestByGuid(b));
        }
        else {
            throw new IllegalArgumentException("Can not check if entities are same. " + a.getClass() + " not implemented in SameEntityChecker.");
        }
    }

    public boolean check(Entity<?> a, Entity<?> b) {
        return a != null && b != null && check(a.getEntityData(), b.getEntityData());
    }
    public boolean check(EntityData a, EntityData b) {
        if (a.getClass().isAssignableFrom(b.getClass()) || b.getClass().isAssignableFrom(a.getClass())) {
            return checkConcreteType(a, b);
        }
        return false;
    }

    private <T extends EntityData> boolean checkConcreteType(T a, T b) {

        if (a == null || b == null) {
            return false;
        }

        // if both are not describers
        if (!a.isDescriber() && !b.isDescriber()) {
            return a.getGuid() == b.getGuid();
        }

        // if both are describers
        else if (a.isDescriber() && b.isDescriber()) {
            return false;
        }

        // if one is describer
        T describer = a.isDescriber() ? a : b;
        T data = describer == a ? b : a;

        if (describer instanceof SkillData) {
            return skillDescriberChecker.check((SkillData) describer, (SkillData) data);
        }
        else if (describer instanceof ItemData) {
            return itemDescriberChecker.check((ItemData) describer, (ItemData) data);
        }
        else if (describer instanceof MonsterData) {
            return monsterDescriberChecker.check((MonsterData) describer, (MonsterData) data);
        }
        else {
            throw new IllegalArgumentException("Describer of type " + a.getClass() + " is not implemented");
        }
    }

}
