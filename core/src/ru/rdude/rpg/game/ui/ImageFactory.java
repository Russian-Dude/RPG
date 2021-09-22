package ru.rdude.rpg.game.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import ru.rdude.rpg.game.logic.enums.Biom;
import ru.rdude.rpg.game.logic.enums.Relief;
import ru.rdude.rpg.game.logic.map.Cell;
import ru.rdude.rpg.game.logic.playerClass.AbilityPath;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class ImageFactory {

    private static TextureAtlas uiTextureAtlas;

    private final Map<Long, TextureAtlas.AtlasRegion> regions = new HashMap<>();
    private final Map<Long, TextureAtlas> regionsToAtlas = new HashMap<>();
    private final Map<String, Image> backgrounds = new HashMap<>();
    private final Map<String, List<Texture>> trees = new HashMap<>();

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

    public TextureAtlas.AtlasRegion getAbilityPathAtlasRegion(AbilityPath abilityPath) {
        if (uiTextureAtlas == null) {
            uiTextureAtlas = new TextureAtlas("main_ui.atlas");
        }
        return uiTextureAtlas.findRegion("Ability_Path_" + abilityPath.name());
    }

    public Image getGroundImage(Cell cell) {
        String name = cell.getBiom().name() + "_GROUND.png";
        Image result = backgrounds.get(name);
        if (result == null) {
            result = new Image(new Texture(Gdx.files.internal("images\\backgrounds\\" + name)));
            backgrounds.put(name, result);
        }
        return result;
    }

    public Image getSkyImage(int hours, int minutes) {
        Image result = backgrounds.get("DEFAULT_SKY");
        if (result == null) {
            result = new Image(new Texture(Gdx.files.internal("images\\backgrounds\\DEFAULT_SKY.png")));
            backgrounds.put("DEFAULT_SKY", result);
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
            result = new Image(new Texture(Gdx.files.internal("images\\backgrounds\\" + name)));
            backgrounds.put(name, result);
        }
        return result;
    }

    public List<Texture> getTreesForBiom(Biom biom) {
        if (trees.isEmpty()) {
            createTreeTextures();
        }
        String s;
        switch (biom) {
            case DIRT:
            case GRASS:
                s = "GREEN";
                break;
            case SAND:
                s = "SAND";
                break;
            case SNOW:
                s = "SNOW";
                break;
            case SWAMP:
                s = "SWAMP";
                break;
            case JUNGLE:
                s = "JUNGLE";
                break;
            case DEADLAND:
            case VOLCANIC:
                s = "DEAD";
                break;
            default:
                throw new IllegalArgumentException("there is no trees for biom " + biom);
        }
        return trees.get(s);
    }

    private void createTreeTextures() {
        String[] types = {"DEAD", "GREEN", "JUNGLE", "SAND", "SNOW", "SWAMP"};
        for (String type : types) {
            int i = 1;
            List<Texture> list = new ArrayList<>();
            FileHandle fileHandle = Gdx.files.internal("images\\trees\\" + type + "_TREE" + i + ".png");
            while (fileHandle.exists()) {
                list.add(new Texture(fileHandle));
                i++;
                fileHandle = Gdx.files.internal("images\\trees\\" + type + "_TREE" + i + ".png");
            }
            trees.put(type, list);
        }
    }

}
