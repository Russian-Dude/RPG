package ru.rdude.rpg.game.logic.data.io;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import ru.rdude.rpg.game.logic.game.Game;
import ru.rdude.rpg.game.logic.map.GameMap;

public class GameMapSerializer {

    private final ObjectMapper objectMapper = Game.getCustomObjectMapper();

    public String serialize(GameMap map) {
        String result = null;
        try {
            result = objectMapper.writeValueAsString(map);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return result;
    }

    public GameMap deserialize(String jsonString) {
        GameMap result = null;
        try {
            result = objectMapper.readValue(jsonString, GameMap.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return result;
    }
}
