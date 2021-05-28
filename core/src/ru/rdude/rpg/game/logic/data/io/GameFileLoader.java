package ru.rdude.rpg.game.logic.data.io;

import com.badlogic.gdx.files.FileHandle;
import ru.rdude.rpg.game.logic.game.Game;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;

public class GameFileLoader {

    public Game load(FileHandle file) {
        try (BufferedInputStream inputStream = new BufferedInputStream(new FileInputStream(file.file()))) {
            return Game.getGameJsonSerializer().deserializeGame(new String(inputStream.readAllBytes()));
        }
        catch (IOException e) {
            e.printStackTrace();
            throw new IllegalStateException("Can not load game save");
        }
    }

}
