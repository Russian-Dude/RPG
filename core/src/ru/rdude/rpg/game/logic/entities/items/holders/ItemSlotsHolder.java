package ru.rdude.rpg.game.logic.entities.items.holders;

import ru.rdude.rpg.game.logic.entities.items.Item;
import ru.rdude.rpg.game.logic.holders.Slot;
import ru.rdude.rpg.game.logic.holders.SlotsHolder;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class ItemSlotsHolder extends SlotsHolder<Item> {

    private ItemSlotsHolder() { }

    public ItemSlotsHolder(int capacity) {
        super(capacity);
    }

    public ItemSlotsHolder(int capacity, String marker, Predicate<Item>... extraRequirements) {
        super(capacity, marker, extraRequirements);
    }

    @Override
    public boolean receiveEntity(Item entity) {
        // increase amount of same items
        List<Item> list = slots.stream()
                .filter(sl -> sl.hasEntity(entity) && sl.getEntity().getAmount() < Item.MAX_AMOUNT)
                .map(Slot::getEntity)
                .collect(Collectors.toList());
        if (!list.isEmpty() && entity.isStackable()) {
            for (Item t : list) {
                entity.setAmount(t.increaseAmountAndReturnRest(entity.getAmount()));
                if (entity.getAmount() == 0)
                    return true;
            }
        }
        // place in empty slots
        Slot<Item> emptySlot;
        while ((emptySlot = findEmptySlot()) != null && entity.getAmount() > 0) {
           if (entity.getAmount() <= Item.MAX_AMOUNT) {
               emptySlot.setEntity(entity);
               return true;
           }
           else {
               Item copy = entity.copy();
               copy.setAmount(0);
               entity.setAmount(copy.increaseAmountAndReturnRest(entity.getAmount()));
               emptySlot.setEntity(copy);
           }
        }
        return false;
    }

    public boolean hasEntity(Item entity, int amount) {
        int currentAmount = 0;
        for (Slot<Item> slot : slots) {
            if (slot.hasEntity(entity)) {
                currentAmount += slot.getEntity().getAmount();
                if (currentAmount >= amount)
                    return true;
            }
        }
        return false;
    }

}
