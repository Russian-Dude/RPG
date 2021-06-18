package ru.rdude.rpg.game.ui;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;

import java.util.HashMap;
import java.util.Map;

public class ImageFactory {

    private final Map<Long, TextureAtlas.AtlasRegion> regions = new HashMap<>();

    public void addRegion(TextureAtlas.AtlasRegion atlasRegion) {
        regions.put(Long.parseLong(atlasRegion.name), atlasRegion);
    }

    public TextureAtlas.AtlasRegion getRegion(long resourceGuid) {
        return regions.get(resourceGuid);
    }

}
