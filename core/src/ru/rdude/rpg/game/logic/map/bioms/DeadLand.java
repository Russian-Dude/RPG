package ru.rdude.rpg.game.logic.map.bioms;

public class DeadLand extends BiomCellProperty {

    private static DeadLand instance;

    private DeadLand() {
    }

    public static DeadLand getInstance() {
        if (instance == null) {
            synchronized (DeadLand.class) {
                if (instance == null) {
                    instance = new DeadLand();
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
