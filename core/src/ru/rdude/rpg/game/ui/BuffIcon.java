package ru.rdude.rpg.game.ui;

import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import ru.rdude.rpg.game.logic.entities.skills.Buff;
import ru.rdude.rpg.game.logic.game.Game;
import ru.rdude.rpg.game.mapVisual.VisualConstants;

public class BuffIcon extends Group {

    private final Buff buff;
    private final Image image;
    private final BuffInfoTooltip tooltip;

    public BuffIcon(Buff buff) {
        this.buff = buff;
        image = new Image(Game.getImageFactory().getRegion(buff.getEntityData().getResources().getSkillIcon().getGuid()));
        tooltip = new BuffInfoTooltip(buff);
        addListener(tooltip);
        image.setSize(VisualConstants.BUFF_ICON_SIZE, VisualConstants.BUFF_ICON_SIZE);
        setSize(VisualConstants.BUFF_ICON_SIZE, VisualConstants.BUFF_ICON_SIZE);
        addActor(image);
    }
}
