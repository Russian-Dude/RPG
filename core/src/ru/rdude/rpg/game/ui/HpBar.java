package ru.rdude.rpg.game.ui;

import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import ru.rdude.rpg.game.logic.entities.beings.Being;
import ru.rdude.rpg.game.logic.stats.Stat;
import ru.rdude.rpg.game.logic.stats.StatObserver;
import ru.rdude.rpg.game.logic.stats.secondary.Hp;

public class HpBar extends Group implements StatObserver {

    private Hp hp;

    private ProgressBar progressBar;
    private Label current;
    private Label max;
    private Label slash;
    private HorizontalGroup horizontalLabelGroup;



    public HpBar(Being being) {
        hp = being.stats().hp();
        hp.subscribe(this);
        hp.max().subscribe(this);

        progressBar = new ProgressBar(0, (float) hp.max().value(),
                1, false, UiData.DEFAULT_SKIN, "hp");
        progressBar.setValue((float) hp.value());

        horizontalLabelGroup = new HorizontalGroup();
        slash = new Label(" / ", UiData.DEFAULT_SKIN);
        current = new Label(String.valueOf((int)hp.value()), UiData.DEFAULT_SKIN);
        max = new Label(String.valueOf((int) hp.max().value()), UiData.DEFAULT_SKIN);
        horizontalLabelGroup.addActor(current);
        horizontalLabelGroup.addActor(slash);
        horizontalLabelGroup.addActor(max);
        horizontalLabelGroup.setSize(progressBar.getWidth(), progressBar.getHeight());

        addActor(progressBar);
        addActor(horizontalLabelGroup);

        setWidth(progressBar.getWidth());
        setHeight(progressBar.getHeight());

        update(hp);
        update(hp.max());
    }


    @Override
    public void update(Stat stat) {
        if (stat == hp) {
            current.setText(String.valueOf((int) hp.value()));
            progressBar.setValue((float) hp.value());
        }
        else if (stat == hp.max()) {
            max.setText(String.valueOf((int) hp.max().value()));
            progressBar.setRange(0, (float) hp.max().value());
        }
    }
}
