package ru.rdude.rpg.game.utils.ui;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SortableVerticalGroup<T extends Actor & Comparable<T>> extends VerticalGroup {

    private List<T> list = new ArrayList<>();

    public SortableVerticalGroup() {
        super();
    }

    public void addSortableActor(T actor) {
        if (list.contains(actor) || actor.getParent() == this) {
            return;
        }
        list.add(actor);
        Collections.sort(list);
        clear();
        list.forEach(this::addActor);
    }

    public void sort() {
        Collections.sort(list);
        clear();
        list.forEach(this::addActor);
    }
}
