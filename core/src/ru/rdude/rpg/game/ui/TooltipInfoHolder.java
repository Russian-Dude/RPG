package ru.rdude.rpg.game.ui;

import com.badlogic.gdx.scenes.scene2d.Actor;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class TooltipInfoHolder<SUB extends Actor> {

    private SUB subscriber;
    private Set<Object> ignore;
    private Object mainObject;
    private Set<Option> options;

    public TooltipInfoHolder(Object mainObject) {
        this.mainObject = mainObject;
        ignore = new HashSet<>();
        options = new HashSet<>();
    }

    public void ignore(Object object) {
        this.ignore.add(object);
    }

    public SUB getSubscriber() {
        return subscriber;
    }

    public void addSubscriber(Actor sub) {
        this.subscriber = (SUB) sub;
    }

    public boolean isIgnored(Object object) {
        return this.ignore.contains(object);
    }

    public boolean isMainObject(Object object) {
        return mainObject.equals(object);
    }

    public void addOption(Option option) {
        this.options.add(option);
    }

    public Set<Option> getOptions() {
        return options;
    }

    public <T extends Option> Set<T> getOptions(Class<T> withClass) {
        return options.stream()
                .filter(option -> option.getClass().equals(withClass))
                .map(option -> (T) option)
                .collect(Collectors.toSet());
    }

    public <T extends SingleOption> T getOption(Class<T> singleOptionClass) {
        T t = null;
        for (Option option : options) {
            if (option.getClass().equals(singleOptionClass)) {
                t = (T) option;
                break;
            }
        }
        if (t == null) {
            try {
                t = singleOptionClass.getConstructor().newInstance();
                options.add(t);
            } catch (InvocationTargetException | InstantiationException | IllegalAccessException e) {
                throw new IllegalArgumentException("Cannot create instance of " + singleOptionClass);
            } catch (NoSuchMethodException e) {
                throw new IllegalArgumentException("Cannot create instance of " + singleOptionClass + ". Default constructor needed ");
            }
        }
        return t;
    }

    public boolean hasOption(Class<? extends Option> optionClass) {
        return options.stream().anyMatch(option -> option.getClass().equals(optionClass));
    }

    public boolean hasOption(Option option) {
        return options.stream().anyMatch(o -> o.equals(option));
    }

    public <T extends SingleOption> boolean optionMeetsCondition(Class<T> optionClass, Predicate<T> condition) {
        return hasOption(optionClass) && condition.test(getOption(optionClass));
    }

    public interface Option { }

    public interface SingleOption extends Option { }
}
