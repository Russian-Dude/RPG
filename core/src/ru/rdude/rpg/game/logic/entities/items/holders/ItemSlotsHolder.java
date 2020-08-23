package ru.rdude.rpg.game.logic.entities.items.holders;

import ru.rdude.rpg.game.logic.entities.items.Item;
import ru.rdude.rpg.game.logic.holders.Slot;
import ru.rdude.rpg.game.logic.holders.SlotsHolder;

import java.util.List;
import java.util.stream.Collectors;

public class ItemSlotsHolder<T extends Item> extends SlotsHolder<T> {


    public ItemSlotsHolder(int capacity, Class<T> requiredClass) {
        super(capacity, requiredClass);
    }

    protected ItemSlotsHolder(int capacity) {
        super(capacity, (Class<T>) Item.class);
    }

    @Override
    public boolean receiveEntity(T entity) {
        // increase amount of same items
        List<T> list = slots.stream()
                .filter(sl -> sl.hasEntity(entity) && sl.getEntity().getAmount() < Item.MAX_AMOUNT)
                .map(Slot::getEntity)
                .collect(Collectors.toList());
        if (!list.isEmpty() && entity.isStackable()) {
            for (T t : list) {
                entity.setAmount(t.increaseAmountAndReturnRest(entity.getAmount()));
                if (entity.getAmount() == 0)
                    return true;
            }
        }
        // place in empty slots
        Slot<T> emptySlot;
        while ((emptySlot = findEmptySlot()) != null && entity.getAmount() > 0) {
           if (entity.getAmount() <= Item.MAX_AMOUNT) {
               emptySlot.setEntity(entity);
               return true;
           }
           else {
               Item copy = entity.copy();
               copy.setAmount(0);
               entity.setAmount(copy.increaseAmountAndReturnRest(entity.getAmount()));
               emptySlot.setEntity((T) copy);
           }
        }
        return false;
    }

    public boolean hasEntity(T entity, int amount) {
        int currentAmount = 0;
        for (Slot<T> slot : slots) {
            if (slot.hasEntity(entity)) {
                currentAmount += slot.getEntity().getAmount();
                if (currentAmount >= amount)
                    return true;
            }
        }
        return false;
    }

}
