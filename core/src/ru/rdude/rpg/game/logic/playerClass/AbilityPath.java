package ru.rdude.rpg.game.logic.playerClass;

import ru.rdude.rpg.game.logic.data.AbilityDataCell;
import ru.rdude.rpg.game.utils.jsonextension.JsonPolymorphicSubType;

@JsonPolymorphicSubType("abilityPath")
public enum AbilityPath implements AbilityCell<AbilityPath>, AbilityDataCell<AbilityPath> {

    SN,
    WE,
    SWE,
    NSWE,
    SE,
    SW,
    NWE,
    NW,
    NE,
    NSW,
    NSE;

    @Override
    public AbilityPath getThis() {
        return AbilityCell.super.getThis();
    }
}
