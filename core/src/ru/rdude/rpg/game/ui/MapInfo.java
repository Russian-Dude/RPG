package ru.rdude.rpg.game.ui;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

public class MapInfo {

    public final Image image;
    public final String text;
    public final long guid;
    public final FileHandle mapFile;

    public MapInfo(FileHandle mapFile, long guid, String name, Image image) {
        this.mapFile = mapFile;
        this.guid = guid;
        this.image = image;
        this.text = name;
    }

    @Override
    public String toString() {
        return text;
    }
}
