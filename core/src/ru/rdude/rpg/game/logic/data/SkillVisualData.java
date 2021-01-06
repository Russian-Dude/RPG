package ru.rdude.rpg.game.logic.data;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class SkillVisualData {

    enum AttackVisualType { THROW, ON_BEING, FULL_SCREEN }

    private AttackData attackData;
    private TextureRegion buffTexture;


    public class AttackData {

        private AttackVisualType attackVisualType;
        private TextureRegion textureRegion;

    }
}
