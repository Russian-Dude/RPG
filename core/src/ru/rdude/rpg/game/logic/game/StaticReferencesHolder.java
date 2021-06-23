package ru.rdude.rpg.game.logic.game;

import ru.rdude.rpg.game.utils.jsonextension.JsonPolymorphicSubType;

import java.util.HashMap;
import java.util.Map;

@JsonPolymorphicSubType("staticReferencesHolder")
public class StaticReferencesHolder<K, V> implements CurrentGameObserver {

    private final Map<K, V> current = new HashMap<>();
    private final Map<K, V> old = new HashMap<>();

    public StaticReferencesHolder() {
        Game.subscribe(this);
    }

    public void put(K key, V value) {
        current.put(key, value);
    }

    public void remove(K key) {
        current.remove(key);
    }

    public V get(K key) {
        return current.get(key);
    }

    @Override
    public void update(Game game, Action action) {
        if (action == Action.CREATED) {
            old.clear();
            old.putAll(current);
        }
        else if (action == Action.BECOME_CURRENT) {
            old.keySet().forEach(current::remove);
        }
    }
}
