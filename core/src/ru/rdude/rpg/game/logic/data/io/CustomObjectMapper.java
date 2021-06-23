package ru.rdude.rpg.game.logic.data.io;

import ru.rdude.rpg.game.utils.jsonextension.PolymorphicObjectMapper;

public class CustomObjectMapper extends PolymorphicObjectMapper {

    public CustomObjectMapper(String... packages) {
        super(packages);
    }
}
