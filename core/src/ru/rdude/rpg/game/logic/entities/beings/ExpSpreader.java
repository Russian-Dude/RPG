package ru.rdude.rpg.game.logic.entities.beings;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import ru.rdude.rpg.game.logic.coefficients.Coefficients;
import ru.rdude.rpg.game.logic.data.MonsterData;
import ru.rdude.rpg.game.logic.entities.skills.Buff;
import ru.rdude.rpg.game.logic.game.Game;
import ru.rdude.rpg.game.logic.gameStates.GameStateBase;
import ru.rdude.rpg.game.logic.gameStates.GameStateObserver;
import ru.rdude.rpg.game.logic.stats.Stat;
import ru.rdude.rpg.game.utils.jsonextension.JsonPolymorphicSubType;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Calculates how useful each player was in the battle
 */

@JsonIdentityInfo(generator = ObjectIdGenerators.UUIDGenerator.class)
@JsonPolymorphicSubType("expSpreader")
public class ExpSpreader implements BeingActionObserver, GameStateObserver, PartyObserver {

    private Party party;
    private List<Effort> effort;

    private ExpSpreader() {
    }

    public ExpSpreader(Party party) {
        this.party = party;
        this.effort = new ArrayList<>();
        party.forEach(being -> {
            being.subscribe(this);
            effort.add(new Effort(being, party));
        });
        //party.subscribe(this);
        Game.getCurrentGame().getGameStateHolder().subscribe(this);
    }

    public Map<Being<?>, Double> spreadExp(Party enemies) {
        final Double exp = enemies.stream()
                .filter(being -> being instanceof Monster)
                .map(being -> {
                    final double mainLvl = ((MonsterData) being.getEntityData()).getMainLvl();
                    final double coefficient = being.stats().lvlValue() / mainLvl;
                    return ((MonsterData) being.getEntityData()).getExpReward() * coefficient;
                })
                .reduce(Double::sum)
                .orElse(0d);
        return spreadExp(exp);
    }

    public Map<Being<?>, Double> spreadExp(double exp) {
        return party.stream()
                .filter(being -> being instanceof Player)
                .collect(Collectors.toMap(Function.identity(), being -> expFor(exp, being)));
    }

    private double effortFromMinions(Being<?> being) {
        if (being.minions == null) {
            return 0d;
        }
        double result = 0d;
        for (Minion minion : being.minions) {
            final Effort minionEffort = this.effort.stream()
                    .filter(ef -> ef.being == minion)
                    .findAny()
                    .orElse(null);
            if (minionEffort != null) {
                result += minionEffort.totalEffort();
                result += effortFromMinions(minion);
            }
        }
        return result * 0.5;
    }

    public double expFor(double allExp, Being<?> being) {
        if (!being.isAlive()) {
            return 0d;
        }
        double intBonus = being.stats.intelValue() * 0.01;
        double partySize = party.getBeings().stream()
                .filter(b -> b instanceof Player)
                .filter(Being::isAlive)
                .count();
        double totalEffort = effort.stream()
                .filter(ef -> !(ef.being instanceof Minion))
                .map(Effort::totalEffort)
                .reduce(Double::sum)
                .orElse(1d);
        totalEffort += effortFromMinions(being);
        double beingEffort = effort.stream()
                .filter(effort -> effort.being == being)
                .findAny()
                .map(Effort::totalEffort)
                .orElse(1d);
        return Math.round((1 + intBonus) * (allExp * 0.07 + allExp * (1 - partySize * 0.07) * (100 / (totalEffort / beingEffort))));
    }

    public void clear() {
        effort.forEach(Effort::clear);
        actualizeEffort();
    }

    private void actualizeEffort() {
        // add new
        party.getBeings().stream()
                .filter(being -> effort.stream().noneMatch(e -> e.being == being))
                .forEach(being -> {
                    being.subscribe(this);
                    effort.add(new Effort(being, party));
                });
        // remove dead minions or minions with dead master or beings that are not in a party anymore
        effort.stream()
                .filter(ef -> {
                    final Being<?> being = ef.being;
                    final boolean isMinion = being instanceof Minion;
                    final boolean masterOrMinionDead = isMinion && (!being.isAlive() || !((Minion) being).getMaster().isAlive());
                    final boolean notInParty = !party.getBeings().contains(being);
                    return masterOrMinionDead || notInParty;
                })
                .collect(Collectors.toList())
                .forEach(effort::remove);
    }

    @Override
    public void update(BeingAction action, Being<?> being) {
        effort.stream()
                .filter(e -> e.being == being)
                .findAny()
                .ifPresent(e -> e.madeAction(action));
    }

    @Override
    public void update(GameStateBase oldValue, GameStateBase newValue) {
        clear();
    }

    @Override
    public void partyUpdate(Party party, boolean added, Being<?> being, int position) {
        if (added && effort.stream().noneMatch(ef -> ef.being == being)) {
            effort.add(new Effort(being, party));
        }
    }

    private static class Effort {

        private final Party party;
        private final Being<?> being;
        private int hitsDeal = 0;
        private int hitsReceived = 0;
        private int buffsDeal = 0;

        private double value = 0d;

        @JsonCreator
        Effort(@JsonProperty("being") Being<?> being, @JsonProperty("party") Party party) {
            this.being = being;
            this.party = party;
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
            Being<?> beingInteractor = null;
            Buff buffInteractor = null;
            if (action.interactor() instanceof Being) {
                beingInteractor = (Being<?>) action.interactor();
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
            double statsChange = 0;
            if (buff.getStats().isPresent()) {
                statsChange = buff.getStats().get().stream()
                        .map(Stat::value)
                        .reduce(Double::sum).orElse(0d);
            }
            double atkCoefChange = 0d;
            double defCoefChange = 0d;
            // coefficients changes
            if (buff.getEntityData().getBuffCoefficients() != null) {
                Coefficients.CoefficientsContainer atkCoef = buff.getEntityData().getBuffCoefficients().atk();
                Coefficients.CoefficientsContainer defCoef = buff.getEntityData().getBuffCoefficients().def();
                // atk
                atkCoefChange = Stream.of(
                        atkCoef.element().getCoefficientsMap().values(),
                        atkCoef.attackType().getCoefficientsMap().values(),
                        atkCoef.beingType().getCoefficientsMap().values(),
                        atkCoef.size().getCoefficientsMap().values())
                        .flatMap(Collection::stream)
                        .map(coef -> (coef - 1) * 100d)
                        .reduce(Double::sum)
                        .orElse(0d);
                // def
                defCoefChange = Stream.of(
                        defCoef.element().getCoefficientsMap().values(),
                        defCoef.attackType().getCoefficientsMap().values(),
                        defCoef.beingType().getCoefficientsMap().values(),
                        defCoef.size().getCoefficientsMap().values())
                        .flatMap(Collection::stream)
                        .map(coef -> (coef - 1) * 100d)
                        .reduce(Double::sum)
                        .orElse(0d);
            }
            // change value
            return statsChange * 5 + atkCoefChange - defCoefChange;
        }
    }
}
