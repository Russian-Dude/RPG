package ru.rdude.rpg.game.logic.map.bioms;

public class Grass extends BiomCellProperty {

    private static Grass instance;

    private Grass() {
    }

    public static Grass getInstance() {
        if (instance == null) {
            synchronized (DeadLand.class) {
                if (instance == null) {
                    instance = new Grass();
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
