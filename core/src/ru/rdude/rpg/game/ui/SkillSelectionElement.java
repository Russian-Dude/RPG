package ru.rdude.rpg.game.ui;

import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Tree;
import com.badlogic.gdx.utils.Align;
import ru.rdude.rpg.game.logic.data.SkillData;
import ru.rdude.rpg.game.logic.data.resources.Resource;
import ru.rdude.rpg.game.logic.game.Game;
import ru.rdude.rpg.game.utils.Functions;

public class SkillSelectionElement extends Tree.Node<SkillSelectionElement, SkillData, Table>{

    private final SkillData skillData;

    public SkillSelectionElement(SkillData skillData) {
        super();
        this.skillData = skillData;

        Table mainTable = new Table();
        final Resource skillIcon = skillData.getResources().getSkillIcon();
        Image icon = skillIcon == null ? new Image() : new Image(Game.getImageFactory().getRegion(skillIcon.getGuid()));
        Label name = new Label(Functions.capitalize(skillData.getName()), UiData.DEFAULT_SKIN, UiData.SMALL_TEXT_STYLE);
        name.setAlignment(Align.center);
        mainTable.add(icon).size(20f).center();
        mainTable.add(name).width(150f).center();
        mainTable.pack();
        mainTable.background(UiData.DEFAULT_SKIN.getDrawable("Window_Transparent_9p"));

        setValue(skillData);
        setActor(mainTable);

        mainTable.addListener(new SkillInfoTooltip(skillData));
    }
}
