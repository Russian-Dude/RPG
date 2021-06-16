package ru.rdude.rpg.game.logic.gameStates;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "Implementation")
@JsonSubTypes({
        @JsonSubTypes.Type(value = Map.class, name = "Map"),
        @JsonSubTypes.Type(value = Camp.class, name = "Camp"),
        @JsonSubTypes.Type(value = Battle.class, name = "Battle")
})
public abstract class GameStateBase {

}
