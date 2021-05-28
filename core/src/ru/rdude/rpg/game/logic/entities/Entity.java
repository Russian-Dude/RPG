package ru.rdude.rpg.game.logic.entities;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import ru.rdude.rpg.game.logic.entities.beings.Monster;
import ru.rdude.rpg.game.logic.entities.beings.Player;
import ru.rdude.rpg.game.logic.entities.items.Item;
import ru.rdude.rpg.game.logic.entities.skills.Buff;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "Implementation")
@JsonSubTypes({
        @JsonSubTypes.Type(value = Monster.class, name = "Monster"),
        @JsonSubTypes.Type(value = Player.class, name = "Player"),
        @JsonSubTypes.Type(value = Buff.class, name = "Buff"),
        @JsonSubTypes.Type(value = Item.class, name = "Item")
})
@JsonIdentityInfo(generator = ObjectIdGenerators.UUIDGenerator.class)
public abstract class Entity {

    public abstract String getName();

}
