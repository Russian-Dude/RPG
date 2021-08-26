package ru.rdude.rpg.game.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.*;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.fasterxml.jackson.annotation.JsonIgnoreType;
import ru.rdude.rpg.game.logic.entities.beings.*;
import ru.rdude.rpg.game.logic.game.Game;
import ru.rdude.rpg.game.visual.MonsterVisual;
import ru.rdude.rpg.game.visual.VisualBeing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@JsonIgnoreType
public class PlayersVisualBottom extends ScrollPane implements PartyObserver {

    private final List<VisualBeing<?>> visualBeings = new ArrayList<>();
    private final Group mainGroup;

    public PlayersVisualBottom() {
        super(new Group());
        mainGroup = (Group) getActor();
        final Party party = Game.getCurrentGame().getCurrentPlayers();
        party.subscribe(this);
        for (Being<?> being : party.getBeings()) {
            addBeing(being);
        }
        mainGroup.setWidth(Gdx.graphics.getWidth());
        float groupHeight = visualBeings.stream()
                .map(VisualBeing::getPrefHeight)
                .max(Float::compareTo)
                .orElse(1f);
        mainGroup.setHeight(groupHeight);
        mainGroup.setTouchable(Touchable.childrenOnly);
        setWidth(Gdx.graphics.getWidth());
        setHeight(getPrefHeight());
    }

    public List<VisualBeing<?>> getVisualBeings() {
        return visualBeings;
    }

    private VisualBeing<?> createBeingVisual(Being<?> being) {
        if (being instanceof Player) {
            return new PlayerVisual((Player) being);
        }
        else if (being instanceof Minion) {
            return new MonsterVisual((Minion) being, MonsterVisual.Style.ALLY);
        }
        else {
            throw new IllegalArgumentException("Can not add visual being to players visual bottom ui: creating visuals for " + being.getClass() + "is not implemented");
        }
    }

    private void addBeing(Being<?> being) {
        addBeing(mainGroup.getChildren().size, being);
    }

    private void addBeing(int position, Being<?> being) {
        VisualBeing<?> visualBeing = createBeingVisual(being);
        visualBeings.add(position, visualBeing);
        final Map<VisualBeing<?>, Float> beingsX = calculateBeingsX();
        mainGroup.addActor((Actor) visualBeing);
        ((Actor) visualBeing).setX(beingsX.get(visualBeing));
    }

    // smoothly add being visual
    public Action createAddBeingAction(int position, Being<?> being) {
        visualBeings.stream()
                .filter(visualBeing -> !visualBeing.getBeing().isAlive())
                .collect(Collectors.toList())
                .forEach(visualBeing -> {
                    visualBeings.remove(visualBeing);
                    visualBeing.remove();
                });

        final VisualBeing<?> newVisualBeing = createBeingVisual(being);
        visualBeings.add(position, newVisualBeing);

        final Map<VisualBeing<?>, Float> beingsX = calculateBeingsX();
        final SequenceAction resultAction = Actions.sequence();
        final ParallelAction moveActions = Actions.parallel();
        resultAction.addAction(moveActions);

        visualBeings.forEach(visualBeing -> {
            if (visualBeing != newVisualBeing) {
                final MoveToAction moveToAction = Actions.moveTo(beingsX.get(visualBeing), ((Actor) visualBeing).getY(), 1f);
                moveToAction.setTarget((Actor) visualBeing);
                moveActions.addAction(moveToAction);
            }
            else {
                ((Actor) newVisualBeing).setVisible(false);
                mainGroup.addActor((Actor) newVisualBeing);
                ((Actor) newVisualBeing).setPosition(beingsX.get(newVisualBeing), Gdx.graphics.getHeight() * 0.6f);
                final AlphaAction fadeOut = Actions.fadeOut(0f);
                fadeOut.setTarget((Actor) newVisualBeing);
                final RunnableAction show = Actions.run(() -> ((Actor) newVisualBeing).setVisible(true));
                final AlphaAction fadeIn = Actions.fadeIn(1f);
                fadeIn.setTarget((Actor) newVisualBeing);
                resultAction.addAction(fadeOut);
                resultAction.addAction(show);
                resultAction.addAction(fadeIn);
            }
        });

        return resultAction;
    }

    private float calculateSpaceBetweenBeings() {
        final float defaultSpace = Gdx.graphics.getWidth() / 4f;
        final float availableWidth = Gdx.graphics.getWidth() * 0.9f;
        final float gaps = visualBeings.size() - 1;
        final float beingsTotalWidth = visualBeings.stream()
                .map(VisualBeing::getPrefWidth)
                .reduce(Float::sum)
                .orElse(1f);

        final float calculatedSpace = (availableWidth - beingsTotalWidth) / gaps;
        return Math.min(defaultSpace, calculatedSpace);
    }

    private Map<VisualBeing<?>, Float> calculateBeingsX() {
        final Map<VisualBeing<?>, Float> result = new HashMap<>();
        final float halfScreen = Gdx.graphics.getWidth() / 2f;
        final float space = calculateSpaceBetweenBeings();
        final float totalBeingsWidth = visualBeings.stream()
                .map(VisualBeing::getPrefWidth)
                .reduce(Float::sum)
                .orElse(1f);
        final float totalWidth = totalBeingsWidth + space * (visualBeings.size() - 1f);
        final float start = halfScreen - totalWidth / 2;

        float currentWidth = 0f;
        for (int i = 0; i < visualBeings.size(); i++) {
            final VisualBeing<?> visualBeing = visualBeings.get(i);
            final float beingWidth = visualBeing.getPrefWidth();
            final float position = start + currentWidth + beingWidth / 2f + space * i;
            currentWidth += beingWidth;
            result.put(visualBeing, position);
        }
        return result;
    }

    @Override
    public void partyUpdate(Party party, boolean added, Being<?> being, int position) {
        if (added) {
            addBeing(position, being);
        }
        else {
            VisualBeing.VISUAL_BEING_FINDER.find(being).ifPresent(visualBeing -> ((Actor) visualBeing).remove());
        }
    }
}

// code below calculates position of this actor so it not overlap logger.
// so those commented lines are not deleted in case it would be useful

/*
    public void recalculatePositions(LoggerVisual loggerVisual) {
        final float loggerWidth = loggerVisual.getWidth();
        final float defaultSpace = 35f;
        final float gaps = playersGroup.getChildren().size - 1;
        final float totalPlayersWidth = playersGroup.getChildren().get(0).getWidth() * playersGroup.getChildren().size;
        final float availableWidth = Gdx.graphics.getWidth() - loggerWidth;
        final float calculatedSpace = (availableWidth - totalPlayersWidth) / gaps;

        playersGroup.space(Math.min(defaultSpace, calculatedSpace));

        final boolean playersOverlapLogger =
                (Gdx.graphics.getWidth() - (totalPlayersWidth + Math.min(defaultSpace, calculatedSpace) * gaps)) / 2f <= loggerWidth;

        if (playersOverlapLogger) {
            playersGroup.setX((Gdx.graphics.getWidth() + loggerWidth) / 2f - playersGroup.getWidth());
        }
        else {
            playersGroup.setX(Gdx.graphics.getWidth() / 2f - playersGroup.getWidth());
        }
    }
 */


// horizontal group implementation

/*
    private final Set<VisualBeing<?>> visualBeings = new HashSet<>();
    private final HorizontalGroup horizontalGroup;

    public PlayersVisualBottom() {
        super(new HorizontalGroup());
        horizontalGroup = (HorizontalGroup) getActor();
        final Party party = Game.getCurrentGame().getCurrentPlayers();
        party.subscribe(this);
        horizontalGroup.space(60);
        horizontalGroup.align(Align.center);
        for (Being<?> being : party.getBeings()) {
            addBeing(being);
        }
        horizontalGroup.setWidth(Gdx.graphics.getWidth());
        horizontalGroup.setHeight(getPrefHeight());
        setWidth(Gdx.graphics.getWidth());
        setHeight(getPrefHeight());
    }

    public Set<VisualBeing<?>> getVisualBeings() {
        return visualBeings;
    }

    private void addBeing(Being<?> being) {
        addBeing(horizontalGroup.getChildren().size, being);
    }

    private void addBeing(int position, Being<?> being) {
        if (being instanceof Player) {
            final PlayerVisual actor = new PlayerVisual((Player) being);
            horizontalGroup.addActorAt(position, actor);
            visualBeings.add(actor);
        }
        else if (being instanceof Minion) {
            final MonsterVisual actor = new MonsterVisual((Minion) being);
            horizontalGroup.addActorAt(position, actor);
            visualBeings.add(actor);
        }
    }

    @Override
    public void partyUpdate(Party party, boolean added, Being<?> being, int position) {
        if (added) {
            addBeing(position, being);
        }
        else {
            VisualBeing.VISUAL_BEING_FINDER.find(being).ifPresent(visualBeing -> ((Actor) visualBeing).remove());
        }
    }
 */
