package ru.rdude.rpg.game.logic.entities.skills;

import ru.rdude.rpg.game.logic.data.SkillData;
import ru.rdude.rpg.game.logic.entities.beings.Being;
import ru.rdude.rpg.game.logic.entities.beings.Party;
import ru.rdude.rpg.game.logic.enums.Target;
import ru.rdude.rpg.game.logic.game.Game;
import ru.rdude.rpg.game.logic.gameStates.Battle;
import ru.rdude.rpg.game.logic.gameStates.Camp;
import ru.rdude.rpg.game.logic.gameStates.GameStateBase;
import ru.rdude.rpg.game.logic.gameStates.Map;
import ru.rdude.rpg.game.utils.Functions;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SkillUser {

    private GameStateBase gameState;
    private Party emptyParty;
    private Party allies;
    private Party enemies;
    private List<Being> allBeings;


    public void use(SkillData skillData, Being caster) {

        emptyParty = new Party();

        gameState = Game.getCurrentGame().getCurrentGameState();
        allies = emptyParty;
        enemies = emptyParty;

        if (gameState instanceof Battle) {
            allies = ((Battle) gameState).getAllySide(caster);
            enemies = ((Battle) gameState).getEnemySide(caster);
        } else if (gameState instanceof Camp || gameState instanceof Map) {
            allies = gameState.getPlayerSide();
        }

        allBeings = Stream.concat(allies.stream(), enemies.stream()).collect(Collectors.toList());

        switch (skillData.getMainTarget()) {
            case NO:
                Game.getCurrentGame().getGameLogger().log(caster, skillData);
                use(skillData, caster, null);
                break;

            case RANDOM_ANY:
                use(skillData, caster, Functions.random(allBeings));
                break;

            case RIGHT_ALLY:
                Being rightAlly = allies.getBeingRightFrom(caster);
                if (rightAlly != null) {
                    use(skillData, caster, rightAlly);
                }
                break;

            case LEFT_ALLY:
                Being leftAlly = allies.getBeingLeftFrom(caster);
                if (leftAlly != null) {
                    use(skillData, caster, leftAlly);
                }
                break;

            case SELF:
                use(skillData, caster, caster);
                break;

            case RANDOM_ALLY:
                Being ally = Functions.random(allies.getBeings());
                use(skillData, caster, ally);
                break;

            case RANDOM_ENEMY:
                if (enemies != emptyParty) {
                    Being enemy = Functions.random(enemies.getBeings());
                    use(skillData, caster, enemy);
                }
                break;
        }
    }

    public void use(SkillData skillData, Being caster, Being mainTarget) {
        if (mainTarget != null) {
            Game.getCurrentGame().getGameLogger().log(caster, mainTarget, skillData);
        }
        // if there is main target and main target does not receive hit - return
        if (mainTarget != null && !SkillApplier.apply(skillData, caster, mainTarget))
            return;

        Party mainTargetParty = emptyParty;
        if (gameState instanceof Battle) {
            mainTargetParty = ((Battle) gameState).getAllySide(mainTarget);
        } else if (gameState instanceof Camp || gameState instanceof Map) {
            Party p = gameState.getPlayerSide();
            if (p != null) mainTargetParty = p;
        }


        // other targets:
        for (Target target : skillData.getTargets()) {
            switch (target) {
                case RANDOM_ENEMY:
                    if (enemies != null && enemies != emptyParty)
                        SkillApplier.apply(skillData, caster, Functions.random(enemies.getBeings()));
                    break;

                case RANDOM_ALLY:
                    SkillApplier.apply(skillData, caster, Functions.random(allies.getBeings()));
                    break;

                case SELF:
                    SkillApplier.apply(skillData, caster, caster);
                    break;

                case LEFT_ALLY:
                    Being leftAlly = allies.getBeingLeftFrom(caster);
                    if (leftAlly != null) SkillApplier.apply(skillData, caster, leftAlly);
                    break;

                case RIGHT_ALLY:
                    Being rightAlly = allies.getBeingRightFrom(caster);
                    if (rightAlly != null) SkillApplier.apply(skillData, caster, rightAlly);
                    break;

                case RANDOM_ANY:
                    SkillApplier.apply(skillData, caster, Functions.random(allBeings));
                    break;

                case ENEMY:
                case ALLY:
                case ANY:
                    SkillApplier.apply(skillData, caster, mainTarget);
                    break;

                case LEFT_FROM_TARGET:
                    if (mainTargetParty != null && mainTargetParty != emptyParty) {
                        Being leftFromTarget = mainTargetParty.getBeingLeftFrom(mainTarget);
                        if (leftFromTarget != null) {
                            SkillApplier.apply(skillData, caster, leftFromTarget);
                        }
                    }
                    break;

                case RIGHT_FROM_TARGET:
                    if (mainTargetParty != null && mainTargetParty != emptyParty) {
                        Being rightFromTarget = mainTargetParty.getBeingRightFrom(mainTarget);
                        if (rightFromTarget != null) {
                            SkillApplier.apply(skillData, caster, rightFromTarget);
                        }
                    }
                    break;

                case ALL:
                    allBeings.forEach(being -> SkillApplier.apply(skillData, caster, being));
                    break;

                case ALL_ALLIES:
                    allies.forEach(being -> SkillApplier.apply(skillData, caster, being));
                    break;

                case ALL_ENEMIES:
                    if (enemies != null && enemies != emptyParty)
                        enemies.forEach(being -> SkillApplier.apply(skillData, caster, being));
                    break;
            }
        }
    }

    public boolean isNeedToPointTarget(SkillData skillData) {
        switch (skillData.getMainTarget()) {
            case ANY:
            case ALLY:
            case ENEMY:
                return true;
            default:
                return false;
        }
    }

}
