package ru.rdude.rpg.game.logic.map.bioms;

public class Swamp extends Biom {

    private static Swamp instance;

    private Swamp() {
    }

    public static Swamp getInstance() {
        if (instance == null) {
            synchronized (DeadLand.class) {
                if (instance == null) {
                    instance = new Swamp();
                }
            }
        }
        return instance;
    }

    @Override
    public Biom getThisInstance() {
        return getInstance();
    }
}
