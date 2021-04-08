package ru.rdude.rpg.game.logic.entities.beings;

import ru.rdude.rpg.game.logic.coefficients.Coefficients;
import ru.rdude.rpg.game.logic.entities.skills.Buff;
import ru.rdude.rpg.game.logic.game.Game;
import ru.rdude.rpg.game.logic.gameStates.GameStateBase;
import ru.rdude.rpg.game.logic.gameStates.GameStateObserver;
import ru.rdude.rpg.game.logic.stats.Stat;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Calculates how useful each player was in the battle
 */

public class ExpSpreader implements BeingActionObserver, GameStateObserver {

    private Party party;
    private Map<Being, Effort> effort = new HashMap<>();

    public ExpSpreader(Party party) {
        this.party = party;
        party.forEach(being -> {
            being.subscribe(this);
            effort.put(being, new Effort(being));
        });
        Game.getCurrentGame().getGameStateHolder().subscribe(this);
    }

    public Map<Being, Double> spreadExp(double exp) {
        return party.stream()
                .collect(Collectors.toMap(Function.identity(), being -> expFor(exp, being)));
    }

    public double expFor(double allExp, Being being) {
        double intBonus = being.stats.intelValue() * 0.01;
        double partySize = party.getBeings().size();
        double totalEffort = effort.values().stream().map(Effort::totalEffort).reduce(Double::sum).orElse(1d);
        double beingEffort = effort.get(being).totalEffort();
        return (1 + intBonus) * (allExp * 0.07 + allExp * (1 - partySize * 0.07) * (100 / (totalEffort / beingEffort)));
    }

    public void clear() {
        // update effort map in case party was was changed
        effort.keySet().stream()
                .filter(being -> !party.getBeings().contains(being))
                .collect(Collectors.toList())
                .forEach(effort::remove);
        party.getBeings().stream()
                .filter(being -> !effort.containsKey(being))
                .collect(Collectors.toList())
                .forEach(being -> effort.put(being, new Effort(being)));
        effort.values().forEach(Effort::clear);
    }

    @Override
    public void update(BeingAction action, Being being) {
        effort.get(being).madeAction(action);
    }

    @Override
    public void update(GameStateBase oldValue, GameStateBase newValue) {
        clear();
    }

    private class Effort {

        private Being being;
        private int hitsDeal = 0;
        private int hitsReceived = 0;
        private int buffsDeal = 0;

        private double value = 0d;

        Effort(Being being) {
            this.being = being;
        }

        void clear() {
            hitsDeal = 0;
            hitsReceived = 0;
            buffsDeal = 0;
            value = 0;
        }

        double totalEffort() {
            return value + hitsDeal * 5 + hitsReceived * 3 + buffsDeal * 6;
        }

        void madeAction(BeingAction action) {
            Being beingInteractor = null;
            Buff buffInteractor = null;
            if (action.interactor() instanceof Being) {
                beingInteractor = (Being) action.interactor();
            } else if (action.interactor() instanceof Buff) {
                buffInteractor = (Buff) action.interactor();
            } else {
                return;
            }
            switch (action.action()) {

                case DAMAGE_DEAL:
                    if (!party.getBeings().contains(beingInteractor)) {
                        value += action.value() * 0.9;
                        hitsDeal += 1;
                    }
                    break;

                case DAMAGE_RECEIVE:
                case CRITICAL_RECEIVE:
                    if (!party.getBeings().contains(beingInteractor)) {
                        value += action.value() * 0.7;
                        hitsReceived += 1;
                    }
                    break;

                case BLOCK:
                case DODGE:
                case PARRY:
                    if (!party.getBeings().contains(beingInteractor)) {
                        value += action.value() * 0.5;
                    }
                    break;

                case KILL:
                    if (!party.getBeings().contains(beingInteractor)) {
                        value += action.value() * 0.3;
                    }
                    break;

                case HEAL_DEAL:
                    if (party.getBeings().contains(beingInteractor)) {
                        if (action.interactor().equals(being)) {
                            value += action.value() * 0.2;
                        } else {
                            value += action.value() * 0.7;
                        }
                        hitsDeal += 1;
                    }
                    break;

                case BUFF_DEAL:
                    double v = calculateBuffValue(buffInteractor);
                    if (v > 0 && party.getBeings().contains(buffInteractor.getCaster())) {
                        if (being.equals(buffInteractor.getCaster())) {
                            value += v * 0.5;
                        } else {
                            value += v * 0.8;
                        }
                        buffsDeal += 1;
                    } else if (v < 0) {
                        value += v * 0.7;
                        buffsDeal += 1;
                    }
                    break;

                case DIE:
                    value = 0d;
                    break;
            }
        }


        private double calculateBuffValue(Buff buff) {
            // stat changes
            double statsChange = buff.getStats().stream().map(Stat::value).reduce(Double::sum).orElse(0d);
            // coefficients changes
            Coefficients.CoefficientsContainer atkCoef = buff.getSkillData().getBuffCoefficients().atk();
            Coefficients.CoefficientsContainer defCoef = buff.getSkillData().getBuffCoefficients().def();
            // atk
            double atkCoefChange = Stream.of(
                    atkCoef.element().getCoefficientsMap().values(),
                    atkCoef.attackType().getCoefficientsMap().values(),
                    atkCoef.beingType().getCoefficientsMap().values(),
                    atkCoef.size().getCoefficientsMap().values())
                    .flatMap(Collection::stream)
                    .map(coef -> (coef - 1) * 100d)
                    .reduce(Double::sum)
                    .orElse(0d);
            // def
            double defCoefChange = Stream.of(
                    defCoef.element().getCoefficientsMap().values(),
                    defCoef.attackType().getCoefficientsMap().values(),
                    defCoef.beingType().getCoefficientsMap().values(),
                    defCoef.size().getCoefficientsMap().values())
                    .flatMap(Collection::stream)
                    .map(coef -> (coef - 1) * 100d)
                    .reduce(Double::sum)
                    .orElse(0d);
            // change value
            return statsChange * 5 + atkCoefChange - defCoefChange;
        }
    }
}
