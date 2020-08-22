package ru.rdude.rpg.game.logic.map.reliefs;

public class Plain extends Relief {

    private static Plain instance;

    private Plain() {
    }

    public static Plain getInstance() {
        if (instance == null) {
            synchronized (Plain.class) {
                if (instance == null) {
                    instance = new Plain();
                }
            }
        }
        return instance;
    }

    @Override
    public Relief getThisInstance() {
        return getInstance();
    }
}
