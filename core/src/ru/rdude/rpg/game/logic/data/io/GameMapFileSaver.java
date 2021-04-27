package ru.rdude.rpg.game.logic.data.io;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.PixmapIO;
import com.badlogic.gdx.graphics.Texture;
import ru.rdude.rpg.game.logic.game.Game;
import ru.rdude.rpg.game.logic.map.GameMap;

public final class GameMapFileSaver {

    private GameMapFileSaver() { }

    public static void save(GameMap gameMap, Texture texture) {
        save(gameMap, texture, String.valueOf(gameMap.guid));
    }

    public static void save(GameMap gameMap, Texture texture, String name) {
        if (name == null || name.isBlank()) {
            name = String.valueOf(gameMap.guid);
        }
        String jsonString = Game.getCurrentGame().getGameJsonSerializer().serialize(gameMap);
        if (!texture.getTextureData().isPrepared()) {
            texture.getTextureData().prepare();
        }
        Pixmap pixmap = texture.getTextureData().consumePixmap();
        PixmapIO.writePNG(Gdx.files.local("map\\" + name + ".png"), pixmap);
    }
}
