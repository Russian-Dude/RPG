package ru.rdude.rpg.game.logic.map.reliefs;

public class Mountains extends ReliefCellProperty {
    private static Mountains instance;

    private Mountains() {
    }

    public static Mountains getInstance() {
        if (instance == null) {
            synchronized (Mountains.class) {
                if (instance == null) {
                    instance = new Mountains();
                }
            }
        }
        return instance;
    }

    @Override
    public ReliefCellProperty getThisInstance() {
        return getInstance();
    }
}
