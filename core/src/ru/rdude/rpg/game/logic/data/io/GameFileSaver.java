package ru.rdude.rpg.game.logic.data.io;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import ru.rdude.rpg.game.logic.game.Game;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringReader;
import java.nio.file.Files;

public class GameFileSaver {

    public void save(Game game, String saveName) {
        FileHandle saveFile = Gdx.files.local("saves\\" + saveName + ".save");
        String jsonString = Game.getGameJsonSerializer().serialize(game);
        try (OutputStream outputStream = Files.newOutputStream(saveFile.file().toPath());
             BufferedReader jsonReader = new BufferedReader(new StringReader(jsonString))) {
            int b = jsonReader.read();
            while (b >= 0) {
                outputStream.write(b);
                b = jsonReader.read();
            }
            if (Game.getCurrentGame().getGameLogger() != null) {
                Game.getCurrentGame().getGameLogger().log("Game saved");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
