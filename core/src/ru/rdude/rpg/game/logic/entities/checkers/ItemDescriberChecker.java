package ru.rdude.rpg.game.logic.entities.checkers;

import ru.rdude.rpg.game.logic.data.ItemData;

class ItemDescriberChecker extends DescriberChecker<ItemData> {

    @Override
    protected ItemData getDataByGuid(long guid) {
        return null;
    }

    @Override
    public boolean check(ItemData describer, ItemData entityData) {
        // rarity
        if (describer.getRarity() != null && describer.getRarity() != entityData.getRarity()) {
            return false;
        }
        // main type
        if (describer.getItemMainType() != null && describer.getItemMainType() != entityData.getItemMainType()) {
            return false;
        }
        // type
        if (describer.getItemType() != null && describer.getItemType() != entityData.getItemType()) {
            return false;
        }
        // elements
        if (describer.getElements() != null && !entityData.getElements().containsAll(describer.getElements())) {
            return false;
        }
        return true;
    }
}
