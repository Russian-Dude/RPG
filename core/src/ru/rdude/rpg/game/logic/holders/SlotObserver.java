package ru.rdude.rpg.game.logic.holders;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import ru.rdude.rpg.game.logic.entities.Entity;

@JsonIdentityInfo(generator = ObjectIdGenerators.UUIDGenerator.class)
public interface SlotObserver {

    void update(Slot<?> slot, Entity<?> oldEntity, Entity<?> newEntity);

}
