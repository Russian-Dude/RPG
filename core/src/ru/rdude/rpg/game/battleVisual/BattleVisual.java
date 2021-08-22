package ru.rdude.rpg.game.battleVisual;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.*;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.fasterxml.jackson.annotation.JsonIgnoreType;
import ru.rdude.rpg.game.logic.actions.IncrementAction;
import ru.rdude.rpg.game.logic.entities.beings.Being;
import ru.rdude.rpg.game.logic.entities.beings.Monster;
import ru.rdude.rpg.game.logic.entities.beings.Party;
import ru.rdude.rpg.game.logic.entities.beings.PartyObserver;
import ru.rdude.rpg.game.logic.enums.Biom;
import ru.rdude.rpg.game.logic.enums.Relief;
import ru.rdude.rpg.game.logic.game.Game;
import ru.rdude.rpg.game.logic.gameStates.Battle;
import ru.rdude.rpg.game.logic.gameStates.BattleAction;
import ru.rdude.rpg.game.logic.gameStates.BattleObserver;
import ru.rdude.rpg.game.logic.map.Cell;
import ru.rdude.rpg.game.logic.time.TimeManager;
import ru.rdude.rpg.game.ui.BattleVictoryWindow;
import ru.rdude.rpg.game.ui.UiData;
import ru.rdude.rpg.game.utils.Functions;
import ru.rdude.rpg.game.visual.GameStateStage;
import ru.rdude.rpg.game.visual.MonsterVisual;
import ru.rdude.rpg.game.visual.VisualBeing;

import java.util.*;
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
                .forEach(monsterVisual -> {
                    monsterVisuals.add(monsterVisual);
                });
        createMidDecorations();

        final Map<MonsterVisual, Float> monstersX = calculateMonstersX();
        monsterVisuals.forEach(monsterVisual -> {
            addActor(monsterVisual);
            final float x = monstersX.get(monsterVisual);
            final float y = Gdx.graphics.getHeight() * 0.6f;
            monsterVisual.setPosition(x, y);
        });
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

    private float calculateSpaceBetweenEnemies() {
        final float defaultSpace = Gdx.graphics.getWidth() / 10f;
        final float availableWidth = Gdx.graphics.getWidth() * 0.9f;
        final float gaps = monsterVisuals.size() - 1;
        final float monstersTotalWidth = monsterVisuals.stream()
                .map(VerticalGroup::getPrefWidth)
                .reduce(Float::sum)
                .orElse(1f);

        final float calculatedSpace = (availableWidth - monstersTotalWidth) / gaps;
        return Math.min(defaultSpace, calculatedSpace);
    }

    private Map<MonsterVisual, Float> calculateMonstersX() {
        final Map<MonsterVisual, Float> result = new HashMap<>();
        final float halfScreen = Gdx.graphics.getWidth() / 2f;
        final float space = calculateSpaceBetweenEnemies();
        final float monstersTotalWidth = monsterVisuals.stream()
                .map(VerticalGroup::getPrefWidth)
                .reduce(Float::sum)
                .orElse(1f);
        final float totalWidth = monstersTotalWidth + space * (monsterVisuals.size() - 1f);
        final float start = halfScreen - totalWidth / 2;

        float currentWidth = 0f;
        for (int i = 0; i < monsterVisuals.size(); i++) {
            final MonsterVisual monsterVisual = monsterVisuals.get(i);
            final float monsterWidth = monsterVisual.getPrefWidth();
            final float position = start + currentWidth + monsterWidth / 2f + space * i;
            currentWidth += monsterWidth;
            result.put(monsterVisual, position);
        }
        return result;
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

    // smoothly add monster visual to battlefield
    public Action addEnemyAndCreateAction(int position, Monster monster) {
        monsterVisuals.stream()
                .filter(monsterVisual -> !monsterVisual.getBeing().isAlive())
                .collect(Collectors.toList())
                .forEach(monsterVisual -> {
                    monsterVisuals.remove(monsterVisual);
                    monsterVisual.remove();
                });

        final MonsterVisual newMonsterVisual = new MonsterVisual(monster);
        monsterVisuals.add(position, newMonsterVisual);

        final Map<MonsterVisual, Float> monstersX = calculateMonstersX();
        final SequenceAction resultAction = Actions.sequence();
        final ParallelAction moveActions = Actions.parallel();
        resultAction.addAction(moveActions);

        monsterVisuals.forEach(monsterVisual -> {
            if (monsterVisual != newMonsterVisual) {
                final MoveToAction moveToAction = Actions.moveTo(monstersX.get(monsterVisual), monsterVisual.getY(), 1f);
                moveToAction.setTarget(monsterVisual);
                moveActions.addAction(moveToAction);
            }
            else {
                newMonsterVisual.setVisible(false);
                addActor(newMonsterVisual);
                newMonsterVisual.setPosition(monstersX.get(newMonsterVisual), Gdx.graphics.getHeight() * 0.6f);
                final AlphaAction fadeOut = Actions.fadeOut(0f);
                fadeOut.setTarget(newMonsterVisual);
                final RunnableAction show = Actions.run(() -> newMonsterVisual.setVisible(true));
                final AlphaAction fadeIn = Actions.fadeIn(1f);
                fadeIn.setTarget(newMonsterVisual);
                resultAction.addAction(fadeOut);
                resultAction.addAction(show);
                resultAction.addAction(fadeIn);
            }
        });

        return resultAction;
    }
}
