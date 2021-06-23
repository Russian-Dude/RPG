package ru.rdude.rpg.game.logic.entities;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import ru.rdude.rpg.game.logic.data.EntityData;

@JsonIdentityInfo(generator = ObjectIdGenerators.UUIDGenerator.class)
public abstract class Entity<T extends EntityData> {

    protected final T entityData;

    public Entity(T entityData) {
        this.entityData = entityData;
    }

    public Entity(long guid) {
        this.entityData = getDataByGuid(guid);
    }

    @JsonProperty("entityData")
    private long getEntityDataGuid() {
        return entityData.getGuid();
    }

    public T getEntityData() {
        return entityData;
    }

    public abstract String getName();

    public boolean sameAs(Entity<?> entity) {
        return entity.entityData.equals(this.entityData);
    }

    protected abstract T getDataByGuid(long guid);

}
