package ru.rdude.rpg.game.logic.entities.items.holders;

import ru.rdude.rpg.game.logic.entities.beings.Being;
import ru.rdude.rpg.game.utils.jsonextension.JsonPolymorphicSubType;

@JsonPolymorphicSubType("backpack")
public class BackpackSlotsHolder extends ItemSlotsHolder {

    private BackpackSlotsHolder() {}

    public BackpackSlotsHolder(Being<?> being) {
        super(16, null, new BackpackSlotPredicate(being));
    }

}
