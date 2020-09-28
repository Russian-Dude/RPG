package ru.rdude.rpg.game.logic.data.io;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import ru.rdude.rpg.game.logic.entities.items.Item;
import ru.rdude.rpg.game.logic.entities.skills.Buff;
import ru.rdude.rpg.game.logic.stats.Stat;
import ru.rdude.rpg.game.logic.stats.Stats;

public class JsonSerializer {

    ObjectMapper objectMapper;

    public void testMethod() {
        objectMapper = new ObjectMapper();
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        objectMapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
        try {
            String jsonStats = objectMapper.writeValueAsString(new StatsJsonSerializer(new Stats(true)));
            System.out.println(jsonStats);
            StatsJsonSerializer desStats = objectMapper.readValue(jsonStats, StatsJsonSerializer.class);
            System.out.println("====");
            desStats.getStats().agi().addBuffClass(Buff.class);
            desStats.getStats().agi().addBuffClass(Item.class);
            desStats.getStats().agi().increaseBuffValue(Item.class, 4d);
            System.out.println(objectMapper.writeValueAsString(desStats));
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }


}
