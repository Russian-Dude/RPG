package ru.rdude.rpg.game.logic.data.io;

import ru.rdude.rpg.game.logic.data.EntityData;
import ru.rdude.rpg.game.logic.data.Module;
import ru.rdude.rpg.game.logic.data.MonsterData;
import ru.rdude.rpg.game.logic.data.SkillData;
import ru.rdude.rpg.game.logic.game.Game;
import ru.rdude.rpg.game.logic.map.GameMap;

public class GameJsonSerializer {

    private final EntityDataSerializer entityDataSerializer = new EntityDataSerializer();
    private final GameMapSerializer gameMapSerializer = new GameMapSerializer();
    private final GameSerializer gameSerializer = new GameSerializer();

    public String serialize(EntityData entity) {
        return entityDataSerializer.serialize(entity);
    }

    public String serialize(GameMap map) {
        return gameMapSerializer.serialize(map);
    }

    public String serialize(Game game) {
        return gameSerializer.serialize(game);
    }

    public GameMap deserializeGameMap(String jsonString) {
        return gameMapSerializer.deserialize(jsonString);
    }

    public Game deserializeGame(String jsonString) {
        return gameSerializer.deserialize(jsonString);
    }

    public <T extends EntityData> T deSerializeEntityData(String jsonString, Class<T> cl) {
        return entityDataSerializer.deserialize(jsonString, cl);
    }

    public SkillData deSerializeSkillData(String jsonString) {
        return entityDataSerializer.deserialize(jsonString, SkillData.class);
    }

    public Module deSerializeModule(String jsonString) {
        return entityDataSerializer.deserialize(jsonString, Module.class);
    }

    public MonsterData deSerializeMonster(String jsonString) {
        return entityDataSerializer.deserialize(jsonString, MonsterData.class);
    }

}
