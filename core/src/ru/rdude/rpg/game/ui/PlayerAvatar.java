package ru.rdude.rpg.game.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

public class PlayerAvatar extends Group {

    private Image image;

    public PlayerAvatar() {
        image = new Image(new Texture(Gdx.files.internal("images\\player\\portrait\\CubicGuy.png")));
        addActor(image);
        setWidth(image.getWidth());
        setHeight(image.getHeight());
    }

}
