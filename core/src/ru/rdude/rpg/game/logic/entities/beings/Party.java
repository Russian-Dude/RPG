package ru.rdude.rpg.game.logic.entities.beings;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import ru.rdude.rpg.game.utils.SubscribersManager;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Stream;

@JsonIdentityInfo(generator = ObjectIdGenerators.UUIDGenerator.class)
public class Party {

    private SubscribersManager<PartyObserver> subscribers;
    private LinkedList<Being<?>> beings;
    private boolean iterating = false;
    private Queue<Runnable> addRemoveQueue = new LinkedList<>();

    public Party() {
        this(new LinkedList<>());
    }

    public Party(Collection<? extends Being<?>> collection) {
        beings = new LinkedList<>(collection);
        subscribers = new SubscribersManager<>();
    }

    public boolean add(Being<?> being) {
        if (iterating) {
            addRemoveQueue.add(() -> add(being));
            return false;
        }
        boolean result = beings.add(being);
        if (result) {
            subscribers.notifySubscribers(sub -> sub.partyUpdate(this, true, being, beings.size() - 1));
        }
        return result;
    }

    public void addToTheLeftFrom(Being<?> from, Being<?> being) {
        if (iterating) {
            addRemoveQueue.add(() -> addToTheLeftFrom(from, being));
            return;
        }
        int index = beings.indexOf(from);
        if (index == 0) {
            beings.addFirst(being);
        }
        else {
            beings.add(index - 1, being);
        }
        subscribers.notifySubscribers(sub -> sub.partyUpdate(this, true, being, index == 0 ? 0 : index - 1));
    }

    public void addToTheRightFrom(Being<?> from, Being<?> being) {
        if (iterating) {
            addRemoveQueue.add(() -> addToTheRightFrom(from, being));
            return;
        }
        int index = beings.indexOf(from);
        beings.add(index + 1, being);
        subscribers.notifySubscribers(sub -> sub.partyUpdate(this, true, being, index + 1));
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
        if (iterating) {
            addRemoveQueue.add(() -> remove(being));
            return;
        }
        int index = beings.indexOf(being);
        beings.remove(being);
        if (being instanceof Minion) {
            ((Minion) being).getMaster().removeMinion((Minion) being);
        }
        subscribers.notifySubscribers(sub -> sub.partyUpdate(this, false, being, index));
    }

    public void forEach(Consumer<? super Being<?>> action) {
        iterating = true;
        beings.forEach(action);
        iterating = false;
        Runnable r;
        while ((r = addRemoveQueue.poll()) != null) {
            r.run();
        }
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

    public void subscribe(PartyObserver subscriber) {
        subscribers.subscribe(subscriber);
    }

    public void unsubscribe(PartyObserver subscriber) {
        subscribers.unsubscribe(subscriber);
    }
}
