package ru.rdude.rpg.game.logic.data.io;

import com.fasterxml.jackson.core.JsonProcessingException;
import ru.rdude.rpg.game.logic.game.Game;

public class GameSerializer {

    private final CustomObjectMapper objectMapper;

    public GameSerializer() {
        objectMapper = Game.getCustomObjectMapper();
    }

    public String serialize(Game game) {
        String result = null;
        try {
            result = objectMapper.writeValueAsString(game);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return result;
    }

    public Game deserialize(String jsonString) {
        Game result = null;
        try {
            result = objectMapper.readValue(jsonString, Game.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return result;
    }
}
