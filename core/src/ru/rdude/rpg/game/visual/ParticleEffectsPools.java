package ru.rdude.rpg.game.visual;

import com.badlogic.gdx.utils.Pool;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

public class ParticleEffectsPools {

    private final ParticleEffectPool particleEffectPool = new ParticleEffectPool();
    private final Pool<ParticleEffector> particleEffectorPool = new ParticleEffectorPool();
    private final ParticleActionPool particleActionPool = new ParticleActionPool();

    public ParticleEffector obtainEffector() {
        return particleEffectorPool.obtain();
    }

    public AutoLoadedParticleEffect obtainParticleEffect(long guid) {
        return particleEffectPool.get(guid);
    }

    public ParticleAction obtainParticleAction(float duration, long guid) {
        return particleActionPool.get(duration, guid);
    }

    public ParticleAction obtainParticleAction(float duration, long guid, ParticleEffector effector) {
        return particleActionPool.get(duration, guid, effector);
    }

    private class ParticleEffectorPool extends Pool<ParticleEffector> {

        @Override
        protected ParticleEffector newObject() {
            return new ParticleEffector(this);
        }

        @Override
        public void free(ParticleEffector object) {
            super.free(object);
            object.remove();
            particleEffectPool.free(object.getLastEffect());
        }
    }


    private static class ParticleEffectPool {

        private final Map<Long, Queue<AutoLoadedParticleEffect>> effects = new HashMap<>();

        AutoLoadedParticleEffect get(long guid) {
            if (!effects.containsKey(guid)) {
                Queue<AutoLoadedParticleEffect> queue = new LinkedList<>();
                effects.put(guid, queue);
                return new AutoLoadedParticleEffect(guid);
            }
            else if (effects.get(guid).isEmpty()) {
                return new AutoLoadedParticleEffect(guid);
            }
            else {
                return effects.get(guid).poll();
            }
        }

        void free(AutoLoadedParticleEffect particleEffect) {
            particleEffect.reset();
            effects.get(particleEffect.getGuid()).add(particleEffect);
        }
    }

    class ParticleActionPool {

        private final Queue<ParticleAction> actions = new LinkedList<>();

        ParticleAction get(float duration, long guid) {
            if (actions.isEmpty()) {
                return createNew(duration, guid);
            }
            else {
                final ParticleAction action = actions.poll();
                action.setDuration(duration);
                final ParticleEffector effector = particleEffectorPool.obtain();
                final AutoLoadedParticleEffect effect = particleEffectPool.get(guid);
                effector.setParticleEffect(effect);
                action.setParticleEffector(effector);
                return action;
            }
        }

        ParticleAction get(float duration, long guid, ParticleEffector effector) {
            final ParticleAction action;
            effector.setParticleEffect(particleEffectPool.get(guid));
            if (actions.isEmpty()) {
                action = new ParticleAction(duration, effector, this);
            }
            else {
                action = actions.poll();
                action.setDuration(duration);
                action.setParticleEffector(effector);
            }
            effector.reset();
            return action;
        }

        private ParticleAction createNew(float duration, long guid) {
            ParticleEffector effector = particleEffectorPool.obtain();
            final AutoLoadedParticleEffect effect = particleEffectPool.get(guid);
            effector.setParticleEffect(effect);
            effector.reset();
            return new ParticleAction(duration, effector, this);
        }

        void free(ParticleAction action) {
            actions.add(action);
        }


    }

}
