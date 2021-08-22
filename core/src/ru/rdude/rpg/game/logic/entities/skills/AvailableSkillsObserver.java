package ru.rdude.rpg.game.logic.entities.skills;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import ru.rdude.rpg.game.logic.data.SkillData;

@JsonIdentityInfo(generator = ObjectIdGenerators.UUIDGenerator.class)
public interface AvailableSkillsObserver {

    enum Action {REMOVED, ADDED}

    void update(Action action, SkillData skillData);

}
