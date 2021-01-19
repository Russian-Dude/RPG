package ru.rdude.rpg.game.utils.ui;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import ru.rdude.rpg.game.ui.UiData;
import ru.rdude.rpg.game.utils.Functions;

import java.util.*;
import java.util.function.BiConsumer;

public class SelectorWithArrows<T> extends Table {

    public enum ContentRepresentation { COUNT, TO_STRING, ACTOR }

    private ContentRepresentation contentRepresentation = ContentRepresentation.COUNT;

    private Set<BiConsumer<T, Integer>> listeners = new HashSet<>();
    private List<T> list;
    private T selected;
    private int selectedIndex = 0;
    private TextButton next = new TextButton(">", UiData.DEFAULT_SKIN, "square_mud");
    private TextButton previous = new TextButton("<", UiData.DEFAULT_SKIN, "square_mud");
    private Label label = new Label("1", UiData.DEFAULT_SKIN, "mud");
    private Actor content = label;

    public SelectorWithArrows(Collection<T> collection) {
        list = new ArrayList<>(collection);
        label.setAlignment(Align.center);
        if (collection.size() > 0) {
            selected = list.get(0);
        }

        // add children
        content.setWidth(next.getWidth() * 2);
        add(previous);
        add(content).width(next.getWidth() * 2);
        add(next);
        pack();

        // listeners
        next.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                selectedIndex = selectedIndex + 1 >= list.size() ? 0 : selectedIndex + 1;
                select(list.get(selectedIndex));
                updateRepresentation();
            }
        });
        previous.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                selectedIndex = selectedIndex - 1 < 0 ? list.size() - 1 : selectedIndex - 1;
                select(list.get(selectedIndex));
                updateRepresentation();
            }
        });
    }

    private void updateRepresentation() {
        switch (contentRepresentation) {
            case ACTOR:{
                content = (Actor) selected;
                break;
            }
            case COUNT:{
                if (content != label) {
                    content = label;
                }
                label.setText(list.indexOf(selected) + 1);
                break;
            }
            case TO_STRING:{
                if (content != label) {
                    content = label;
                }
                label.setText(selected.toString());
                break;
            }
        }
    }

    public void setContentRepresentation(ContentRepresentation contentRepresentation) {
        if (contentRepresentation == ContentRepresentation.ACTOR && !(list.get(0) instanceof Actor)) {
            throw new IllegalArgumentException("Content representation was set to ACTOR but real content is " + list.get(0).getClass());
        }
        this.contentRepresentation = contentRepresentation;
        updateRepresentation();
    }

    public void addListener(BiConsumer<T, Integer> listener) {
        listeners.add(listener);
    }

    public T getSelected() {
        return selected;
    }

    public int selectedIndex() {
        return selectedIndex;
    }

    public void select(T t) {
        if (!list.contains(t)) {
            return;
        }
        selected = t;
        selectedIndex = list.indexOf(t);
        listeners.forEach(l -> l.accept(selected, selectedIndex));
        updateRepresentation();
    }

    public void selectRandom() {
        select(Functions.random(list));
    }
}
