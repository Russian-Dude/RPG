package ru.rdude.rpg.game.logic.data.io;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import ru.rdude.rpg.game.logic.enums.StatName;
import ru.rdude.rpg.game.logic.stats.Stat;
import ru.rdude.rpg.game.logic.stats.Stats;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;

public class CustomObjectMapper extends ObjectMapper {

    public CustomObjectMapper() {
        super();
        this.enable(SerializationFeature.INDENT_OUTPUT);
        this.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
        createModules();
    }

    private void createModules() {
        createStatsModule();
    }

    private void createStatsModule() {
        com.fasterxml.jackson.databind.JsonSerializer<Stats> jsonSerializer = new JsonSerializer<>() {
            @Override
            public void serialize(Stats stats, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
                jsonGenerator.writeStartObject("Stats");
                jsonGenerator.writeBooleanField("calculatable", stats.isCalculatable());
                stats.forEachWithNestedStats(stat -> {
                    try {
                        jsonGenerator.writeFieldName(StatName.get(stat.getClass()).getName());
                        jsonGenerator.writeStartObject();
                        jsonGenerator.writeNumberField("value", stat.value());
                        jsonGenerator.writeObjectField("buffs", stat.getBuffs());
                        jsonGenerator.writeEndObject();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
                jsonGenerator.writeEndObject();
            }
        };
        JsonDeserializer<Stats> jsonDeserializer = new JsonDeserializer<>() {
            @Override
            public Stats deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
                JsonNode node = jsonParser.getCodec().readTree(jsonParser);
                Stats stats = new Stats(node.get("calculatable").asBoolean());
                Arrays.stream(StatName.values()).forEach(statName -> {
                    Map<String, Double> buffs = CustomObjectMapper.this.convertValue(node.get(statName.getName()).get("buffs"), new TypeReference<>() {
                    });
                    Stat stat = stats.get(statName.getClazz());
                    stat.set(node.get(statName.getName()).get("value").asDouble());
                    stat.setBuffs(buffs);
                });
                return stats;
            }
        };
        SimpleModule simpleModule = new SimpleModule();
        simpleModule.addSerializer(Stats.class, jsonSerializer);
        simpleModule.addDeserializer(Stats.class, jsonDeserializer);
        this.registerModule(simpleModule);
    }
}
