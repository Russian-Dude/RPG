package ru.rdude.rpg.game.logic.map.bioms;

public class Sand extends Biom {

    private static Sand instance;

    private Sand() {
    }

    public static Sand getInstance() {
        if (instance == null) {
            synchronized (DeadLand.class) {
                if (instance == null) {
                    instance = new Sand();
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
