package ru.rdude.rpg.game.ui;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.fasterxml.jackson.annotation.JsonIgnoreType;
import ru.rdude.rpg.game.logic.entities.beings.Being;
import ru.rdude.rpg.game.logic.entities.beings.BeingAction;
import ru.rdude.rpg.game.logic.entities.beings.BeingActionObserver;
import ru.rdude.rpg.game.logic.entities.skills.Buff;
import ru.rdude.rpg.game.logic.entities.skills.BuffObserver;
import ru.rdude.rpg.game.mapVisual.VisualConstants;
import ru.rdude.rpg.game.visual.VisualBeing;

import java.util.HashMap;
import java.util.Map;

@JsonIgnoreType
public class BeingBuffsIcons implements BeingActionObserver, BuffObserver {

    private final static int maxBuffsOnSide = 6;
    private final Side leftSide = new Side();
    private final Side rightSide = new Side();
    private final MorePlayerBuffsWindow buffsWindow = new MorePlayerBuffsWindow();
    private final TextButton moreButton = new TextButton("+", UiData.DEFAULT_SKIN, UiData.YES_SQUARE_BUTTON_STYLE);
    private final Map<Buff, BuffIcon> icons = new HashMap<>();

    public BeingBuffsIcons(VisualBeing<?> visualBeing) {
        visualBeing.getBeing().getBuffs().forEach(this::add);
        visualBeing.getBeing().subscribe(this);

        moreButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                moreButton.getStage().addActor(buffsWindow);
                buffsWindow.setX(((Actor) visualBeing).getX() + (((Actor) visualBeing).getWidth() / 2f) - (buffsWindow.getWidth() / 2f));
                buffsWindow.setY(((Actor) visualBeing).getY() + ((Actor) visualBeing).getHeight() + 10f);
                buffsWindow.setVisible(!buffsWindow.isVisible());
            }
        });
        buffsWindow.setVisible(false);
    }

    public Side leftSide() {
        return leftSide;
    }

    public Side rightSide() {
        return rightSide;
    }

    public MorePlayerBuffsWindow buffsWindow() {
        return buffsWindow;
    }

    private void add(Buff buff) {
        if (buff.getType() == Buff.Type.ABILITY) {
            return;
        }
        BuffIcon icon = new BuffIcon(buff);
        icons.put(buff, icon);
        buff.subscribe(this);
        if (leftSide.size() < maxBuffsOnSide) {
            leftSide.add(icon);
        }
        else {
            if (rightSide.size() < maxBuffsOnSide) {
                rightSide.add(icon);
            }
            else {
                if (buffsWindow.isEmpty()) {
                    buffsWindow.addIcon(rightSide.getFirst());
                    rightSide.addActorAt(0, moreButton);
                }
                buffsWindow.addIcon(icon);
            }
        }
    }

    private void remove(Buff buff) {
        if (buff.getType() == Buff.Type.ABILITY) {
            return;
        }
        leftSide.remove(icons.get(buff));
        rightSide.remove(icons.get(buff));
        icons.remove(buff);
        buff.unsubscribe(this);
        rearrange();
    }

    private void rearrange() {
        while (leftSide.size() < maxBuffsOnSide && rightSide.size() > 0) {
            leftSide.add(rightSide.getFirst());
            if (buffsWindow.isNotEmpty()) {
                if (buffsWindow.iconsAmount() == 1) {
                    rightSide.removeActor(moreButton);
                    rightSide.add(buffsWindow.getFirst());
                }
                else {
                    rightSide.addActorAt(1, buffsWindow.getFirst());
                }
            }
        }
    }

    @Override
    public void update(BeingAction action, Being<?> being) {
        if (action.action() == BeingAction.Action.BUFF_RECEIVE) {
            add((Buff) action.interactor());
        }
    }

    @Override
    public void update(Buff buff, boolean ends) {
        if (ends) {
            remove(buff);
        }
    }

    public class Side extends VerticalGroup {

        public Side() {
            setWidth(VisualConstants.BUFF_ICON_SIZE);
            align(Align.bottom);
            space(2f);
        }

        private int size() {
            return getChildren().size;
        }

        private void add(BuffIcon buffIcon) {
            addActorAt(0, buffIcon);
        }

        private void remove(BuffIcon buffIcon) {
            removeActor(buffIcon);
        }

        private BuffIcon getFirst() {
            Actor first = getChild(0);
            if (first == null || first instanceof BuffIcon) {
                return (BuffIcon) first;
            }
            else if (size() > 1) {
                return (BuffIcon) getChild(1);
            }
            return null;
        }

    }
}
