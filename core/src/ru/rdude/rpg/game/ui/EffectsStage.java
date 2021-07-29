package ru.rdude.rpg.game.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.FitViewport;
import ru.rdude.rpg.game.logic.game.Game;
import ru.rdude.rpg.game.visual.ParticleEffector;

public class EffectsStage extends Stage {

    public EffectsStage() {
        super(new FitViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));
    }

    public ParticleEffector addEffect(long guid) {
        final ParticleEffector particleEffector = Game.getParticleEffectsPool().obtainEffector();
        particleEffector.setParticleEffect(Game.getParticleEffectsPool().obtainParticleEffect(guid));
        addActor(particleEffector);
        return particleEffector;
    }
}
