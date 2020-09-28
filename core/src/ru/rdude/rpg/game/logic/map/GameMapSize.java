package ru.rdude.rpg.game.logic.map;

public enum GameMapSize {
    XS(64, 32),
    S(128, 64),
    M(256, 128),
    L(512, 256),
    XL(1024, 512),
    XXL(2048, 1024);

    private int width;
    private int height;

    GameMapSize(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
