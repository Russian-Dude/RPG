package ru.rdude.rpg.game.ui;

import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.utils.Align;
import com.fasterxml.jackson.annotation.JsonIgnoreType;
import ru.rdude.rpg.game.logic.stats.Stat;
import ru.rdude.rpg.game.logic.stats.StatObserver;
import ru.rdude.rpg.game.logic.stats.primary.Lvl;

@JsonIgnoreType
public class ExpBar extends Group implements StatObserver {

    private final Lvl.Exp exp;

    private final ProgressBar progressBar;
    private final Label current;
    private final Label max;
    private final Label slash;
    private final HorizontalGroup horizontalLabelGroup;

    public ExpBar(Lvl.Exp exp) {
        this.exp = exp;
        exp.subscribe(this);
        exp.getMax().subscribe(this);

        progressBar = new ProgressBar(0, ((float) exp.getMax().value()), 1, false, UiData.DEFAULT_SKIN, "exp");
        progressBar.setValue((float) exp.value());
        progressBar.setAnimateDuration(0.3f);

        horizontalLabelGroup = new HorizontalGroup();
        slash = new Label(" / ", UiData.DEFAULT_SKIN, UiData.BIG_TEXT_STYLE);
        current = new Label(String.valueOf((int)exp.value()), UiData.DEFAULT_SKIN, UiData.BIG_TEXT_STYLE);
        max = new Label(String.valueOf((int) exp.getMax().value()), UiData.DEFAULT_SKIN, UiData.BIG_TEXT_STYLE);
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

    @Override
    public void setWidth(float width) {
        super.setWidth(width);
        getChildren().forEach(child -> child.setWidth(width));
    }

    @Override
    public void update(Stat stat) {
        if (stat == exp.getMax()) {
            progressBar.setRange(0, (float) exp.getMax().value());
            max.setText(String.valueOf((int) exp.getMax().value()));
        }
        if (stat == exp) {
            progressBar.setValue((float) exp.value());
            current.setText(String.valueOf((int) exp.value()));
        }
    }
}
