package ru.rdude.rpg.game.visual;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import ru.rdude.rpg.game.logic.entities.beings.Monster;
import ru.rdude.rpg.game.ui.HpBar;
import ru.rdude.rpg.game.ui.StmBar;
import ru.rdude.rpg.game.ui.UiData;

public class MonsterVisual extends VerticalGroup implements VisualBeing<Monster>, VisualTarget {

    private final Monster monster;
    private final DamageLabel damageLabel;

    public MonsterVisual(Monster monster) {
        super();
        this.monster = monster;
        damageLabel = new DamageLabel();

        Group group = new Group();
        group.addActor(damageLabel);
    }

    @Override
    public Monster getBeing() {
        return monster;
    }

    @Override
    public DamageLabel getDamageLabel() {
        return damageLabel;
    }

    @Override
    public Vector2 getCenter() {
        return null;
    }

    @Override
    public HpBar getHpBar() {
        // TODO: 02.08.2021 get monster hp bar
        return null;
    }

    @Override
    public StmBar getStmBar() {
        // TODO: 02.08.2021 get monster stm bar
        return null;
    }
}
