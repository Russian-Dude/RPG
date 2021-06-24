package ru.rdude.rpg.game.logic.entities.skills;

import java.util.Collection;

public interface SkillsProvider {

    Collection<Long> getAvailableSkills();

}
