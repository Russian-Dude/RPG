package ru.rdude.rpg.game.logic.entities.skills;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import ru.rdude.rpg.game.logic.entities.beings.Monster;
import ru.rdude.rpg.game.logic.entities.beings.Player;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "Implementation")
@JsonSubTypes({
        @JsonSubTypes.Type(value = Monster.class, name = "Monster"),
        @JsonSubTypes.Type(value = Player.class, name = "Player")
})
public interface BuffObserver {
    void update(Buff buff, boolean ends);
}
