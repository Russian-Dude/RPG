package ru.rdude.rpg.game.logic.entities.skills;

import ru.rdude.rpg.game.logic.entities.beings.Being;
import ru.rdude.rpg.game.utils.Apply;

import java.util.ArrayList;
import java.util.List;

public final class SkillTargets {

    private final static List<Being<?>> emptyList = new ArrayList<>();

    private Being<?> mainTarget;
    private List<Being<?>> subTargets;

    public SkillTargets(Being<?> mainTarget, List<Being<?>> subTargets) {
        this.mainTarget = mainTarget;
        this.subTargets = subTargets == null ? emptyList : subTargets;
    }

    public Being<?> getMainTarget() {
        return mainTarget;
    }

    public List<Being<?>> getSubTargets() {
        return subTargets;
    }

    public List<Being<?>> getAll() {
        return Apply.to(subTargets, list -> list.add(0, mainTarget));
    }
}
