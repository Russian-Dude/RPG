package ru.rdude.rpg.game.visual;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import ru.rdude.rpg.game.logic.entities.beings.Monster;
import ru.rdude.rpg.game.ui.UiData;

public class MonsterVisual extends VerticalGroup implements VisualBeing<Monster>, VisualTarget {

    private final Monster monster;
    private final Label damageLabel;

    public MonsterVisual(Monster monster) {
        super();
        this.monster = monster;
        damageLabel = new Label("", UiData.DEFAULT_SKIN, UiData.BIG_TEXT_STYLE);

        Group group = new Group();
        group.addActor(damageLabel);
    }

    @Override
    public Monster getBeing() {
        return monster;
    }

    @Override
    public Label getDamageLabel() {
        return damageLabel;
    }

    @Override
    public Vector2 getCenter() {
        return null;
    }
}
