package ru.rdude.rpg.game.visual;

import com.badlogic.gdx.math.Vector2;
import ru.rdude.rpg.game.ui.CastBar;
import ru.rdude.rpg.game.ui.HpBar;
import ru.rdude.rpg.game.ui.StmBar;

public interface VisualTarget {

    Vector2 getCenter();
    HpBar getHpBar();
    StmBar getStmBar();
    CastBar getCastBar();
}
