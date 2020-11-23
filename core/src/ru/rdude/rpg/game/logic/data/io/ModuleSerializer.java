package ru.rdude.rpg.game.logic.data.io;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.SerializationFeature;
import ru.rdude.rpg.game.logic.data.Module;
import ru.rdude.rpg.game.logic.data.SkillData;

public class ModuleSerializer {

    private CustomObjectMapper objectMapper;

    public ModuleSerializer() {
        objectMapper = new CustomObjectMapper();
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        objectMapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
    }

    public String serialize(Module entity) {
        String result = null;
        try {
            result = objectMapper.writeValueAsString(entity);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return result;
    }

    public Module deserialize(String jsonString) {
        Module result = null;
        try {
            result = objectMapper.readValue(jsonString, Module.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return result;
    }
}
