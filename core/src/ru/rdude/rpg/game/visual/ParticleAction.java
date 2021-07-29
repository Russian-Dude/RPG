package ru.rdude.rpg.game.visual;

import com.badlogic.gdx.scenes.scene2d.actions.TemporalAction;

public class ParticleAction extends TemporalAction {

    private ParticleEffector particleEffector;
    private final ParticleEffectsPools.ParticleActionPool pool;

    public ParticleAction(float duration, ParticleEffector particleEffector, ParticleEffectsPools.ParticleActionPool pool) {
        super(duration);
        this.pool = pool;
        this.particleEffector = particleEffector;
    }

    public ParticleEffector getParticleEffector() {
        return particleEffector;
    }

    public void setParticleEffector(ParticleEffector particleEffector) {
        this.particleEffector = particleEffector;
    }

    @Override
    protected void begin() {
        reset();
        particleEffector.reset();
        super.begin();
        particleEffector.start();
    }

    @Override
    protected void end() {
        super.end();
        pool.free(this);
    }

    @Override
    protected void update(float percent) {
    }
}
