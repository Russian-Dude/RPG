package ru.rdude.rpg.game.logic.enums;

public enum Target {
    NO(true, false),
    SELF(true, true),
    ALLY(true, false),
    ENEMY(true, false),
    ANY(true, false),
    ANY_OTHER(true, false),
    ALL(true, true),
    ALL_ALLIES(true, true),
    ALL_OTHER_ALLIES(true, true),
    ALL_ENEMIES(true, true),
    ALL_OTHER(true, true),
    RANDOM_ALLY(true, true),
    RANDOM_ENEMY(true, true),
    RANDOM_ANY(true, true),
    RANDOM_ANY_OTHER(true, true),
    LEFT_FROM_TARGET(false, true),
    RIGHT_FROM_TARGET(false, true),
    LEFT_ALLY(true, true),
    RIGHT_ALLY(true, true);

    private boolean canBeMainTarget;
    private boolean canBeSubTarget;

    Target(boolean canBeMainTarget, boolean canBeSubTarget) {
        this.canBeMainTarget = canBeMainTarget;
        this.canBeSubTarget = canBeSubTarget;
    }

    public boolean isCanBeMainTarget() {
        return canBeMainTarget;
    }

    public boolean isCanBeSubTarget() {
        return canBeSubTarget;
    }
}
