package ru.rdude.rpg.game.logic.enums;

public enum Size implements UsedByStatistics
{SMALL (5f), MEDIUM (15f), BIG (25f);
    private final float sizeNumber;
    Size(float sizeNumber) {
        this.sizeNumber = sizeNumber;
    }
    public float getSizeNumber() {
        return sizeNumber;
    }
}