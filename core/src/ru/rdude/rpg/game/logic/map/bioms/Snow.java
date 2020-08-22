package ru.rdude.rpg.game.logic.map.bioms;

public class Snow extends Biom{

    private static Snow instance;

    private Snow() {
    }

    public static Snow getInstance() {
        if (instance == null) {
            synchronized (DeadLand.class) {
                if (instance == null) {
                    instance = new Snow();
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
