package ru.rdude.rpg.game.logic.entities.states;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import ru.rdude.rpg.game.utils.jsonextension.JsonPolymorphicSubType;

import java.util.*;

public class StateHolder<T> {

    private static final StateChanger defaultStateChanger = new DefaultStateChanger();

    private List<StateEntry<T>> cache;

    public StateHolder() {
        this(new HashSet<>());
    }

    public StateHolder(T... defaultValues) {
        this(new HashSet<>(Arrays.asList(defaultValues)));
    }

    public StateHolder(Set<T> defaultStateSet) {
        if (defaultStateSet == null)
            throw new IllegalArgumentException();
        cache = new ArrayList<>();
        cache.add(new StateEntry<>(defaultStateChanger, defaultStateSet));
    }

    public Set<T> getDefault() {
        return cache.get(0).value;
    }

    public Set<T> getCurrent() {
        return cache.get(cache.size() - 1).value;
    }

    private StateEntry<T> containsStateChanger(StateChanger stateChanger) {
        for (StateEntry<T> stateEntry : cache) {
            if (stateEntry.key.equals(stateChanger))
                return stateEntry;
        }
        return null;
    }

    public void setDefault(Set<T> set) {
        cache.get(0).setValue(set);
    }

    public void add(StateChanger stateChanger, Set<T> state) {
        if (stateChanger == null || state == null)
            throw new IllegalArgumentException();

        StateEntry<T> entry;
        if ((entry = containsStateChanger(stateChanger)) != null) {
            cache.remove(entry);
        }

        if (stateChanger.isStateOverlay()) {
            cache.add(new StateEntry<>(stateChanger, state));
            return;
        }

        Set<T> sum = new HashSet<>(getCurrent());
        sum.addAll(state);
        cache.add(new StateEntry<>(stateChanger, sum));
    }

    public void add(StateChanger stateChanger, T... t) {
        add(stateChanger, new HashSet<>(Arrays.asList(t)));
    }

    public void remove(StateChanger stateChanger) {
        StateEntry<T> entry = containsStateChanger(stateChanger);
        if (entry != null)
            cache.remove(entry);
    }

    public int size() {
        return cache.size();
    }

    public void clear() {
        StateEntry<T> defEntry = cache.get(0);
        List<StateEntry<T>> newCache = new ArrayList<>();
        newCache.add(defEntry);
        cache = newCache;
    }

    public StateHolder<T> copy() {
        StateHolder<T> copy = new StateHolder<>();
        List<StateEntry<T>> cacheCopy = new ArrayList<>();
        cache.forEach(stateEntry -> cacheCopy.add(stateEntry.copy()));
        copy.cache = cacheCopy;
        return copy;
    }





    static class StateEntry<T> {
        StateChanger key;
        Set<T> value;

        @JsonCreator
        public StateEntry(@JsonProperty("key") StateChanger key, @JsonProperty("value") Set<T> value) {
            this.key = key;
            this.value = value;
        }

        public StateChanger getKey() {
            return key;
        }

        public void setKey(StateChanger key) {
            this.key = key;
        }

        public Set<T> getValue() {
            return value;
        }

        public void setValue(Set<T> value) {
            this.value = value;
        }

        public StateEntry<T> copy() {
            return new StateEntry<T>(key, new HashSet<>(value));
        }
    }

    // need to create class instead of using lambda for correct save to json
    @JsonPolymorphicSubType("defaultStateChanger")
    static class DefaultStateChanger implements StateChanger {

        @Override
        public boolean isStateOverlay() {
            return false;
        }
    }
}
