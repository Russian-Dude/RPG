package ru.rdude.rpg.game.logic.data.io;

import ru.rdude.rpg.game.logic.data.EntityData;
import ru.rdude.rpg.game.logic.data.Module;
import ru.rdude.rpg.game.logic.data.MonsterData;
import ru.rdude.rpg.game.logic.data.SkillData;

public class GameJsonSerializer {

    private EntityDataSerializer entityDataSerializer;

    public GameJsonSerializer() {
        entityDataSerializer = new EntityDataSerializer();
    }

    public String serialize(EntityData entity) {
        return entityDataSerializer.serialize(entity);
    }

    public <T extends EntityData> T deSerializeEntityData(String jsonString, Class<T> cl) {
        return entityDataSerializer.deserialize(jsonString, cl);
    }

    public SkillData deSerializeSkillData(String jsonString) {
        return entityDataSerializer.deserialize(jsonString, SkillData.class);
    }

    public Module deSerializeModule(String jsonString) {
        return entityDataSerializer.deserialize(jsonString, Module.class);
    }

    public MonsterData deSerializeMonster(String jsonString) {
        return entityDataSerializer.deserialize(jsonString, MonsterData.class);
    }

}
