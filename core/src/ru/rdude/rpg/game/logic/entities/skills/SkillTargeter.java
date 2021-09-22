package ru.rdude.rpg.game.logic.entities.skills;

import ru.rdude.rpg.game.logic.data.SkillData;
import ru.rdude.rpg.game.logic.entities.beings.Being;
import ru.rdude.rpg.game.logic.entities.beings.Party;
import ru.rdude.rpg.game.logic.entities.beings.Player;
import ru.rdude.rpg.game.logic.enums.SkillEffect;
import ru.rdude.rpg.game.logic.enums.Target;
import ru.rdude.rpg.game.logic.game.Game;
import ru.rdude.rpg.game.logic.gameStates.Battle;
import ru.rdude.rpg.game.logic.gameStates.Camp;
import ru.rdude.rpg.game.logic.gameStates.GameStateBase;
import ru.rdude.rpg.game.logic.gameStates.Map;
import ru.rdude.rpg.game.utils.Functions;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SkillTargeter {

    private final Party emptyParty = new Party();
    private GameStateBase gameState;
    private Party allies;
    private Party enemies;
    private List<Being<?>> allBeings;

    public SkillTargets get(Being<?> caster, Target mainTarget) {

        gameState = Game.getCurrentGame().getCurrentGameState();
        allies = emptyParty;
        enemies = emptyParty;

        if (gameState instanceof Battle) {
            allies = ((Battle) gameState).getAllySide(caster);
            enemies = ((Battle) gameState).getEnemySide(caster);
        } else if (gameState instanceof Camp || gameState instanceof Map) {
            allies = Game.getCurrentGame().getCurrentPlayers();
        }

        allBeings = Stream.concat(allies.stream(), enemies.stream()).collect(Collectors.toList());

        // ai points random target if it needs to
        if (!(caster instanceof Player)) {
            if (mainTarget == Target.ALLY) mainTarget = Target.RANDOM_ALLY;
            else if (mainTarget == Target.ENEMY) mainTarget = Target.RANDOM_ENEMY;
            else if (mainTarget == Target.ANY) mainTarget = Target.RANDOM_ANY;
            else if (mainTarget == Target.ANY_OTHER) mainTarget = Target.RANDOM_ANY_OTHER;
        }

        switch (mainTarget) {
            case ALLY:
            case ENEMY:
            case ANY:
            case ANY_OTHER:
                break;

            case NO:
                return null;

            case RANDOM_ANY:
                return new SkillTargets(Functions.random(allBeings), null);

            case RANDOM_ANY_OTHER:
                final Set<Being<?>> beings = allBeings.stream().filter(b -> b != caster).collect(Collectors.toSet());
                if (!beings.isEmpty()) {
                    return new SkillTargets(Functions.random(beings), null);
                }
                break;

            case RIGHT_ALLY:
                Being<?> rightAlly = allies.getBeingRightFrom(caster);
                if (rightAlly != null) {
                    return new SkillTargets(rightAlly, null);
                }
                break;

            case LEFT_ALLY:
                Being<?> leftAlly = allies.getBeingLeftFrom(caster);
                if (leftAlly != null) {
                    return new SkillTargets(leftAlly, null);
                }
                break;

            case SELF:
                return new SkillTargets(caster, null);

            case RANDOM_ALLY:
                return new SkillTargets(Functions.random(allies.getBeings()), null);

            case RANDOM_ENEMY:
                if (enemies != emptyParty) {
                    return new SkillTargets(Functions.random(enemies.getBeings()), null);
                }
                break;

            case ALL:
                return new SkillTargets(null, allBeings);

            case ALL_ALLIES:
                return new SkillTargets(null, allies.getBeings());

            case ALL_OTHER_ALLIES:
                final List<Being<?>> otherAllies = allies.stream()
                        .filter(being -> being != caster)
                        .collect(Collectors.toList());
                return new SkillTargets(null, otherAllies);

            case ALL_ENEMIES:
                if (enemies != null && enemies != emptyParty) {
                    return new SkillTargets(null, enemies.getBeings());
                }
                break;

            case ALL_OTHER:
                final List<Being<?>> allOther = allBeings.stream()
                        .filter(being -> !being.equals(caster))
                        .collect(Collectors.toList());
                return new SkillTargets(null, allOther);
        }
        return new SkillTargets(null, null);
    }

    public SkillTargets get(Being<?> caster, Being<?> mainTarget, List<Target> subTargets) {

        List<Being<?>> subList = new ArrayList<>();

        gameState = Game.getCurrentGame().getCurrentGameState();
        allies = emptyParty;
        enemies = emptyParty;

        if (gameState instanceof Battle) {
            allies = ((Battle) gameState).getAllySide(caster);
            enemies = ((Battle) gameState).getEnemySide(caster);
        } else if (gameState instanceof Camp || gameState instanceof Map) {
            allies = Game.getCurrentGame().getCurrentPlayers();
        }

        allBeings = Stream.concat(allies.stream(), enemies.stream()).collect(Collectors.toList());

        Party mainTargetParty = emptyParty;
        if (gameState instanceof Battle) {
            mainTargetParty = ((Battle) gameState).getAllySide(mainTarget);
        } else if (gameState instanceof Camp || gameState instanceof Map) {
            Party p = Game.getCurrentGame().getCurrentPlayers();
            if (p != null) mainTargetParty = p;
        }


        // other targets:
        for (Target target : subTargets) {
            switch (target) {
                case RANDOM_ENEMY:
                    if (enemies != null && enemies != emptyParty)
                        subList.add(Functions.random(enemies.getBeings()));
                    break;

                case RANDOM_ALLY:
                    subList.add(Functions.random(allies.getBeings()));
                    break;

                case SELF:
                    subList.add(caster);
                    break;

                case LEFT_ALLY:
                    Being<?> leftAlly = allies.getBeingLeftFrom(caster);
                    if (leftAlly != null) subList.add(leftAlly);
                    break;

                case RIGHT_ALLY:
                    Being<?> rightAlly = allies.getBeingRightFrom(caster);
                    if (rightAlly != null) subList.add(rightAlly);
                    break;

                case RANDOM_ANY:
                    subList.add(Functions.random(allBeings));
                    break;

                case RANDOM_ANY_OTHER:
                    subList.add(Functions.random(allBeings.stream().filter(being -> being != caster).collect(Collectors.toSet())));
                    break;

                case ENEMY:
                case ALLY:
                case ANY:
                case ANY_OTHER:
                    if (mainTarget != null) {
                        subList.add(mainTarget);
                    }
                    break;

                case LEFT_FROM_TARGET:
                    if (mainTargetParty != null && mainTargetParty != emptyParty) {
                        Being<?> leftFromTarget = mainTargetParty.getBeingLeftFrom(mainTarget);
                        if (leftFromTarget != null) {
                            subList.add(leftFromTarget);
                        }
                    }
                    break;

                case RIGHT_FROM_TARGET:
                    if (mainTargetParty != null && mainTargetParty != emptyParty) {
                        Being<?> rightFromTarget = mainTargetParty.getBeingRightFrom(mainTarget);
                        if (rightFromTarget != null) {
                            subList.add(rightFromTarget);
                        }
                    }
                    break;

                case ALL:
                    subList.addAll(allBeings);
                    break;

                case ALL_OTHER:
                    allBeings.stream()
                            .filter(being -> being != caster)
                            .forEach(subList::add);
                    break;

                case ALL_ALLIES:
                    subList.addAll(allies.getBeings());
                    break;

                case ALL_OTHER_ALLIES:
                    allies.stream().filter(being -> being != caster).forEach(subList::add);
                    break;

                case ALL_ENEMIES:
                    if (enemies != null && enemies != emptyParty)
                        subList.addAll(enemies.getBeings());
                    break;
            }
        }
        return new SkillTargets(mainTarget, subList);
    }

    public SkillTargets get(SkillData skillData, Being<?> caster, Target mainTarget) {

        gameState = Game.getCurrentGame().getCurrentGameState();
        allies = emptyParty;
        enemies = emptyParty;

        if (gameState instanceof Battle) {
            allies = ((Battle) gameState).getAllySide(caster);
            enemies = ((Battle) gameState).getEnemySide(caster);
        } else if (gameState instanceof Camp || gameState instanceof Map) {
            allies = Game.getCurrentGame().getCurrentPlayers();
        }

        allBeings = Stream.concat(allies.stream(), enemies.stream())
                .filter(being -> being.getEffect() != SkillEffect.EXILE)
                .collect(Collectors.toList());

        switch (mainTarget) {
            case ALLY:
            case ENEMY:
            case ANY:
            case ANY_OTHER:
                Game.getGameVisual().getSkillTargeter().start(caster, mainTarget, skillData);
                break;

            case NO:
                return get(caster, null, skillData.getTargets());

            case RANDOM_ANY:
                return get(caster, Functions.random(allBeings), skillData.getTargets());

            case RANDOM_ANY_OTHER:
                final Set<Being<?>> beings = allBeings.stream().filter(b -> b != caster).collect(Collectors.toSet());
                if (!beings.isEmpty()) {
                    return get(caster, Functions.random(beings), skillData.getTargets());
                }
                break;

            case RIGHT_ALLY:
                Being<?> rightAlly = allies.getBeingRightFrom(caster);
                if (rightAlly != null && rightAlly.getEffect() != SkillEffect.EXILE) {
                    return get(caster, rightAlly, skillData.getTargets());
                }
                break;

            case LEFT_ALLY:
                Being<?> leftAlly = allies.getBeingLeftFrom(caster);
                if (leftAlly != null && leftAlly.getEffect() != SkillEffect.EXILE) {
                    return get(caster, leftAlly, skillData.getTargets());
                }
                break;

            case SELF:
                return get(caster, caster, skillData.getTargets());

            case RANDOM_ALLY:
                Being<?> ally = Functions.random(allies.getBeings().stream()
                        .filter(being -> being.getEffect() != SkillEffect.EXILE)
                        .collect(Collectors.toSet()));
                return get(caster, ally, skillData.getTargets());

            case RANDOM_ENEMY:
                if (enemies != emptyParty) {
                    Being<?> enemy = Functions.random(enemies.getBeings().stream()
                            .filter(being -> being.getEffect() != SkillEffect.EXILE)
                            .collect(Collectors.toSet()));
                    return get(caster, enemy, skillData.getTargets());
                }
                break;

            case ALL:
                List<Target> allTargets = new ArrayList<>(skillData.getTargets());
                allTargets.add(0, Target.ALL);
                return get(caster, null, allTargets);

            case ALL_ALLIES:
                List<Target> allAllies = new ArrayList<>(skillData.getTargets());
                allAllies.add(0, Target.ALL_ALLIES);
                return get(caster, null, allAllies);

            case ALL_OTHER_ALLIES:
                List<Target> allOtherAllies = new ArrayList<>(skillData.getTargets());
                allOtherAllies.add(0, Target.ALL_OTHER_ALLIES);
                return get(caster, null, allOtherAllies);

            case ALL_ENEMIES:
                List<Target> allEnemies = new ArrayList<>(skillData.getTargets());
                allEnemies.add(0, Target.ALL_ENEMIES);
                return get(caster, null, allEnemies);

            case ALL_OTHER:
                List<Target> allOther = new ArrayList<>(skillData.getTargets());
                allOther.add(0, Target.ALL_OTHER);
                return get(caster, null, allOther);
        }
        return new SkillTargets(null, null);
    }

}
