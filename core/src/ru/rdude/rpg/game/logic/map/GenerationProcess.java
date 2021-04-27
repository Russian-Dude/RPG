package ru.rdude.rpg.game.logic.map;

public enum GenerationProcess {
    NO("Generation is not active"),
    START("Starting"),
    FINISH("Finishing"),
    INTERRUPTED("Interrupted"),
    WATER_CREATION("Creating water"),
    BIOME_CREATION("Creating biomes"),
    RIVERS_CREATION("Creating rivers"),
    DENOISING("De-noising biomes"),
    RELIEF_CREATION("Creating relief"),
    CITIES_CREATION("Creating cities"),
    DUNGEONS_CREATION("Creating dungeons"),
    ROADS_CREATION("Creating roads"),
    DEPTH_OF_WATER_CREATION("Creating depth of water"),
    LEVELING("Leveling");

    public final String description;

    GenerationProcess(String description) {
        this.description = description;
    }
}
