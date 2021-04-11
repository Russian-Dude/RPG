package ru.rdude.rpg.game.mapVisual;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class MapSpriteObject extends Sprite {

    private int currentDepth = 1;
    private int cellX = 0;
    private int cellY = 0;

    public MapSpriteObject(TextureRegion region) {
        super(region);
    }

    public int getCurrentDepth() {
        return currentDepth;
    }

    public void setCurrentDepth(int currentDepth) {
        this.currentDepth = currentDepth;
    }

    public int getCellX() {
        return cellX;
    }

    public void setCellX(int cellX) {
        this.cellX = cellX;
    }

    public int getCellY() {
        return cellY;
    }

    public void setCellY(int cellY) {
        this.cellY = cellY;
    }
}
