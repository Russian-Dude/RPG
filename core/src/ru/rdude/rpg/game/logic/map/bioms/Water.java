package ru.rdude.rpg.game.logic.map.bioms;

public class Water extends Biom {

    private static Water instance;

    private Water() {
    }

    public static Water getInstance() {
        if (instance == null) {
            synchronized (DeadLand.class) {
                if (instance == null) {
                    instance = new Water();
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
