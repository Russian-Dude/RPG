package ru.rdude.rpg.game.logic.gameStates;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import ru.rdude.rpg.game.logic.entities.beings.ExpSpreader;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "Implementation")
@JsonSubTypes({
        @JsonSubTypes.Type(value = ExpSpreader.class, name = "ExpSpreader")
})
public interface GameStateObserver {

    void update(GameStateBase oldValue, GameStateBase newValue);

}
