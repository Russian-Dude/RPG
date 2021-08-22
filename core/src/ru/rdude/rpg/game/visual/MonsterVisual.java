package ru.rdude.rpg.game.visual;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import ru.rdude.rpg.game.logic.entities.beings.Monster;
import ru.rdude.rpg.game.logic.game.Game;
import ru.rdude.rpg.game.ui.HpBar;
import ru.rdude.rpg.game.ui.StmBar;
import ru.rdude.rpg.game.ui.UiData;
import ru.rdude.rpg.game.utils.Functions;

public class MonsterVisual extends VerticalGroup implements VisualBeing<Monster>, VisualTarget {

    private final Monster monster;
    private final DamageLabel damageLabel;
    private final Image monsterImage;
    private final HpBar hpBar;

    public MonsterVisual(Monster monster) {
        super();
        this.monster = monster;
        this.damageLabel = new DamageLabel();
        this.hpBar = new HpBar(monster);
        align(Align.center);

        Group group = new Group();

        final Long monsterImageGuid = monster.getEntityData().getResources().getMonsterImage().getGuid();
        final TextureAtlas.AtlasRegion imageRegion = Game.getImageFactory().getRegion(monsterImageGuid);
        final float widthToHeightRelation = (float) imageRegion.getRegionWidth() / (float) imageRegion.getRegionHeight();
        final float monsterSizeCoefficient;
        switch (monster.getEntityData().getSize()) {
            case BIG:
                monsterSizeCoefficient = 1.5f;
                break;
            case SMALL:
                monsterSizeCoefficient = 1.2f;
                break;
            case MEDIUM:
                monsterSizeCoefficient = 1f;
                break;
            default:
                throw new IllegalArgumentException("Can not create visual size coefficient of monster size: " + monster.getEntityData().getSize());
        }
        final float imageWidth = (Gdx.graphics.getWidth() / 10f) * monsterSizeCoefficient;
        final float imageHeight = imageWidth / widthToHeightRelation;
        monsterImage = new Image(imageRegion);
        monsterImage.setSize(imageWidth, imageHeight);
        group.setSize(imageWidth, imageHeight);
        group.addActor(monsterImage);
        group.addActor(damageLabel);
        damageLabel.setPosition(group.getWidth() / 2, group.getHeight() / 2);
        addActor(hpBar);
        addActor(group);
        final Label nameLabel = new Label(monster.getName(), UiData.DEFAULT_SKIN, UiData.BIG_TEXT_STYLE);
        addActor(nameLabel);

        // skill targeting
        monsterImage.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (Game.getGameVisual().getSkillTargeter().isTargeting()) {
                    Game.getGameVisual().getSkillTargeter().target(monster);
                }
            }
        });
        final float yOffset = Gdx.graphics.getHeight() / 8f;
        padTop(Functions.random(0, yOffset));
    }

    @Override
    public Monster getBeing() {
        return monster;
    }

    @Override
    public DamageLabel getDamageLabel() {
        return damageLabel;
    }

    @Override
    public Vector2 getCenter() {
        float x = monsterImage.getWidth() / 2f;
        float y = monsterImage.getHeight() / 2f;
        return monsterImage.localToStageCoordinates(new Vector2(x, y));
    }

    @Override
    public HpBar getHpBar() {
        return hpBar;
    }

    @Override
    public StmBar getStmBar() {
        // TODO: 02.08.2021 get monster stm bar
        return null;
    }
}
