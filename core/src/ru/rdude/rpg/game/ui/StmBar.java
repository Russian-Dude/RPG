package ru.rdude.rpg.game.ui;

import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.utils.Align;
import com.fasterxml.jackson.annotation.JsonIgnoreType;
import ru.rdude.rpg.game.logic.entities.beings.Being;
import ru.rdude.rpg.game.logic.stats.Stat;
import ru.rdude.rpg.game.logic.stats.StatObserver;
import ru.rdude.rpg.game.logic.stats.secondary.Hp;
import ru.rdude.rpg.game.logic.stats.secondary.Stm;

@JsonIgnoreType
public class StmBar extends Group implements StatObserver {

    private Stm stm;

    private ProgressBar progressBar;
    private Label current;
    private Label max;
    private Label slash;
    private HorizontalGroup horizontalLabelGroup;



    public StmBar(Being being) {
        stm = being.stats().stm();
        stm.subscribe(this);
        stm.max().subscribe(this);

        progressBar = new ProgressBar(0, (float) stm.max().value(),
                1, false, UiData.DEFAULT_SKIN, "stm");
        progressBar.setValue((float) stm.value());
        progressBar.setAnimateDuration(0.3f);

        horizontalLabelGroup = new HorizontalGroup();
        slash = new Label(" / ", UiData.DEFAULT_SKIN, UiData.BIG_TEXT_STYLE);
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


    @Override
    public void update(Stat stat) {
        if (stat == stm) {
            current.setText(String.valueOf((int) stm.value()));
            progressBar.setValue((float) stm.value());
        }
        else if (stat == stm.max()) {
            max.setText(String.valueOf((int) stm.max().value()));
            progressBar.setRange(0, (float) stm.max().value());
        }
    }
}
