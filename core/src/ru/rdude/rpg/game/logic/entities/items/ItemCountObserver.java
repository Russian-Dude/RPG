package ru.rdude.rpg.game.logic.entities.items;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

@JsonIdentityInfo(generator = ObjectIdGenerators.UUIDGenerator.class)
public interface ItemCountObserver {

    void update(int amount, Item item);

}
