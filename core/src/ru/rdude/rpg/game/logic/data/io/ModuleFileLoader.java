package ru.rdude.rpg.game.logic.data.io;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import ru.rdude.rpg.game.logic.data.ItemData;
import ru.rdude.rpg.game.logic.data.Module;
import ru.rdude.rpg.game.logic.data.MonsterData;
import ru.rdude.rpg.game.logic.data.SkillData;
import ru.rdude.rpg.game.logic.game.Game;
import ru.rdude.rpg.game.ui.ItemImageFactory;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ModuleFileLoader {

    private static ModuleFileLoader currentLoader;

    private GameJsonSerializer gameJsonSerializer;
    private ItemImageFactory itemImageFactory;

    public ModuleFileLoader(GameJsonSerializer gameJsonSerializer, ItemImageFactory itemImageFactory) {
        this.gameJsonSerializer = gameJsonSerializer;
        this.itemImageFactory = itemImageFactory;
        currentLoader = this;
        load();
    }

    private void load() {
        // remove old files
        for (FileHandle fileHandle : Gdx.files.local("temp\\images").list()) {
            fileHandle.delete();
        }

        // load files
        FileHandle[] moduleFiles = Gdx.files.local("modules").list(".module");
        for (FileHandle moduleFile : moduleFiles) {
            try (ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(moduleFile.file()))) {
                ZipEntry entry;
                while ((entry = zipInputStream.getNextEntry()) != null) {

                    // main module logic file
                    if (!entry.isDirectory() && entry.getName().equals("moduledata")) {
                        BufferedInputStream bufferedInputStream = new BufferedInputStream(zipInputStream);
                        String jsonString = new String(bufferedInputStream.readAllBytes());
                        Module module = gameJsonSerializer.deSerializeModule(jsonString);
                        // store entities
                        ItemData.storeItems(module.getItemData());
                        SkillData.storeSkills(module.getSkillData());
                        MonsterData.storeMonsters(module.getMonsterData());
                    }

                    // images
                    else if (!entry.isDirectory() && entry.getName().contains("images")) {
                        File tempFile = Gdx.files.local("temp\\images\\" + entry.getName().replaceAll("\\|/|(images)", "")).file();
                        tempFile.deleteOnExit();
                        Files.write(tempFile.toPath(), new BufferedInputStream(zipInputStream).readAllBytes());
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // store atlas regions
        FileHandle[] atlasTextFiles = Gdx.files.local("temp\\images").list(".txt");
        for (FileHandle atlasTextFile : atlasTextFiles) {
            TextureAtlas textureAtlas = new TextureAtlas(atlasTextFile);
            textureAtlas.getRegions().forEach(region -> itemImageFactory.addRegion(region));
        }
    }
}
