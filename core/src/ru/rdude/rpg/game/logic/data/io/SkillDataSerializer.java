package ru.rdude.rpg.game.logic.data.io;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.SerializationFeature;
import ru.rdude.rpg.game.logic.data.SkillData;

public class SkillDataSerializer {

    private CustomObjectMapper objectMapper;

    public SkillDataSerializer() {
        objectMapper = new CustomObjectMapper();
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        objectMapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
    }

    public String serialize(SkillData entity) {
        String result = null;
        try {
            result = objectMapper.writeValueAsString(entity);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return result;
    }

    public SkillData deserialize(String jsonString) {
        SkillData result = null;
        try {
            result = objectMapper.readValue(jsonString, SkillData.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return result;
    }
}
