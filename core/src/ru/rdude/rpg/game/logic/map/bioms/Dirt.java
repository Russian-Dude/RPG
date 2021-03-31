package ru.rdude.rpg.game.logic.map.bioms;

public class Dirt extends BiomCellProperty {

    private static Dirt instance;

    private Dirt() {
    }

    public static Dirt getInstance() {
        if (instance == null) {
            synchronized (DeadLand.class) {
                if (instance == null) {
                    instance = new Dirt();
                }
            }
        }
        return instance;
    }

    @Override
    public BiomCellProperty getThisInstance() {
        return getInstance();
    }
}
