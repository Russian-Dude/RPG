package ru.rdude.rpg.game.utils.ui;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Scaling;

public class ResizableImage extends Image {

    public ResizableImage() {
        super();
    }

    public ResizableImage(NinePatch patch) {
        super(patch);
    }

    public ResizableImage(TextureRegion region) {
        super(region);
    }

    public ResizableImage(Texture texture) {
        super(texture);
    }

    public ResizableImage(Skin skin, String drawableName) {
        super(skin, drawableName);
    }

    public ResizableImage(Drawable drawable) {
        super(drawable);
    }

    public ResizableImage(Drawable drawable, Scaling scaling) {
        super(drawable, scaling);
    }

    public ResizableImage(Drawable drawable, Scaling scaling, int align) {
        super(drawable, scaling, align);
    }

    @Override
    public float getPrefWidth() {
        return getWidth();
    }

    @Override
    public float getPrefHeight() {
        return getHeight();
    }
}
