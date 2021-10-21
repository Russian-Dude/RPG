package ru.rdude.rpg.game.mapVisual;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import ru.rdude.rpg.game.logic.data.MonsterData;
import ru.rdude.rpg.game.logic.data.resources.Resource;
import ru.rdude.rpg.game.logic.game.Game;
import ru.rdude.rpg.game.logic.gameStates.Map;
import ru.rdude.rpg.game.ui.MonsterOnMapTooltip;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class MonstersOnMap extends Group {

    private final Map.MonstersOnCell monsters;
    private MonsterOnMapTooltip tooltip;

    public MonstersOnMap(Map.MonstersOnCell monsters) {
        super();
        this.monsters = monsters;
        this.tooltip = new MonsterOnMapTooltip(monsters);
        addListener(tooltip);
        createVisuals(monsters);
    }

    public Map.MonstersOnCell getMonsters() {
        return monsters;
    }

    private void createVisuals(Map.MonstersOnCell monsters) {
        List<TextureAtlas.AtlasRegion> monsterRegions = monsters.getMonsters().stream()
                .sorted(Comparator.comparingInt(monster -> monster.lvl))
                .map(monster -> MonsterData.getMonsterByGuid(monster.guid).getResources().getMonsterImage())
                .map(resource -> Game.getImageFactory().getRegion(resource.getGuid()))
                .collect(Collectors.toList());
        switch (monsterRegions.size()) {
            case 1:
                createVisuals1(monsterRegions.get(0));
                break;
            case 2:
                createVisuals2(monsterRegions);
                break;
            case 3:
                createVisuals3(monsterRegions);
                break;
            case 4:
                createVisuals4(monsterRegions);
                break;
            case 5:
            default:
                createVisuals5(monsterRegions);
                break;
        }
    }

    private void createVisuals1(TextureAtlas.AtlasRegion atlasRegion) {
        float widthToHeightRelation = (float) atlasRegion.getRegionWidth() / (float) atlasRegion.getRegionHeight();
        Image image = new Image(atlasRegion);
        setRelationalSize(120f, atlasRegion, image);
        addActor(image);
        image.setPosition((128f - image.getWidth()) / 2, (128f - image.getHeight()) / 2);
    }

    private void createVisuals2(List<TextureAtlas.AtlasRegion> monsterAtlasRegions) {
        for (int i = 0; i < monsterAtlasRegions.size(); i++) {
            TextureAtlas.AtlasRegion atlasRegion = monsterAtlasRegions.get(i);
            Image image = new Image(atlasRegion);
            setRelationalSize(i == 0 ? 100f : 120f, atlasRegion, image);
            addActor(image);
            if (i == 0) {
                image.setPosition((128f - image.getWidth()) / 1.5f, image.getHeight() / 1.5f);
            }
        }
    }

    private void createVisuals3(List<TextureAtlas.AtlasRegion> monsterAtlasRegions) {
        List<Float> widthList = List.of(80f, 90f, 120f);
        List<Float> xOffsetList = List.of(10f, 60f, 0f);
        List<Float> yOffsetList = List.of(70f, 25f, 0f);
        for (int i = 0; i < monsterAtlasRegions.size(); i++) {
            TextureAtlas.AtlasRegion atlasRegion = monsterAtlasRegions.get(i);
            Image image = new Image(atlasRegion);
            setRelationalSize(widthList.get(i), atlasRegion, image);
            addActor(image);
            image.setPosition(xOffsetList.get(i), yOffsetList.get(i));
        }
    }

    private void createVisuals4(List<TextureAtlas.AtlasRegion> monsterAtlasRegions) {
        List<Float> widthList = List.of(75f ,80f, 90f, 120f);
        List<Float> xOffsetList = List.of(64f, 10f, 60f, 0f);
        List<Float> yOffsetList = List.of(75f, 70f, 25f, 0f);
        for (int i = 0; i < monsterAtlasRegions.size(); i++) {
            TextureAtlas.AtlasRegion atlasRegion = monsterAtlasRegions.get(i);
            Image image = new Image(atlasRegion);
            setRelationalSize(widthList.get(i), atlasRegion, image);
            addActor(image);
            image.setPosition(xOffsetList.get(i), yOffsetList.get(i));
        }
    }

    private void createVisuals5(List<TextureAtlas.AtlasRegion> monsterAtlasRegions) {
        List<Float> widthList = List.of(75f, 75f ,80f, 90f, 120f);
        List<Float> xOffsetList = List.of(32f, 64f, 10f, 60f, 0f);
        List<Float> yOffsetList = List.of(75f, 75f, 70f, 25f, 0f);
        for (int i = 0; i < monsterAtlasRegions.size(); i++) {
            TextureAtlas.AtlasRegion atlasRegion = monsterAtlasRegions.get(i);
            Image image = new Image(atlasRegion);
            setRelationalSize(widthList.get(i), atlasRegion, image);
            addActor(image);
            image.setPosition(xOffsetList.get(i), yOffsetList.get(i));
        }
    }

    private void setRelationalSize(float size, TextureAtlas.AtlasRegion atlasRegion, Image image) {
        float widthToHeightRelation = (float) atlasRegion.getRegionWidth() / (float) atlasRegion.getRegionHeight();
        boolean heightMoreThanWidth = widthToHeightRelation < 1;
        if (heightMoreThanWidth) {
            image.setSize(size * widthToHeightRelation, size);
        }
        else {
            image.setSize(size, size / widthToHeightRelation);
        }
    }
}
