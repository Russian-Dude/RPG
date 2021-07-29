package ru.rdude.rpg.game.ui;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;

import java.util.HashMap;
import java.util.Map;

public class ImageFactory {

    private final Map<Long, TextureAtlas.AtlasRegion> regions = new HashMap<>();
    private final Map<Long, TextureAtlas> regionsToAtlas = new HashMap<>();

    public void addAtlas(TextureAtlas atlas) {
        atlas.getRegions().forEach(region -> {
            final long guid = Long.parseLong(region.name);
            regions.put(guid, region);
            regionsToAtlas.put(guid, atlas);
        });
    }

    public TextureAtlas.AtlasRegion getRegion(long resourceGuid) {
        return regions.get(resourceGuid);
    }

    public TextureAtlas getAtlas(long resourceGuid) {
        return regionsToAtlas.get(resourceGuid);
    }

}
