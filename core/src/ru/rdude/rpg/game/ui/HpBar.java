package ru.rdude.rpg.game.ui;

import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.utils.Align;
import com.fasterxml.jackson.annotation.JsonIgnoreType;
import ru.rdude.rpg.game.logic.entities.beings.Being;
import ru.rdude.rpg.game.logic.entities.skills.Buff;
import ru.rdude.rpg.game.logic.entities.skills.Damage;
import ru.rdude.rpg.game.logic.stats.Stat;
import ru.rdude.rpg.game.logic.stats.StatObserver;
import ru.rdude.rpg.game.logic.stats.secondary.Hp;
import ru.rdude.rpg.game.utils.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@JsonIgnoreType
public class HpBar extends Group implements StatObserver {

    private final Hp hp;

    private final ProgressBar progressBar;
    private final Label current;
    private final Label max;
    private final Label slash;
    private final HorizontalGroup horizontalLabelGroup;

    private final List<Damage> delayedDamages = new ArrayList<>();
    private final Map<Buff, Pair<Double, Double>> delayedBuffs = new HashMap<>();



    public HpBar(Being<?> being) {
        hp = being.stats().hp();
        hp.subscribe(this);
        hp.max().subscribe(this);

        progressBar = new ProgressBar(0, (float) hp.max().value(),
                1, false, UiData.DEFAULT_SKIN, "hp");
        progressBar.setValue((float) hp.value());
        progressBar.setAnimateDuration(0.3f);

        horizontalLabelGroup = new HorizontalGroup();
        slash = new Label(" / ", UiData.DEFAULT_SKIN, UiData.BIG_TEXT_STYLE);
        current = new Label(String.valueOf((int)hp.value()), UiData.DEFAULT_SKIN, UiData.BIG_TEXT_STYLE);
        max = new Label(String.valueOf((int) hp.max().value()), UiData.DEFAULT_SKIN, UiData.BIG_TEXT_STYLE);
        horizontalLabelGroup.addActor(current);
        horizontalLabelGroup.addActor(slash);
        horizontalLabelGroup.addActor(max);
        horizontalLabelGroup.setSize(progressBar.getWidth(), progressBar.getHeight());
        horizontalLabelGroup.align(Align.center);

        addActor(progressBar);
        addActor(horizontalLabelGroup);

        setWidth(progressBar.getWidth());
        setHeight(progressBar.getHeight());

        update(hp);
        update(hp.max());
    }

    public void addDelayed(Damage damage) {
        delayedDamages.add(damage);
    }

    public void addDelayed(Buff buff, double current, double max) {
        delayedBuffs.put(buff, new Pair<>(current, max));
    }

    public void actDelayed(Damage damage) {
        delayedDamages.remove(damage);
        if (damage.isHit()) {
            double value = Math.max(0, progressBar.getValue() - damage.value());
            setCurrent(value);
        }
        delayedDamages.remove(damage);
        noMoreDelayed();
    }

    public void actDelayed(Buff buff) {
        setMax(delayedBuffs.get(buff).getSecond());
        setCurrent(delayedBuffs.get(buff).getFirst());
        delayedBuffs.remove(buff);
        noMoreDelayed();
    }

    private void noMoreDelayed() {
        if (delayedDamages.isEmpty() && delayedBuffs.isEmpty()) {
            setMax(hp.maxValue());
            setCurrent(hp.value());
        }
    }

    private void setCurrent(double value) {
        current.setText(String.valueOf((int) value));
        progressBar.setValue((float) value);
    }

    private void setMax(double value) {
        max.setText(String.valueOf((int) value));
        progressBar.setRange(0, (float) value);
    }

    @Override
    public void update(Stat stat) {
        if (delayedDamages.isEmpty() && delayedBuffs.isEmpty()) {
            if (stat == hp) {
                setCurrent(hp.value());
            }
            else if (stat == hp.max()) {
                setMax(hp.maxValue());
            }
        }
    }
}
