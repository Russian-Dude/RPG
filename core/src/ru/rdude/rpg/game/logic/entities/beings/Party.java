package ru.rdude.rpg.game.logic.entities.beings;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import java.util.Collection;
import java.util.LinkedList;
import java.util.function.Consumer;
import java.util.stream.Stream;

@JsonIdentityInfo(generator = ObjectIdGenerators.UUIDGenerator.class)
public class Party {

    private LinkedList<Being<?>> beings;

    public Party() {
        this(new LinkedList<>());
    }

    public Party(Collection<? extends Being<?>> collection) {
        beings = new LinkedList<>(collection);
    }

    public boolean add(Being<?> being) {
        return beings.add(being);
    }

    public void addToTheLeftFrom(Being<?> being, Being<?> from) {

        int index = beings.indexOf(from);
        if (index == 0)
            beings.addFirst(being);
        else
            beings.add(index - 1, being);
    }

    public void addToTheRightFrom(Being<?> being, Being<?> from) {
        int index = beings.indexOf(from);
        beings.add(index + 1, being);
    }

    public Being<?> getBeingLeftFrom(Being<?> from) {
        int index = beings.indexOf(from);
        if (index == 0) return null;
        return beings.get(beings.indexOf(from) - 1);
    }

    public Being<?> getBeingRightFrom(Being<?> from) {
        int index = beings.indexOf(from);
        if (index == beings.size() - 1) return null;
        return beings.get(beings.indexOf(from) + 1);
    }

    public void remove(Being<?> being) {
        beings.remove(being);
    }

    public void forEach(Consumer<? super Being> action) {
        beings.forEach(action);
    }

    public Stream<Being<?>> stream() {
        return beings.stream();
    }

    public <T> Stream<T> streamOnly(Class<T> cl) {
        return beings.stream()
                .filter(being -> being != null && cl.isAssignableFrom(being.getClass()))
                .map(being -> (T) being);
    }

    public LinkedList<Being<?>> getBeings() {
        return beings;
    }
}
