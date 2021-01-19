package ru.rdude.rpg.game.ui;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import ru.rdude.rpg.game.logic.entities.beings.Being;
import ru.rdude.rpg.game.logic.stats.Stat;
import ru.rdude.rpg.game.logic.stats.StatObserver;
import ru.rdude.rpg.game.logic.stats.primary.*;

public class StatVisual extends Table implements StatObserver {

    private final Stat stat;
    private boolean isDecimal;
    private final Label value;
    private final Label pureValue;
    private Stat statPoints;
    private Being being;
    private Button plusButton;
    private Button minusButton;


    public StatVisual(Being being, Stat stat) {
        this(being, stat, true);
    }

    public StatVisual(Stat stat) {
        this(null, stat, false);
    }

    public StatVisual(Being being, Stat stat, boolean canIncrease) {
        this(being, stat, canIncrease, false);
    }

    public StatVisual(Being being, Stat stat, boolean canIncrease, boolean isDecimal) {
        this(being, stat, canIncrease, isDecimal, false);
    }

    public StatVisual(Being being, Stat stat, boolean canIncrease, boolean isDecimal, boolean canDecrease) {

        // create
        this.stat = stat;
        this.isDecimal = isDecimal;
        Label name;
        if (isStatSecondary()) {
            name = new Label(stat.getName(), UiData.DEFAULT_SKIN, UiData.SMALL_TEXT_STYLE);
            value = new Label(String.valueOf(stat.value()), UiData.DEFAULT_SKIN, UiData.SMALL_TEXT_STYLE);
            pureValue = new Label("", UiData.DEFAULT_SKIN, UiData.SMALL_TEXT_STYLE);
        }
        else {
            name = new Label(stat.getName(), UiData.DEFAULT_SKIN, UiData.BIG_TEXT_STYLE);
            value = new Label(String.valueOf(stat.value()), UiData.DEFAULT_SKIN, UiData.BIG_TEXT_STYLE);
            pureValue = new Label("", UiData.DEFAULT_SKIN, UiData.BIG_TEXT_STYLE);
        }
        if (canIncrease) {
            this.statPoints = being.stats().lvl().statPoints();
            this.being = being;
            this.plusButton = new TextButton("+", UiData.DEFAULT_SKIN, UiData.YES_SQUARE_BUTTON_STYLE);
        }
        if (canDecrease) {
            this.minusButton = new TextButton("-", UiData.DEFAULT_SKIN, UiData.NO_SQUARE_BUTTON_STYLE);
        }
        // add
        row().spaceRight(10);
        add(name).center().expand(false, false);
        if (canDecrease) {
            add(minusButton);
        }
        add(value).padLeft(10).align(Align.left).minWidth(20).expand(false, false).minWidth(canIncrease ? 15 : 50);
        add(pureValue);
        if (canIncrease) {
            add(plusButton);
        }
        pack();
        align(Align.center);

        // button action
        if (canIncrease) {
            plusButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    if (statPoints.value() < 1) {
                        return;
                    }
                    stat.increase(1);
                    statPoints.decrease(1);
                }
            });
        }
        if (canDecrease) {
            minusButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    if (stat.value() < 1) {
                        return;
                    }
                    stat.decrease(1);
                    statPoints.increase(1);
                }
            });
        }

        // subscribe
        stat.subscribe(this);
        if (canIncrease) {
            statPoints.subscribe(this);
        }

        // update
        update(stat);
        if (canIncrease) {
            update(statPoints);
        }
    }

    @Override
    public void update(Stat stat) {
        if (stat == this.stat) {
            if (minusButton != null) {
                minusButton.setVisible(stat.value() > 0);
            }
            if (stat instanceof Lvl.Exp) {
                value.setText((int) (stat.value()) + " / " + (int) (((Lvl.Exp) stat)).getMax());
            }
            else {
                // somehow ternal operator does not work here
                if (isDecimal) {
                    value.setText(String.format("%.1f", stat.value()) + "%");
                }
                else {
                    value.setText(String.valueOf((int) stat.value()));
                }
                if (stat.value() == stat.pureValue()) {
                    pureValue.setText("");
                }
                else {
                    if (isDecimal) {
                        pureValue.setText("(" + String.format("%.1f", stat.pureValue()) + ")");
                    }
                    else {
                        pureValue.setText("(" + (int) stat.pureValue() + ")");
                    }
                }
            }
        }
        else if (stat == this.statPoints) {
            plusButton.setVisible(stat.value() > 0);
        }
    }

    private boolean isStatSecondary() {
        return !(stat instanceof Agi)
                && !(stat instanceof Dex)
                && !(stat instanceof Int)
                && !(stat instanceof Luck)
                && !(stat instanceof Str)
                && !(stat instanceof Vit)
                && !(stat instanceof Lvl)
                && !(stat instanceof Lvl.Exp)
                && !(stat instanceof Lvl.Points);
    }
}
