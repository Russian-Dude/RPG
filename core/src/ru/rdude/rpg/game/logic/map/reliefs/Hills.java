package ru.rdude.rpg.game.logic.map.reliefs;

public class Hills extends Relief {
    private static Hills instance;

    private Hills() {
    }

    public static Hills getInstance() {
        if (instance == null) {
            synchronized (Hills.class) {
                if (instance == null) {
                    instance = new Hills();
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
