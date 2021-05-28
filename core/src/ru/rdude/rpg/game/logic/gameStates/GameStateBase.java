package ru.rdude.rpg.game.logic.gameStates;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import ru.rdude.rpg.game.logic.entities.beings.Party;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "Implementation")
@JsonSubTypes({
        @JsonSubTypes.Type(value = Map.class, name = "Map"),
        @JsonSubTypes.Type(value = Camp.class, name = "Camp"),
        @JsonSubTypes.Type(value = Battle.class, name = "Battle")
})
public abstract class GameStateBase {

    protected Party playerSide;

    public Party getPlayerSide() {
        return playerSide;
    }
}
