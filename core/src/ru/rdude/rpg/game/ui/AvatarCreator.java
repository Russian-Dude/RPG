package ru.rdude.rpg.game.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

import java.util.ArrayList;
import java.util.List;

public class AvatarCreator {

    private TextureAtlas textureAtlas;

    private List<TextureAtlas.AtlasRegion> faces;
    private List<TextureAtlas.AtlasRegion> clothes;
    private List<TextureAtlas.AtlasRegion> mouths;
    private List<TextureAtlas.AtlasRegion> noses;
    private List<TextureAtlas.AtlasRegion> eyes;
    private List<TextureAtlas.AtlasRegion> eyePupils;
    private List<TextureAtlas.AtlasRegion> eyeBrows;
    private List<TextureAtlas.AtlasRegion> beards;
    private List<TextureAtlas.AtlasRegion> hairs;

    public AvatarCreator() {
        textureAtlas = new TextureAtlas("faces.txt");

        faces = new ArrayList<>();
        clothes = new ArrayList<>();
        mouths = new ArrayList<>();
        noses = new ArrayList<>();
        eyes = new ArrayList<>();
        eyePupils = new ArrayList<>();
        eyeBrows = new ArrayList<>();
        beards = new ArrayList<>();
        hairs = new ArrayList<>();

        textureAtlas.getRegions().forEach(atlasRegion -> {
            String name = atlasRegion.name;
            if (name.startsWith("face_skin")) {
                faces.add(atlasRegion);
            }
            else if (name.startsWith("face_cloth")) {
                clothes.add(atlasRegion);
            }
            else if (name.startsWith("mouth")) {
                mouths.add(atlasRegion);
            }
            else if (name.startsWith("nose")) {
                noses.add(atlasRegion);
            }
            else if (name.startsWith("eyes_pupils")) {
                eyePupils.add(atlasRegion);
            }
            else if (name.startsWith("eyes")) {
                eyes.add(atlasRegion);
            }
            else if (name.startsWith("eyebrows")) {
                eyeBrows.add(atlasRegion);
            }
            else if (name.startsWith("beard")) {
                beards.add(atlasRegion);
            }
            else if (name.startsWith("hair")) {
                hairs.add(atlasRegion);
            }
            else {
                throw new IllegalArgumentException("Faces texture atlas contains image that can not be used");
            }
        });
    }

    public List<TextureAtlas.AtlasRegion> faces() {
        return faces;
    }

    public List<TextureAtlas.AtlasRegion> clothes() {
        return clothes;
    }

    public List<TextureAtlas.AtlasRegion> mouths() {
        return mouths;
    }

    public List<TextureAtlas.AtlasRegion> noses() {
        return noses;
    }

    public List<TextureAtlas.AtlasRegion> eyes() {
        return eyes;
    }

    public List<TextureAtlas.AtlasRegion> eyePupils() {
        return eyePupils;
    }

    public List<TextureAtlas.AtlasRegion> eyeBrows() {
        return eyeBrows;
    }

    public List<TextureAtlas.AtlasRegion> beards() {
        return beards;
    }

    public List<TextureAtlas.AtlasRegion> hairs() {
        return hairs;
    }
}
