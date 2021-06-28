package ru.rdude.rpg.game.ui;

import com.badlogic.gdx.scenes.scene2d.Actor;
import ru.rdude.rpg.game.logic.data.EntityData;
import ru.rdude.rpg.game.logic.data.ItemData;
import ru.rdude.rpg.game.logic.data.SkillData;
import ru.rdude.rpg.game.logic.enums.EntityReferenceInfo;

import java.util.ArrayList;
import java.util.List;

public final class TooltipInfoFactory {

    private final ItemTooltipInfoFactory itemFactory = new ItemTooltipInfoFactory();
    private final BuffTooltipInfoFactory buffFactory = new BuffTooltipInfoFactory();
    private final SkillTooltipInfoFactory skillFactory = new SkillTooltipInfoFactory();

    public List<Actor> get(EntityData entityData,
                           EntityReferenceInfo referenceInfo,
                           TooltipInfoHolder<?> infoHolder) {

        boolean describer = entityData.isDescriber();

        if (entityData instanceof ItemData) {
            return describer ? itemFactory.getDescriber((ItemData) entityData, referenceInfo, infoHolder)
                    : itemFactory.getConcrete((ItemData) entityData, referenceInfo, infoHolder);
        }

        else if (entityData instanceof SkillData) {
            return describer ? skillFactory.getDescriber((SkillData) entityData, referenceInfo, infoHolder)
                    : skillFactory.getConcrete((SkillData) entityData, referenceInfo, infoHolder);
        }

        else {
            return new ArrayList<>();
        }
    }

}
