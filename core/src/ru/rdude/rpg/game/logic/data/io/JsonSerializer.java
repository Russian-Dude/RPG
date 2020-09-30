package ru.rdude.rpg.game.logic.data.io;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import ru.rdude.rpg.game.logic.data.SkillData;
import ru.rdude.rpg.game.logic.entities.items.Item;
import ru.rdude.rpg.game.logic.entities.skills.Buff;
import ru.rdude.rpg.game.logic.enums.BuffType;
import ru.rdude.rpg.game.logic.stats.Stat;
import ru.rdude.rpg.game.logic.stats.Stats;

import java.util.HashMap;

public class JsonSerializer {

    ObjectMapper objectMapper;

    public void testMethod() {
        objectMapper = new ObjectMapper();
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        objectMapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
        try {
            SkillData skillData = new SkillData(12);
            String skillDataJson = objectMapper.writeValueAsString(skillData);
            System.out.println(skillDataJson);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
