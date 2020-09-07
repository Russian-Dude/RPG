package ru.rdude.rpg.game.logic.holders;

import ru.rdude.rpg.game.logic.entities.Entity;

public interface SlotObserver {

    void update(Slot<?> slot, Entity entity);

}
