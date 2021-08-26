package ru.rdude.rpg.game.visual;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.*;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.Pools;
import ru.rdude.rpg.game.battleVisual.BattleVisual;
import ru.rdude.rpg.game.logic.entities.beings.Monster;
import ru.rdude.rpg.game.logic.entities.skills.SkillResult;
import ru.rdude.rpg.game.logic.game.Game;
import ru.rdude.rpg.game.logic.gameStates.Battle;
import ru.rdude.rpg.game.logic.gameStates.GameStateBase;
import ru.rdude.rpg.game.ui.EffectsStage;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class SkillAnimator {

    public SequenceAction action(List<SkillResult> skillResults) {
        if (skillResults == null || skillResults.isEmpty()) {
            return Actions.sequence();
        }

        final SequenceAction mainAction = Actions.sequence();
        final List<List<SkillResult>> skills = separateSkills(skillResults);
        skills.forEach(s -> mainAction.addAction(createActionForOneSkill(s)));
        return mainAction;
    }

    private List<List<SkillResult>> separateSkills(List<SkillResult> skillResults) {

        List<List<SkillResult>> res = new ArrayList<>();
        SkillResult currentSkill = skillResults.get(0);
        List<SkillResult> currentSkillResults = new ArrayList<>();

        for (SkillResult skillResult : skillResults) {
            if (skillResult.getCaster() != currentSkill.getCaster() || skillResult.getSkillData() != currentSkill.getSkillData()) {
                res.add(currentSkillResults);
                currentSkillResults = new ArrayList<>();
                currentSkill = skillResult;
            }
            currentSkillResults.add(skillResult);
        }
        res.add(currentSkillResults);

        return res;
    }

    private Action createActionForOneSkill(List<SkillResult> skillResults) {
        ParallelAction mainAction;
        ParallelAction subTargetsAction;
        final SkillAnimation skillAnimation = skillResults.get(0).getSkillData().getResources().getSkillAnimation();
        final SkillAnimation.SubTargetsOrder subTargetsOrder = skillAnimation.getSubTargetsOrder();

        if (subTargetsOrder == SkillAnimation.SubTargetsOrder.SIMULTANEOUSLY) {
            mainAction = Actions.parallel();
            subTargetsAction = mainAction;
        } else if (subTargetsOrder == SkillAnimation.SubTargetsOrder.SIMULTANEOUSLY_AFTER_MAIN) {
            mainAction = Actions.sequence();
            subTargetsAction = Actions.sequence();
        } else {
            mainAction = Actions.sequence();
            subTargetsAction = mainAction;
        }

        for (int i = 0; i < skillResults.size(); i++) {
            SkillResult skillResult = skillResults.get(i);
            if (i == 0) {
                mainAction.addAction(createAction(skillResult));
            } else {
                if (i == 1 && subTargetsOrder == SkillAnimation.SubTargetsOrder.SIMULTANEOUSLY_AFTER_MAIN) {
                    mainAction.addAction(subTargetsAction);
                }
                subTargetsAction.addAction(createAction(skillResult));
            }
        }

        return mainAction;
    }

    private Action createAction(SkillResult skillResult) {
        final SkillAnimation skillAnimation = skillResult.getSkillData().getResources().getSkillAnimation();
        final SkillAnimation.Entry root = skillAnimation.getRoot();
        SkillAnimation.Entry current = root;
        SequenceAction sequenceAction = Actions.sequence();
        ParallelAction parallelAction = Actions.parallel();
        while (current != null) {
            if (current == root || current.getEntryOrder() == SkillAnimation.EntryOrder.ORDERED) {
                if (!parallelAction.getActions().isEmpty()) {
                    sequenceAction.addAction(parallelAction);
                    parallelAction = Actions.parallel();
                }
                sequenceAction.addAction(createAction(current, skillResult));
            }
            else {
                parallelAction.addAction(createAction(current, skillResult));
            }
            current = current.getNext();
        }
        if (!parallelAction.getActions().isEmpty()) {
            sequenceAction.addAction(parallelAction);
        }
        // summon animation
        skillResult.getSummon().ifPresent(minion -> {
            final GameStateBase currentGameState = Game.getCurrentGame().getCurrentGameState();
            // if monster summoned to enemy side
            if (currentGameState instanceof Battle && ((Battle) currentGameState).getEnemySide().getBeings().contains(skillResult.getTarget())) {
                final int index = ((Battle) currentGameState).getEnemySide().getBeings().indexOf(skillResult.getTarget()) + 1;
                sequenceAction.addAction(((BattleVisual) Game.getGameVisual().getCurrentGameStateStage()).createAddEnemyAction(index, minion));
            }
            // if monster summoned to player side
            else if (Game.getCurrentGame().getCurrentPlayers().getBeings().contains(skillResult.getTarget())) {
                final int index = Game.getCurrentGame().getCurrentPlayers().getBeings().indexOf(skillResult.getTarget()) + 1;
                sequenceAction.addAction(Game.getGameVisual().getUi().getPlayersVisualBottom().createAddBeingAction(index, minion));
            }
        });
        sequenceAction.addAction(createDelayedBarsAction(skillResult));
        sequenceAction.addAction(createDamageLabelAction(skillResult));
        if (skillResult.getTarget() instanceof Monster && !skillResult.getTarget().isAlive() ) {
            VisualBeing.VISUAL_BEING_FINDER.find(skillResult.getTarget()).ifPresent(vis -> sequenceAction.addAction(createDisappearMonsterAction((MonsterVisual) vis)));
        }
        return sequenceAction;
    }

    private Action createAction(SkillAnimation.Entry entry, SkillResult skillResult) {
        // directed
        if (entry.getDirected() != null) {
            return getDirected(entry, skillResult);
        }
        else if (entry.getCasterBack() != null) {
            return getOnActor(entry.getCasterBack(), skillResult, true, false);
        }
        else if (entry.getTargetBack() != null) {
            return getOnActor(entry.getTargetBack(), skillResult, false, false);
        }
        else if (entry.getCasterFront() != null) {
            return getOnActor(entry.getCasterFront(), skillResult, true, true);
        }
        else if (entry.getTargetFront() != null) {
            return getOnActor(entry.getTargetFront(), skillResult, false, true);
        }
        else if (entry.getFullscreen() != null) {
            // TODO: 22.07.2021 fullscreen skill effect
            return null;
        }
        else {
            throw new IllegalStateException("Not implemented type of skill effect");
        }
    }

    private Action getOnActor(Long effect, SkillResult skillResult, boolean caster, boolean front) {
        final VisualBeing<?> visualBeing;
        final Optional<? extends VisualBeing<?>> optional;
        if (caster) {
            optional = VisualBeing.VISUAL_BEING_FINDER.find(skillResult.getCaster());
        }
        else {
            optional = VisualBeing.VISUAL_BEING_FINDER.find(skillResult.getTarget());
        }
        if (optional.isPresent()) {
            visualBeing = optional.get();
        }
        else {
            return Actions.sequence();
        }

        final EffectsStage effectsStage = front ? Game.getGameVisual().getEffectsStageFront() : Game.getGameVisual().getEffectsStageBack();

        ParticleAction particleAction = Game.getParticleEffectsPool().obtainParticleAction(0.5f, effect);
        RunnableAction positionAction = Actions.run(() -> {
            effectsStage.addActor(particleAction.getParticleEffector());
            particleAction.getParticleEffector().setPosition(visualBeing.getCenter().x, visualBeing.getCenter().y);
        });

        return Actions.sequence(positionAction, particleAction);
    }

    private Action getDirected(SkillAnimation.Entry entry, SkillResult skillResult) {
        final Optional<? extends VisualBeing<?>> casterVisual = VisualBeing.VISUAL_BEING_FINDER.find(skillResult.getCaster());
        final Optional<? extends VisualBeing<?>> targetVisual = VisualBeing.VISUAL_BEING_FINDER.find(skillResult.getTarget());

        // if cant throw return empty action
        if (casterVisual.isEmpty()
                || targetVisual.isEmpty()
                || Objects.equals(casterVisual.get(), targetVisual.get())
                || entry.getDirection() == SkillAnimation.Direction.NO) {
            return Actions.sequence();
        }

        final VisualBeing<?> from;
        final VisualBeing<?> to;
        if (entry.getDirection() == SkillAnimation.Direction.FORWARD) {
            from = casterVisual.get();
            to = targetVisual.get();
        }
        else {
            from = targetVisual.get();
            to = casterVisual.get();
        }

        ParticleAction particleAction = Game.getParticleEffectsPool().obtainParticleAction(0.5f, entry.getDirected());
        RunnableAction positionAction = Actions.run(() -> {
            Game.getGameVisual().getEffectsStageFront().addActor(particleAction.getParticleEffector());
            particleAction.getParticleEffector().setPosition(from.getCenter().x, from.getCenter().y);
            particleAction.getParticleEffector().addAction(Actions.moveTo(to.getCenter().x, to.getCenter().y, 0.5f));
        });

        return Actions.sequence(positionAction, particleAction);
    }

    private RunnableAction createDelayedBarsAction(SkillResult skillResult) {
        final Optional<? extends VisualBeing<?>> targetVisual = VisualBeing.VISUAL_BEING_FINDER.find(skillResult.getTarget());
        return Actions.run(() -> {
            skillResult.getBuff().ifPresent(buff -> targetVisual.ifPresent(vt -> vt.getHpBar().actDelayed(buff)));
            skillResult.getDamage().ifPresent(damage -> targetVisual.ifPresent(vt -> vt.getHpBar().actDelayed(damage)));
        });
    }

    private RunnableAction createDamageLabelAction(SkillResult skillResult) {
        return Actions.run(() -> {
            skillResult.getDamage().ifPresent(damage -> {
                final DamageLabel damageLabel = Pools.get(DamageLabel.class).obtain();
                damageLabel.setText("");
                final MoveByAction moveUp = Actions.moveBy(0, 120, 1f);
                final MoveByAction moveMoreUp = Actions.moveBy(0, 20, 0.2f);
                final AlphaAction fadeInAction = Actions.fadeIn(0.2f);
                final AlphaAction fadeOutAction = Actions.fadeOut(0.2f);
                final ScaleByAction scaleUpAction = Actions.scaleBy(2f, 2f, 1f);
                final ScaleByAction scaleDownAction = Actions.scaleBy(-2f, -2f, 0.2f);
                final ParallelAction showAction = Actions.parallel(moveUp, fadeInAction, scaleUpAction);
                final ParallelAction hideAction = Actions.parallel(fadeOutAction, moveMoreUp);
                final RunnableAction removeAction = Actions.run(() -> {
                    damageLabel.remove();
                    Pools.get(DamageLabel.class).free(damageLabel);
                });
                final SequenceAction resultAction = Actions.sequence(showAction, hideAction, scaleDownAction, removeAction);
                VisualBeing.VISUAL_BEING_FINDER.find(skillResult.getTarget()).ifPresent(b -> {
                    Game.getGameVisual().getEffectsStageFront().addActor(damageLabel);
                    final Vector2 position = b.getCenter();
                    damageLabel.setPosition(position.x, position.y);
                    final String damageText;
                    if (damage.isHit()) {
                        damageText = String.valueOf((int) damage.value());
                    }
                    else if (damage.isMiss()) {
                        damageText = "miss";
                    }
                    else if (damage.isDodge()) {
                        damageText = "dodge";
                    }
                    else if (damage.isParry()) {
                        damageText = "parry";
                    }
                    else if (damage.isBlock()) {
                        damageText = "block";
                    }
                    else {
                        damageText = "";
                    }
                    damageLabel.setText(damageText);
                });
                damageLabel.addAction(resultAction);
            });
        });
    }

    private Action createDisappearMonsterAction(MonsterVisual monsterVisual) {
        final AlphaAction alphaAction = Actions.fadeOut(0.5f);
        alphaAction.setTarget(monsterVisual);
        return Actions.sequence(alphaAction, Actions.run(monsterVisual::remove));
    }

}