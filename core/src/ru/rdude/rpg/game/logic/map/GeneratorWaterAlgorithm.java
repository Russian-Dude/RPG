package ru.rdude.rpg.game.logic.map;

import java.util.Arrays;

public enum GeneratorWaterAlgorithm {
    AS_BIOM("Water as biome"),
    SEPARATE_FROM_BIOM("Separate"),
    MIXED("Mixed"),
    SUPER_MIXED("Extra mixed"),
    SMALL_ISLANDS("Small islands"),
    NO_WATER("No water");

    public final String description;

    GeneratorWaterAlgorithm(String description) {
        this.description = description;
    }

    public static String[] descriptions() {
        return Arrays.stream(values())
                .map(g -> g.description)
                .toArray(String[]::new);
    }

    public static GeneratorWaterAlgorithm byDescription(String description) {
        return Arrays.stream(values())
                .filter(g -> g.description.equals(description))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("There is no algorithm with description: " + description));
    }
}
