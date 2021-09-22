package ru.rdude.rpg.game.ui;

import com.badlogic.gdx.scenes.scene2d.Actor;
import ru.rdude.rpg.game.logic.data.EntityData;
import ru.rdude.rpg.game.logic.data.ItemData;
import ru.rdude.rpg.game.logic.data.MonsterData;
import ru.rdude.rpg.game.logic.data.SkillData;
import ru.rdude.rpg.game.logic.enums.EntityReferenceInfo;
import ru.rdude.rpg.game.logic.gameStates.Map;
import ru.rdude.rpg.game.logic.playerClass.Ability;
import ru.rdude.rpg.game.logic.playerClass.PlayerClass;

import java.util.ArrayList;
import java.util.List;

public final class TooltipInfoFactory {

    private final ItemTooltipInfoFactory itemFactory = new ItemTooltipInfoFactory();
    private final BuffTooltipInfoFactory buffFactory = new BuffTooltipInfoFactory();
    private final SkillTooltipInfoFactory skillFactory = new SkillTooltipInfoFactory();
    private final MonsterTooltipInfoFactory monsterFactory = new MonsterTooltipInfoFactory();
    private final AbilityTooltipInfoFactory abilityFactory = new AbilityTooltipInfoFactory();

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

        else if (entityData instanceof MonsterData) {
            return describer ? monsterFactory.getDescriber((MonsterData) entityData, referenceInfo, infoHolder)
                    : monsterFactory.getConcrete((MonsterData) entityData, referenceInfo, infoHolder);
        }

        else {
            return new ArrayList<>();
        }
    }

    public List<Actor> get(Ability ability, PlayerClass playerClass, EntityReferenceInfo referenceInfo, TooltipInfoHolder<?> infoHolder) {
        return abilityFactory.get(ability, playerClass, referenceInfo, infoHolder);
    }

    public List<Actor> get(Map.MonstersOnCell monsters) {
        return monsterFactory.getOnMap(monsters);
    }

}
