package ru.rdude.rpg.game.visual;

import com.badlogic.gdx.math.Vector2;
import ru.rdude.rpg.game.ui.HpBar;
import ru.rdude.rpg.game.ui.StmBar;

public interface VisualTarget {

    DamageLabel getDamageLabel();
    Vector2 getCenter();
    HpBar getHpBar();
    StmBar getStmBar();
}
