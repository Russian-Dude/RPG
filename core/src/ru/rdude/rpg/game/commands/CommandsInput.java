package ru.rdude.rpg.game.commands;

import ru.rdude.rpg.game.logic.game.Game;

import java.util.Arrays;

public class CommandsInput {

    public void input(String command) {
        Game.getCurrentGame().getGameLogger().log(command);
        final String[] words = command.toUpperCase().split("\\s+");
        Arrays.stream(Commands.values())
                .filter(com -> com.name().equals(words[0]))
                .findAny()
                .orElse(Commands.NO_SUCH_COMMAND)
                .accept(command);
    }

}
