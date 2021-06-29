package ru.rdude.rpg.game.logic.entities.skills;

import ru.rdude.rpg.game.logic.data.SkillData;

public interface AvailableSkillsObserver {

    enum Action {REMOVED, ADDED}

    void update(Action action, SkillData skillData);

}
