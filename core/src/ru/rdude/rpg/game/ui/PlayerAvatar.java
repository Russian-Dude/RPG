package ru.rdude.rpg.game.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.badlogic.gdx.utils.Align;
import ru.rdude.rpg.game.logic.game.Game;
import ru.rdude.rpg.game.ui.colors.SkinColor;

import java.util.List;

public class PlayerAvatar extends Group {

    private final Sprite faceSprite = new Sprite(Game.getCurrentGame().getAvatarCreator().faces().get(0));
    private final Sprite clothSprite = new Sprite(Game.getCurrentGame().getAvatarCreator().faces().get(0));
    private final Sprite mouthSprite = new Sprite(Game.getCurrentGame().getAvatarCreator().faces().get(0));
    private final Sprite noseSprite = new Sprite(Game.getCurrentGame().getAvatarCreator().faces().get(0));
    private final Sprite eyesSprite = new Sprite(Game.getCurrentGame().getAvatarCreator().faces().get(0));
    private final Sprite eyePupilsSprite = new Sprite(Game.getCurrentGame().getAvatarCreator().faces().get(0));
    private final Sprite eyeBrowsSprite = new Sprite(Game.getCurrentGame().getAvatarCreator().faces().get(0));
    private final Sprite beardSprite = new Sprite(Game.getCurrentGame().getAvatarCreator().faces().get(0));
    private final Sprite hairSprite = new Sprite(Game.getCurrentGame().getAvatarCreator().faces().get(0));

    private final Image face = new Image(new SpriteDrawable(faceSprite));
    private final Image cloth = new Image(new SpriteDrawable(clothSprite));
    private final Image mouth = new Image(new SpriteDrawable(mouthSprite));
    private final Image nose = new Image(new SpriteDrawable(noseSprite));
    private final Image eyes = new Image(new SpriteDrawable(eyesSprite));
    private final Image eyePupils = new Image(new SpriteDrawable(eyePupilsSprite));
    private final Image eyeBrows = new Image(new SpriteDrawable(eyeBrowsSprite));
    private final Image beard = new Image(new SpriteDrawable(beardSprite));
    private final Image hair = new Image(new SpriteDrawable(hairSprite));

    private final List<Image> images = List.of(face, cloth, mouth, nose, eyes, eyePupils, eyeBrows, beard, hair);

    private final Label name = new Label("", UiData.DEFAULT_SKIN, "mud");

    public PlayerAvatar() {
        addActor(face);
        addActor(cloth);
        addActor(mouth);
        addActor(beard);
        addActor(nose);
        addActor(eyes);
        addActor(eyePupils);
        addActor(eyeBrows);
        addActor(hair);
        addActor(name);

        name.setAlignment(Align.center);
        name.setWidth(face.getWidth());
        setSize(face.getWidth(), face.getHeight());
    }

    public void setFace(TextureAtlas.AtlasRegion textureRegion) {
        ((SpriteDrawable) face.getDrawable()).getSprite().setRegion(textureRegion);
    }

    public void setCloth(TextureAtlas.AtlasRegion textureRegion) {
        ((SpriteDrawable) cloth.getDrawable()).getSprite().setRegion(textureRegion);
    }

    public void setMouth(TextureAtlas.AtlasRegion textureRegion) {
        ((SpriteDrawable) mouth.getDrawable()).getSprite().setRegion(textureRegion);
    }

    public void setNose(TextureAtlas.AtlasRegion textureRegion) {
        ((SpriteDrawable) nose.getDrawable()).getSprite().setRegion(textureRegion);
    }

    public void setEyes(TextureAtlas.AtlasRegion textureRegion) {
        ((SpriteDrawable) eyes.getDrawable()).getSprite().setRegion(textureRegion);
    }

    public void setEyePupils(TextureAtlas.AtlasRegion textureRegion) {
        ((SpriteDrawable) eyePupils.getDrawable()).getSprite().setRegion(textureRegion);
    }

    public void setEyeBrows(TextureAtlas.AtlasRegion textureRegion) {
        ((SpriteDrawable) eyeBrows.getDrawable()).getSprite().setRegion(textureRegion);
    }

    public void setBeard(TextureAtlas.AtlasRegion textureRegion) {
        ((SpriteDrawable) beard.getDrawable()).getSprite().setRegion(textureRegion);
    }

    public void setHair(TextureAtlas.AtlasRegion textureRegion) {
        ((SpriteDrawable) hair.getDrawable()).getSprite().setRegion(textureRegion);
    }

    public void setName(String name) {
        this.name.setText(name);
    }

    public void setFaceColor(Color color) {
        face.setColor(color);
        nose.setColor(color);
        mouth.setColor(color);
    }

    public void setFaceColor(float r, float g, float b, float a) {
        face.setColor(r, g, b, a);
        nose.setColor(r, g, b, a);
        mouth.setColor(r, g, b, a);
    }

    public void setClothColor(Color color) {
        cloth.setColor(color);
    }

    public void setClothColor(float r, float g, float b, float a) {
        cloth.setColor(r, g, b, a);
    }

    public void setEyesColor(Color color) {
        eyePupils.setColor(color);
    }

    public void setEyesColor(float r, float g, float b, float a) {
        eyePupils.setColor(r, g, b, a);
    }

    public void setHairColor(Color color) {
        hair.setColor(color);
        eyeBrows.setColor(color);
        beard.setColor(color);
    }

    public void setHairColor(float r, float g, float b, float a) {
        hair.setColor(r, g, b, a);
        eyeBrows.setColor(r, g, b, a);
        beard.setColor(r, g, b, a);
    }

    @Override
    public void setSize(float width, float height) {
        super.setSize(width, height);
        images.forEach(image -> image.setSize(width, height));
    }
}
