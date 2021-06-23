package ru.rdude.rpg.game.logic.holders;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import ru.rdude.rpg.game.logic.entities.Entity;
import ru.rdude.rpg.game.utils.jsonextension.JsonPolymorphicSubType;

import java.util.function.Predicate;

@JsonPolymorphicSubType("simpleSlot")
public class SimpleSlot<T extends Entity<?>> extends Slot<T> {

    @JsonCreator
    public SimpleSlot(@JsonProperty("marker") String marker) {
        super(marker, t -> true);
    }

    public SimpleSlot(String marker, Predicate<T>... extraRequirements) {
        super(marker, extraRequirements);
    }
}
