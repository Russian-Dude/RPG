package ru.rdude.rpg.game.logic.map.objects;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import ru.rdude.rpg.game.logic.map.CellSide;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "type"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = Dungeon.class, name = "Dungeon"),
        @JsonSubTypes.Type(value = City.class, name = "City")
})
public abstract class MapObject {

    private long id;
    private CellSide position;

    public MapObject(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public CellSide getPosition() {
        return position;
    }

    public void setPosition(CellSide position) {
        this.position = position;
    }

    public abstract MapObjectRoadAvailability roadAvailability();
}
