package ru.rdude.rpg.game.logic.map.bioms;

public class Volcanic extends BiomCellProperty {

    private static Volcanic instance;

    private Volcanic() {
    }

    public static Volcanic getInstance() {
        if (instance == null) {
            synchronized (DeadLand.class) {
                if (instance == null) {
                    instance = new Volcanic();
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
