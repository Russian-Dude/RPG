package ru.rdude.rpg.game.logic.entities.items.holders;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import ru.rdude.rpg.game.logic.entities.Entity;

import java.util.function.Predicate;

@JsonIdentityInfo(generator = ObjectIdGenerators.UUIDGenerator.class)
public interface SlotPredicate<T extends Entity<?>> extends Predicate<T> {
}
