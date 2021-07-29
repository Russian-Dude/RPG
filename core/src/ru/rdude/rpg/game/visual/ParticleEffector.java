package ru.rdude.rpg.game.visual;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Pool;

public class ParticleEffector extends Actor implements Pool.Poolable {

    private AutoLoadedParticleEffect lastEffect;
    private AutoLoadedParticleEffect particleEffect;
    private float lastDelta = 0f;
    private Vector2 coordinates = new Vector2(0, 0);
    private Pool<ParticleEffector> inPool;

    public ParticleEffector(Pool<ParticleEffector> inPool) {
        this.inPool = inPool;
    }

    public ParticleEffector() {
    }


    public void setParticleEffect(AutoLoadedParticleEffect particleEffect) {
        this.particleEffect = particleEffect;
        this.lastEffect = particleEffect;
    }

    public void start() {
        if (particleEffect != null) {
            particleEffect.allowCompletion();
            particleEffect.reset(true);
            particleEffect.start();
        }
    }

    public void startParticleEffect(AutoLoadedParticleEffect particleEffect) {
        setParticleEffect(particleEffect);
        start();
    }

    public AutoLoadedParticleEffect getLastEffect() {
        return lastEffect;
    }

    public AutoLoadedParticleEffect getParticleEffect() {
        return particleEffect;
    }

    @Override
    public void setPosition(float x, float y) {
        super.setPosition(x, y);
        coordinates.x = x;
        coordinates.y = y;
    }

    @Override
    public void setPosition(float x, float y, int alignment) {
        super.setPosition(x, y, alignment);
        coordinates.x = x;
        coordinates.y = y;
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        if (particleEffect != null) {
            if (particleEffect.isComplete()) {
                particleEffect = null;
            }
            else {
                particleEffect.setPosition(coordinates.x, coordinates.y);
            }
        }
        lastDelta = delta;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        if (particleEffect != null) {
            particleEffect.draw(batch, lastDelta);
        }
        else if (inPool != null && lastEffect != null) {
            remove();
            inPool.free(this);
            lastEffect = null;
        }
    }

    @Override
    public void reset() {
        if (particleEffect != null) {
            particleEffect.allowCompletion();
            particleEffect.reset();
        }
    }
}
