package ru.rdude.rpg.game.utils;

import java.util.Collection;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public interface Apply {

    static <T> T to(T t, Consumer<T> consumer) {
        consumer.accept(t);
        return t;
    }

    static <T, V> Map<T, V> fillMap(Map<T, V> map, Collection<T> keys, Supplier<V> value) {
        keys.forEach(t -> map.put(t, value.get()));
        return map;
    }

    static <T, V> Map<T, V> fillMap(Map<T, V> map, Collection<T> keys, Function<T, V> value) {
        keys.forEach(t -> map.put(t, value.apply(t)));
        return map;
    }

}
