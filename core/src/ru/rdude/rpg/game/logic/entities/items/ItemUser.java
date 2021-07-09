package ru.rdude.rpg.game.logic.entities.items;

import ru.rdude.rpg.game.logic.data.SkillData;
import ru.rdude.rpg.game.logic.entities.beings.Being;
import ru.rdude.rpg.game.logic.entities.beings.BeingAction;
import ru.rdude.rpg.game.logic.entities.skills.SkillUser;
import ru.rdude.rpg.game.logic.game.Game;
import ru.rdude.rpg.game.logic.holders.Slot;

public final class ItemUser {

    public void use(Item item, Being<?> being) {
        switch (item.getEntityData().getItemType().getMainType()) {
            case ARMOR:
            case WEAPON:
                boolean inBackpack = being.backpack().hasEntity(item);
                boolean inEquipment = being.equipment().hasEntity(item);
                if (inBackpack) {
                    being.equipment().slotFor(item.getEntityData().getItemType())
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
                item.getEntityData().getSkillsOnUse()
                        .forEach(skillGuid -> {
                            final SkillData skill = SkillData.getSkillByGuid(skillGuid);
                            Game.getSkillUser().use(skill, being, skill.getMainTarget());
                        });
                item.decreaseAmount(1);
                being.notifySubscribers(new BeingAction(BeingAction.Action.ITEM_USED, item, null, 1), being);
                break;
            case SIMPLE:
                return;
            default:
                throw new IllegalStateException(item.getEntityData().getItemType().getMainType() +  " is not implemented in Skill User");
        }
    }

}
