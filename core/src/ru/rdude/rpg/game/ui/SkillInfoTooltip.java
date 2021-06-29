package ru.rdude.rpg.game.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Tooltip;
import com.badlogic.gdx.utils.Align;
import ru.rdude.rpg.game.logic.data.SkillData;
import ru.rdude.rpg.game.logic.game.Game;

import java.util.List;

public class SkillInfoTooltip extends Tooltip<Table> {

    public SkillInfoTooltip(SkillData skillData) {
        super(new Table());

        setInstant(true);
        Table mainTable = getActor();
        mainTable.columnDefaults(0).space(10f);

        TooltipInfoHolder<Label> infoHolder = new TooltipInfoHolder<>(skillData);
        final List<Actor> actors = Game.getTooltipInfoFactory().get(skillData, skillData.getEntityInfo(), infoHolder);

        actors.forEach(actor -> {
            if (actor instanceof Label) {
                final float width = Math.min (((Label) actor).getPrefWidth(), Gdx.graphics.getWidth() / 3f);
                ((Label) actor).setWrap(true);
                ((Label) actor).setAlignment(Align.center);
                mainTable.add(actor).width(width).center().row();
            }
            else {
                mainTable.add(actor).center().row();
            }
        });
        mainTable.pack();
        mainTable.background(UiData.DEFAULT_SKIN.getDrawable("Window_Transparent_9p"));
    }
}
