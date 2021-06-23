package ru.rdude.rpg.game.logic.data.io;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.SerializationFeature;
import ru.rdude.rpg.game.logic.data.EntityData;
import ru.rdude.rpg.game.logic.data.SkillData;
import ru.rdude.rpg.game.logic.game.Game;

public class EntityDataSerializer {

    private final CustomObjectMapper objectMapper;

    public EntityDataSerializer() {
        objectMapper = Game.getCustomObjectMapper();
    }

    public String serialize(EntityData entity) {
        String result = null;
        try {
            result = objectMapper.writeValueAsString(entity);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return result;
    }

    public <T extends EntityData> T deserialize(String jsonString, Class<T> cl) {
        T result = null;
        try {
            result = objectMapper.readValue(jsonString, cl);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return result;
    }
}
