package ru.rdude.rpg.game.logic.data.io;

import ru.rdude.rpg.game.logic.data.EntityData;
import ru.rdude.rpg.game.logic.data.SkillData;

public class JsonSerializer {

    private SkillDataSerializer skillDataSerializer;

    public JsonSerializer() {
        skillDataSerializer = new SkillDataSerializer();
    }

    public String serialize(EntityData entity) {
        if (entity instanceof SkillData) {
            return skillDataSerializer.serialize((SkillData) entity);
        } else {
            return null;
        }
    }


    public SkillData deSerializeSkillData(String jsonString) {
        return skillDataSerializer.deserialize(jsonString);
    }
}
