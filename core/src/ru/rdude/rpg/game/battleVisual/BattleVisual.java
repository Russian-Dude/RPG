package ru.rdude.rpg.game.battleVisual;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.fasterxml.jackson.annotation.JsonIgnoreType;
import ru.rdude.rpg.game.logic.entities.beings.Monster;
import ru.rdude.rpg.game.logic.enums.Biom;
import ru.rdude.rpg.game.logic.enums.Relief;
import ru.rdude.rpg.game.logic.game.Game;
import ru.rdude.rpg.game.logic.gameStates.Battle;
import ru.rdude.rpg.game.logic.gameStates.BattleAction;
import ru.rdude.rpg.game.logic.gameStates.BattleObserver;
import ru.rdude.rpg.game.logic.map.Cell;
import ru.rdude.rpg.game.logic.time.TimeManager;
import ru.rdude.rpg.game.ui.BattleVictoryWindow;
import ru.rdude.rpg.game.utils.Functions;
import ru.rdude.rpg.game.visual.GameStateStage;
import ru.rdude.rpg.game.visual.MonsterVisual;
import ru.rdude.rpg.game.visual.VisualBeing;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@JsonIgnoreType
public class BattleVisual extends Stage implements GameStateStage, BattleObserver {

    private Cell cell;
    private Battle battle;

    private List<MonsterVisual> monsterVisuals = new ArrayList<>();

    public BattleVisual(Cell cell, Battle battle) {
        super();
        this.cell = cell;
        this.battle = battle;
        battle.subscribe(this);
        final Table monstersTable = new Table();
        final float groundHeight = Gdx.graphics.getHeight() / 1.5f;
        final Image ground = Game.getImageFactory().getGroundImage(cell);
        final Image background = Game.getImageFactory().getBackgroundImage(cell);
        final TimeManager timeManager = Game.getCurrentGame().getTimeManager();
        final Image sky = Game.getImageFactory().getSkyImage(timeManager.hour(), timeManager.minute());
        ground.setSize(Gdx.graphics.getWidth(), groundHeight);
        background.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight() - groundHeight);
        sky.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight() - groundHeight);
        addActor(sky);
        addActor(background);
        addActor(ground);
        background.setY(groundHeight);
        sky.setY(groundHeight);
        createFarDecorations();
        battle.getEnemySide().streamOnly(Monster.class)
                .map(MonsterVisual::new)
                .forEach(vis -> {
                    monstersTable.add(vis).space(Gdx.graphics.getWidth() / 10f).center();
                    monsterVisuals.add(vis);
                });
        monstersTable.pack();
        addActor(monstersTable);
        createMidDecorations();
        monstersTable.setPosition(Gdx.graphics.getWidth() / 2f - (monstersTable.getWidth() / 2f), Gdx.graphics.getHeight() / 2.1f);
    }

    private void createFarDecorations() {
        if (cell.getRelief() != Relief.FOREST || cell.getBiom() == Biom.WATER) {
            return;
        }
        float size = Gdx.graphics.getWidth() / 12f;
        float posY = Gdx.graphics.getHeight() / 1.5f - size / 5;
        float offsetY = Gdx.graphics.getHeight() / 30f;
        final List<Texture> trees = Game.getImageFactory().getTreesForBiom(cell.getBiom());
        for (float posX = -size / 2; posX < Gdx.graphics.getWidth() + size/2 + 1; posX+= Functions.random(size / 2f, size)) {
            Image image = new Image(Functions.random(trees));
            addActor(image);
            image.setSize(Functions.random(size * 0.7f, size * 1.3f), Functions.random(size * 0.7f, size * 1.3f));
            image.setPosition(posX, posY + Functions.random(-offsetY, offsetY));
        }
        size = Gdx.graphics.getWidth() / 8f;
        posY = Gdx.graphics.getHeight() / 1.5f - size / 2;
        offsetY = Gdx.graphics.getHeight() / 40f;
        for (float posX = -size / 2; posX < Gdx.graphics.getWidth() + size/2 + 1; posX+= Functions.random(size, size * 1.5f)) {
            Image image = new Image(Functions.random(trees));
            addActor(image);
            image.setSize(Functions.random(size * 0.7f, size * 1.3f), Functions.random(size * 0.7f, size * 1.3f));
            image.setPosition(posX, posY + Functions.random(-offsetY, offsetY));
        }
    }

    private void createMidDecorations() {
        if (cell.getRelief() != Relief.FOREST || cell.getBiom() == Biom.WATER) {
            return;
        }
        float size = Gdx.graphics.getWidth() / 3f;
        float posY = Gdx.graphics.getHeight() / 1.5f - size;
        final List<Texture> trees = Game.getImageFactory().getTreesForBiom(cell.getBiom());
        // left
        Image imageLeft = new Image(Functions.random(trees));
        addActor(imageLeft);
        final float leftWidth = Functions.random(size * 0.7f, size * 1.3f);
        imageLeft.setSize(leftWidth, Functions.random(size * 0.7f, size * 1.3f));
        imageLeft.setPosition(- leftWidth / 2f, posY);
        // right
        Image imageRight = new Image(Functions.random(trees));
        addActor(imageRight);
        final float rightWidth = Functions.random(size * 0.7f, size * 1.3f);
        imageRight.setSize(rightWidth, Functions.random(size * 0.7f, size * 1.3f));
        imageRight.setPosition(Gdx.graphics.getWidth() - rightWidth / 2f, posY);
    }

    @Override
    public List<VisualBeing<?>> getVisualBeings() {
        return Stream.of(Game.getGameVisual().getUi().getPlayerVisuals(),
                monsterVisuals)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    @Override
    public void update(BattleAction battleAction) {
        if (battleAction == BattleAction.WIN) {
            final BattleVictoryWindow victoryWindow = Game.getGameVisual().getUi().getBattleVictoryWindow();
            victoryWindow.setItemsReward(battle.createItemsReward());
            victoryWindow.setExpRewards(battleAction.lastExpRewards);
            victoryWindow.setVisible(true);
        }
    }
}
