package ru.rdude.rpg.game.ui;

import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.utils.Align;
import com.fasterxml.jackson.annotation.JsonIgnoreType;
import ru.rdude.rpg.game.logic.entities.beings.Being;
import ru.rdude.rpg.game.logic.entities.beings.Player;
import ru.rdude.rpg.game.logic.entities.skills.Buff;
import ru.rdude.rpg.game.logic.entities.skills.Damage;
import ru.rdude.rpg.game.logic.entities.skills.SkillResult;
import ru.rdude.rpg.game.logic.enums.BeingType;
import ru.rdude.rpg.game.logic.stats.Stat;
import ru.rdude.rpg.game.logic.stats.StatObserver;
import ru.rdude.rpg.game.logic.stats.secondary.Stm;
import ru.rdude.rpg.game.utils.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@JsonIgnoreType
public class StmBar extends Group implements StatObserver {

    private Stm stm;

    private final ProgressBar progressBar;
    private final Label current;
    private final Label max;
    private HorizontalGroup horizontalLabelGroup;

    private final List<SkillResult> delayedResults = new ArrayList<>();
    private final Map<Buff, Pair<Double, Double>> delayedBuffs = new HashMap<>();


    public StmBar(Being<?> being) {
        stm = being.stats().stm();
        stm.subscribe(this);
        stm.max().subscribe(this);

        String type = being instanceof Player || being.beingTypes().getCurrent().contains(BeingType.BOSS) ? "stm" : "stm_small";
        progressBar = new ProgressBar(0, (float) stm.max().value(),
                1, false, UiData.DEFAULT_SKIN, type);
        progressBar.setValue((float) stm.value());
        progressBar.setAnimateDuration(0.3f);

        horizontalLabelGroup = new HorizontalGroup();
        Label slash = new Label(" / ", UiData.DEFAULT_SKIN, UiData.BIG_TEXT_STYLE);
        current = new Label(String.valueOf((int)stm.value()), UiData.DEFAULT_SKIN, UiData.BIG_TEXT_STYLE);
        max = new Label(String.valueOf((int) stm.max().value()), UiData.DEFAULT_SKIN, UiData.BIG_TEXT_STYLE);
        horizontalLabelGroup.addActor(current);
        horizontalLabelGroup.addActor(slash);
        horizontalLabelGroup.addActor(max);
        horizontalLabelGroup.setSize(progressBar.getWidth(), progressBar.getHeight());
        horizontalLabelGroup.align(Align.center);

        addActor(progressBar);
        addActor(horizontalLabelGroup);

        setWidth(progressBar.getWidth());
        setHeight(progressBar.getHeight());
    }

    public void addDelayed(SkillResult skillResult) {
        delayedResults.add(skillResult);
    }

    public void addDelayed(Buff buff, double current, double max) {
        delayedBuffs.put(buff, new Pair<>(current, max));
    }

    public void actDelayed(SkillResult skillResult) {
        delayedResults.remove(skillResult);
        double value = Math.max(0, progressBar.getValue() - skillResult.getSkillData().getStaminaReq());
        setCurrent(value);
        delayedResults.remove(skillResult);
        noMoreDelayed();
    }

    public void actDelayed(Buff buff) {
        final Pair<Double, Double> delayed = delayedBuffs.get(buff);
        if (delayed == null) {
            return;
        }
        setMax(delayed.getSecond());
        setCurrent(delayed.getFirst());
        delayedBuffs.remove(buff);
        noMoreDelayed();
    }

    private void noMoreDelayed() {
        if (delayedResults.isEmpty() && delayedBuffs.isEmpty()) {
            setMax(stm.maxValue());
            setCurrent(stm.value());
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
        if (delayedResults.isEmpty() && delayedBuffs.isEmpty()) {
            if (stat == stm) {
                setCurrent(stm.value());
            }
            else if (stat == stm.max()) {
                setMax(stm.maxValue());
            }
        }
    }
}
