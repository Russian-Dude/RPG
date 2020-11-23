package ru.rdude.rpg.game.logic.data.io;

import ru.rdude.rpg.game.logic.data.EntityData;
import ru.rdude.rpg.game.logic.data.Module;
import ru.rdude.rpg.game.logic.data.SkillData;

public class GameJsonSerializer {

    private SkillDataSerializer skillDataSerializer;
    private ModuleSerializer moduleSerializer;

    public GameJsonSerializer() {
        skillDataSerializer = new SkillDataSerializer();
        moduleSerializer = new ModuleSerializer();
    }

    public String serialize(EntityData entity) {
        if (entity instanceof SkillData) {
            return skillDataSerializer.serialize((SkillData) entity);
        } else if (entity instanceof Module){
            return moduleSerializer.serialize((Module) entity);
        }
        else throw new IllegalArgumentException("Not implemented");
    }


    public SkillData deSerializeSkillData(String jsonString) {
        return skillDataSerializer.deserialize(jsonString);
    }

    public Module deSerializeModule(String jsonString) {
        return moduleSerializer.deserialize(jsonString);
    }

}
