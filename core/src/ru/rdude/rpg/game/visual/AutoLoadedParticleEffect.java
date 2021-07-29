package ru.rdude.rpg.game.visual;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import ru.rdude.rpg.game.logic.game.Game;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.concurrent.atomic.AtomicReference;

public class AutoLoadedParticleEffect extends ParticleEffect {

    private final long guid;

    public AutoLoadedParticleEffect(long guid) {
        super();
        this.guid = guid;
        final FileHandle particleFile = Gdx.files.local("temp\\particles\\" + guid + ".p");
        this.load(particleFile, Game.getImageFactory().getAtlas(getImageGuid(particleFile.file())));
    }

    public long getGuid() {
        return guid;
    }

    private long getImageGuid(File particleFile) {
        try {
            AtomicReference<Long> result = new AtomicReference<>();
            Files.readAllLines(particleFile.toPath()).stream()
                    .dropWhile(line -> !line.contains("- Image Paths -"))
                    .skip(1)
                    .limit(1)
                    .findAny()
                    .ifPresent(line -> result.set(Long.parseLong(line)));
            return result.get();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        throw new IllegalStateException("Can not find image for particle effect");
    }
}
