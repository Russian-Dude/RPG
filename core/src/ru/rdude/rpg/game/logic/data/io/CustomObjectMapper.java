package ru.rdude.rpg.game.logic.data.io;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import ru.rdude.rpg.game.logic.coefficients.Coefficients;
import ru.rdude.rpg.game.logic.enums.AttackType;
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
        createCoefficientsModule();
    }

    private void createStatsModule() {
        JsonSerializer<Stats> jsonSerializer = new JsonSerializer<>() {
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

    private void createCoefficientsModule() {
        JsonSerializer<Coefficients> jsonSerializer = new JsonSerializer<>() {
            @Override
            public void serialize(Coefficients coefficients, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
                jsonGenerator.writeStartObject("Coefficients");

                jsonGenerator.writeFieldName("Atk");
                jsonGenerator.writeStartObject();
                jsonGenerator.writeObjectField("Attack type", coefficients.atk().attackType().getCoefficientsMap());
                jsonGenerator.writeObjectField("Being type", coefficients.atk().beingType().getCoefficientsMap());
                jsonGenerator.writeObjectField("Element", coefficients.atk().element().getCoefficientsMap());
                jsonGenerator.writeObjectField("Size", coefficients.atk().size().getCoefficientsMap());
                jsonGenerator.writeEndObject();

                jsonGenerator.writeFieldName("Def");
                jsonGenerator.writeStartObject();
                jsonGenerator.writeObjectField("Attack type", coefficients.def().attackType().getCoefficientsMap());
                jsonGenerator.writeObjectField("Being type", coefficients.def().beingType().getCoefficientsMap());
                jsonGenerator.writeObjectField("Element", coefficients.def().element().getCoefficientsMap());
                jsonGenerator.writeObjectField("Size", coefficients.def().size().getCoefficientsMap());
                jsonGenerator.writeEndObject();

                jsonGenerator.writeEndObject();
            }
        };

        JsonDeserializer<Coefficients> jsonDeserializer = new JsonDeserializer<>() {
            @Override
            public Coefficients deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
                JsonNode node = jsonParser.getCodec().readTree(jsonParser);
                Coefficients coefficients = new Coefficients();
                coefficients.atk().attackType().setCoefficientsMap(
                        CustomObjectMapper.this.convertValue(node.get("Coefficients").get("Atk").get("Attack type"), new TypeReference<>() {
                        }));
                coefficients.def().attackType().setCoefficientsMap(
                        CustomObjectMapper.this.convertValue(node.get("Coefficients").get("Def").get("Attack type"), new TypeReference<>() {
                        }));
                coefficients.atk().beingType().setCoefficientsMap(
                        CustomObjectMapper.this.convertValue(node.get("Coefficients").get("Atk").get("Being type"), new TypeReference<>() {
                        }));
                coefficients.def().beingType().setCoefficientsMap(
                        CustomObjectMapper.this.convertValue(node.get("Coefficients").get("Def").get("Being type"), new TypeReference<>() {
                        }));
                coefficients.atk().element().setCoefficientsMap(
                        CustomObjectMapper.this.convertValue(node.get("Coefficients").get("Atk").get("Element"), new TypeReference<>() {
                        }));
                coefficients.def().element().setCoefficientsMap(
                        CustomObjectMapper.this.convertValue(node.get("Coefficients").get("Def").get("Element"), new TypeReference<>() {
                        }));
                coefficients.atk().size().setCoefficientsMap(
                        CustomObjectMapper.this.convertValue(node.get("Coefficients").get("Atk").get("Size"), new TypeReference<>() {
                        }));
                coefficients.def().size().setCoefficientsMap(
                        CustomObjectMapper.this.convertValue(node.get("Coefficients").get("Def").get("Size"), new TypeReference<>() {
                        }));
                return coefficients;
            }
        };

        SimpleModule simpleModule = new SimpleModule();
        simpleModule.addSerializer(Coefficients.class, jsonSerializer);
        simpleModule.addDeserializer(Coefficients.class, jsonDeserializer);
        this.registerModule(simpleModule);
    }
}
