package ru.rdude.rpg.game.logic.data.io;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.PixmapIO;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import ru.rdude.rpg.game.logic.game.Game;
import ru.rdude.rpg.game.logic.map.GameMap;
import ru.rdude.rpg.game.utils.Functions;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public final class GameMapFileSaver {

    private GameMapFileSaver() {
    }

    public static FileHandle save(GameMap gameMap, Pixmap pixmap) {
        return save(gameMap, pixmap, String.valueOf(gameMap.guid));
    }

    public static FileHandle save(GameMap gameMap, Pixmap pixmap, String name) {
        if (name == null || name.isBlank()) {
            name = String.valueOf(gameMap.guid);
        }
        gameMap.setName(name);
        String fileName = name;
        if (Arrays.stream(Gdx.files.local("maps").list(".map")).anyMatch(file -> file.nameWithoutExtension().equals(fileName))) {
            name += Functions.generateGuid();
        }

        String jsonString = Game.getGameJsonSerializer().serialize(gameMap);

        FileHandle imageTempFile = Gdx.files.local("temp\\map_images\\current_saving.png");
        Path imageTempPath = imageTempFile.file().toPath();
        FileHandle mapFile = Gdx.files.local("maps\\" + name + ".map");
        PixmapIO.writePNG(imageTempFile, pixmap);

        try (OutputStream outputStream = Files.newOutputStream(mapFile.file().toPath());
             ZipOutputStream zipOutputStream = new ZipOutputStream(outputStream);
             BufferedReader jsonReader = new BufferedReader(new StringReader(jsonString))) {

            // image
            zipOutputStream.putNextEntry(new ZipEntry("image.png"));
            Files.copy(imageTempPath, zipOutputStream);
            zipOutputStream.closeEntry();

            // info
            zipOutputStream.putNextEntry(new ZipEntry("info"));
            ObjectMapper infoMapper = new ObjectMapper();
            ObjectNode info = infoMapper.createObjectNode();
            info.put("guid", gameMap.guid);
            info.put("name", gameMap.getName());
            String infoJson = infoMapper.writeValueAsString(info);
            zipOutputStream.write(infoJson.getBytes());
            zipOutputStream.closeEntry();

            // map data
            zipOutputStream.putNextEntry(new ZipEntry("data"));
            int b = jsonReader.read();
            while (b >= 0) {
                zipOutputStream.write(b);
                b = jsonReader.read();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        imageTempFile.delete();
        Game.getMapFiles().put(gameMap.guid, GameMapFileLoader.loadInfo(mapFile));
        return mapFile;
    }
}
