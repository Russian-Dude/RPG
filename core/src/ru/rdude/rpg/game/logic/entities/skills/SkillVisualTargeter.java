package ru.rdude.rpg.game.logic.entities.skills;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import ru.rdude.rpg.game.logic.data.SkillData;
import ru.rdude.rpg.game.logic.entities.beings.Being;
import ru.rdude.rpg.game.logic.entities.beings.Monster;
import ru.rdude.rpg.game.logic.entities.beings.Player;
import ru.rdude.rpg.game.logic.enums.SkillEffect;
import ru.rdude.rpg.game.logic.enums.Target;
import ru.rdude.rpg.game.logic.game.Game;
import ru.rdude.rpg.game.logic.gameStates.Battle;
import ru.rdude.rpg.game.logic.gameStates.Camp;
import ru.rdude.rpg.game.logic.gameStates.GameStateBase;
import ru.rdude.rpg.game.logic.gameStates.Map;

import java.util.LinkedList;

public class SkillVisualTargeter {

    private boolean targeting = false;
    private boolean firstClick = false;
    private Being<?> currentCaster;
    private Target currentTarget;
    private SkillData skillData;

    public boolean isTargeting() {
        return targeting;
    }

    public void start(Being<?> caster, Target target, SkillData skillData) {
        if (caster instanceof Monster) {
            findTargetForMonster(caster, target, skillData);
        } else if (caster instanceof Player) {
            this.targeting = true;
            this.firstClick = true;
            this.currentCaster = caster;
            this.currentTarget = target;
            this.skillData = skillData;
        } else throw new IllegalArgumentException("Not implemented");
    }

    public void target(Being<?> target) {
        final boolean isAllies = isAllies(currentCaster, target);
        if (currentTarget == Target.ALLY && !isAllies
                || currentTarget == Target.ANY_OTHER && currentCaster.equals(target)
                || currentTarget == Target.ENEMY && isAllies
                || target.getEffect() == SkillEffect.EXILE) {
            clear();
        }
        else {
            currentCaster.setReady(false);
            Game.getCurrentGame().getSkillsSequencer().add(skillData, currentCaster, Game.getSkillTargeter().get(currentCaster, target, skillData.getTargets()), true);
            clear();
        }
    }

    public void act() {
        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
            if (firstClick) {
                firstClick = false;
            }
            else {
                clear();
            }
        }
    }

    public void clear() {
        this.targeting = false;
        this.currentCaster = null;
        this.currentTarget = null;
        this.skillData = null;
    }

    private boolean isAllies(Being<?> a, Being<?> b) {
        final GameStateBase gameState = Game.getCurrentGame().getCurrentGameState();
        if (gameState instanceof Camp || gameState instanceof Map) {
            final LinkedList<Being<?>> beings = Game.getCurrentGame().getCurrentPlayers().getBeings();
            return beings.contains(a) && beings.contains(b);
        } else if (gameState instanceof Battle) {
            return ((Battle) gameState).getAllySide(a).getBeings().contains(b);
        }
        return true;
    }

    private void findTargetForMonster(Being<?> caster, Target target, SkillData skillData) {
        if (target == Target.ANY) {
            Game.getSkillUser().use(skillData, caster, Target.RANDOM_ANY);
        } else if (target == Target.ANY_OTHER) {
            Game.getSkillUser().use(skillData, caster, Target.RANDOM_ANY_OTHER);
        } else if (target == Target.ALLY) {
            Game.getSkillUser().use(skillData, caster, Target.RANDOM_ALLY);
        } else if (target == Target.ENEMY) {
            Game.getSkillUser().use(skillData, caster, Target.RANDOM_ENEMY);
        }
    }
}
