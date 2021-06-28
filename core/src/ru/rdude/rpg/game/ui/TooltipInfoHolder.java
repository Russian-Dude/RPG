package ru.rdude.rpg.game.ui;

import com.badlogic.gdx.scenes.scene2d.Actor;

import java.util.*;

public class TooltipInfoHolder<SUB extends Actor> {

    private SUB subscriber;
    private Set<Object> ignore;
    private Object mainObject;

    public TooltipInfoHolder(Object mainObject) {
        this.mainObject = mainObject;
        ignore = new HashSet<>();
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
}
