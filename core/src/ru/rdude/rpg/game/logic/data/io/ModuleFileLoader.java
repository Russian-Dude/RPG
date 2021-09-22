package ru.rdude.rpg.game.logic.data.io;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import ru.rdude.rpg.game.logic.data.Module;
import ru.rdude.rpg.game.logic.data.*;
import ru.rdude.rpg.game.ui.ImageFactory;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ModuleFileLoader {

    private GameJsonSerializer gameJsonSerializer;
    private ImageFactory imageFactory;

    public ModuleFileLoader(GameJsonSerializer gameJsonSerializer, ImageFactory imageFactory) {
        this.gameJsonSerializer = gameJsonSerializer;
        this.imageFactory = imageFactory;
    }

    public void load() {
        // remove old files
        for (FileHandle fileHandle : Gdx.files.local("temp\\images").list()) {
            fileHandle.delete();
        }

        // load files
        FileHandle[] moduleFiles = Gdx.files.local("modules").list(".module");
        for (FileHandle moduleFile : moduleFiles) {
            try (ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(moduleFile.file()));
                 BufferedInputStream bufferedInputStream = new BufferedInputStream(zipInputStream)) {
                ZipEntry entry;
                while ((entry = zipInputStream.getNextEntry()) != null) {

                    // main module logic file
                    if (!entry.isDirectory() && entry.getName().equals("module")) {
                        String jsonString = new String(bufferedInputStream.readAllBytes());
                        Module module = gameJsonSerializer.deSerializeModule(jsonString);
                        // store entities
                        ItemData.storeItems(module.getItemData());
                        SkillData.storeSkills(module.getSkillData());
                        MonsterData.storeMonsters(module.getMonsterData());
                        EventData.storeEvents(module.getEventData());
                        QuestData.storeQuests(module.getQuestData());
                        PlayerClassData.storeClasses(module.getPlayerClassData());
                        AbilityData.storeAbilities(module.getAbilityData());
                    }

                    // images
                    else if (!entry.isDirectory() && entry.getName().contains("images")) {
                        File tempFile = Gdx.files.local("temp\\images\\" + entry.getName().replaceAll("\\|/|(images)", "")).file();
                        tempFile.deleteOnExit();
                        Files.write(tempFile.toPath(), new BufferedInputStream(zipInputStream).readAllBytes());
                    }

                    // particle files
                    else if (!entry.isDirectory() && entry.getName().contains("particles")) {
                        File tempFile = Gdx.files.local("temp\\particles\\" + entry.getName().replaceAll("\\|/|(particles)", "")).file();
                        tempFile.deleteOnExit();
                        Files.write(tempFile.toPath(), new BufferedInputStream(zipInputStream).readAllBytes());
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // store atlas regions
        FileHandle[] atlasTextFiles = Gdx.files.local("temp\\images").list(".atlas");
        for (FileHandle atlasTextFile : atlasTextFiles) {
            TextureAtlas textureAtlas = new TextureAtlas(atlasTextFile);
            imageFactory.addAtlas(textureAtlas);
        }
    }
}
