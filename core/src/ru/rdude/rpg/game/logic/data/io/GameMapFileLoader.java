package ru.rdude.rpg.game.logic.data.io;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import ru.rdude.rpg.game.logic.game.Game;
import ru.rdude.rpg.game.logic.map.GameMap;
import ru.rdude.rpg.game.ui.MapInfo;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.nio.file.Files;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class GameMapFileLoader {

    public static GameMap load(FileHandle file) {
        try (ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(file.file()));
             BufferedInputStream bufferedInputStream = new BufferedInputStream(zipInputStream)) {

            ZipEntry entry;
            while ((entry = zipInputStream.getNextEntry()) != null) {
                if (entry.getName().equals("data")) {
                    return Game.getGameJsonSerializer().deserializeGameMap(new String(bufferedInputStream.readAllBytes()));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        throw new IllegalArgumentException("Can not load game map because there is no data entry in the file " + file);
    }

    public static MapInfo loadInfo(FileHandle file) {
        try (ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(file.file()));
             BufferedInputStream bufferedInputStream = new BufferedInputStream(zipInputStream)) {

            ZipEntry entry;

            Image image = null;
            long guid = -1;
            String name = null;
            while ((entry = zipInputStream.getNextEntry()) != null) {
                if (entry.getName().equals("image.png")) {
                    FileHandle imageUnzipped = Gdx.files.local("temp\\images\\unzippedImage");
                    Files.copy(bufferedInputStream, imageUnzipped.file().toPath());
                    image = new Image(new Texture(imageUnzipped));
                    imageUnzipped.delete();
                }
                else if (entry.getName().equals("info")) {
                    ObjectNode infoNode = new ObjectMapper().readValue(new String(bufferedInputStream.readAllBytes()), ObjectNode.class);
                    guid = infoNode.get("guid").asLong();
                    name = infoNode.get("name").asText();
                }
            }
            return new MapInfo(file, guid, name, image);

        } catch (Exception e) {
            e.printStackTrace();
        }
        throw new IllegalArgumentException("Can not load game map from file " + file);
    }
}
