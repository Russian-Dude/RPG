package ru.rdude.rpg.game.logic.enums;

public enum WaterDepth {
    SMALL("SHOAL"), NORMAL("DEEP"), DEEP("OCEAN"), RIVER("RIVER");

    public final String name;

    WaterDepth(String name) {
        this.name = name;
    }
}
