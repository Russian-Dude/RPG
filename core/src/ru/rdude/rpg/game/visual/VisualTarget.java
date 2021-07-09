package ru.rdude.rpg.game.visual;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Label;

public interface VisualTarget {

    Label getDamageLabel();
    Vector2 getCenter();


}
