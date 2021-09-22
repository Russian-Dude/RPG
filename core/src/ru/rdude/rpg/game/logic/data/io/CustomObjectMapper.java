package ru.rdude.rpg.game.logic.data.io;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.module.SimpleModule;
import ru.rdude.rpg.game.logic.enums.UsedByStatistics;
import ru.rdude.rpg.game.utils.jsonextension.PolymorphicObjectMapper;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class CustomObjectMapper extends PolymorphicObjectMapper {

    public CustomObjectMapper(String... packages) {
        super(packages);
        createUsedByStatisticsDeserializer();
    }


    private void createUsedByStatisticsDeserializer() {

        Map<String, ? extends UsedByStatistics> nameToEnumMap = reflections.values().stream()
                .flatMap(refl -> refl.getSubTypesOf(UsedByStatistics.class).stream())
                .filter(Class::isEnum)
                .flatMap(cl -> Arrays.stream(cl.getEnumConstants()))
                .collect(Collectors.toMap(en -> ((Enum<? extends UsedByStatistics>) en).name(), Function.identity()));

        JsonDeserializer<UsedByStatistics> jsonDeserializer = new JsonDeserializer<>() {
            @Override
            public UsedByStatistics deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
                JsonNode node = p.getCodec().readTree(p);
                return nameToEnumMap.get(node.asText());
            }
        };

        SimpleModule module = new SimpleModule();
        module.addDeserializer(UsedByStatistics.class, jsonDeserializer);
        registerModule(module);
    }
}
