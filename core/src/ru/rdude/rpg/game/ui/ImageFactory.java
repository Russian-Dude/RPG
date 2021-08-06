package ru.rdude.rpg.game.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import ru.rdude.rpg.game.logic.enums.Biom;
import ru.rdude.rpg.game.logic.enums.Relief;
import ru.rdude.rpg.game.logic.map.Cell;

import java.util.HashMap;
import java.util.Map;

public final class ImageFactory {

    private final Map<Long, TextureAtlas.AtlasRegion> regions = new HashMap<>();
    private final Map<Long, TextureAtlas> regionsToAtlas = new HashMap<>();
    private final Map<String, Image> backgrounds = new HashMap<>();

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

    public Image getGroundImage(Cell cell) {
        String name = cell.getBiom().name() + "_GROUND.png";
        Image result = backgrounds.get(name);
        if (result == null) {
            result = new Image(new Texture(Gdx.files.internal("images\\backgrounds\\" + name + ".png")));
            backgrounds.put(name, result);
        }
        return result;
    }

    public Image getBackgroundImage(Cell cell) {
        if (cell.getRelief() == Relief.PLAIN || cell.getBiom() == Biom.WATER) {
            Image result = backgrounds.get("EMPTY");
            if (result == null) {
                result = new Image();
                backgrounds.put("EMPTY", new Image());
            }
            return result;
        }
        String name = cell.getBiom().name() + "_" + cell.getRelief().name() + "_BACK.png";
        Image result = backgrounds.get(name);
        if (result == null) {
            result = new Image(new Texture(Gdx.files.internal("images\\backgrounds\\" + name + ".png")));
            backgrounds.put(name, result);
        }
        return result;
    }

}
