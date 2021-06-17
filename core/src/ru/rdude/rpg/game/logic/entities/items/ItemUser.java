package ru.rdude.rpg.game.logic.entities.items;

import ru.rdude.rpg.game.logic.data.SkillData;
import ru.rdude.rpg.game.logic.entities.beings.Being;
import ru.rdude.rpg.game.logic.entities.beings.BeingAction;
import ru.rdude.rpg.game.logic.entities.skills.SkillUser;
import ru.rdude.rpg.game.logic.holders.Slot;

public final class ItemUser {

    public void use(Item item, Being being) {
        switch (item.getItemData().getItemType().getMainType()) {
            case ARMOR:
            case WEAPON:
                boolean inBackpack = being.backpack().hasEntity(item);
                boolean inEquipment = being.equipment().hasEntity(item);
                if (inBackpack) {
                    being.equipment().slotFor(item.getItemData().getItemType())
                            .ifPresent(slot -> slot.swapEntities(Slot.withEntity(item)));
                }
                else if (inEquipment) {
                    being.backpack().findEmptySlot().ifPresent(slot -> {
                        Slot.withEntity(item).setEntity(null);
                        slot.setEntity(item);
                    });
                }
                else {
                    being.receive(item);
                }
                break;
            case USABLE:
                item.getItemData().getSkillsOnUse()
                        .forEach(skillGuid -> SkillUser.use(SkillData.getSkillByGuid(skillGuid), being));
                item.decreaseAmount(1);
                being.notifySubscribers(new BeingAction(BeingAction.Action.ITEM_USED, item, null, 1), being);
                break;
            case SIMPLE:
                return;
            default:
                throw new IllegalStateException(item.itemData.getItemType().getMainType() +  " is not implemented in Skill User");
        }
    }

}