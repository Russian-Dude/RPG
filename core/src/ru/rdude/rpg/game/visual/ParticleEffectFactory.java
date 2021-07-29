package ru.rdude.rpg.game.visual;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import ru.rdude.rpg.game.logic.game.Game;

public class ParticleEffectFactory {

    public ParticleEffect get(long guid) {
        ParticleEffect particleEffect = new ParticleEffect();
        particleEffect.load(Gdx.files.local("temp\\particles\\" + guid + ".p"), Game.getImageFactory().getAtlas(guid));
        return particleEffect;
    }

}
