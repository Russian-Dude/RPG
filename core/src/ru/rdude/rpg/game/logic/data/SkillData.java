package ru.rdude.rpg.game.logic.data;

import ru.rdude.rpg.game.logic.coefficients.Coefficients;
import ru.rdude.rpg.game.logic.entities.beings.BeingAction;
import ru.rdude.rpg.game.logic.enums.*;
import ru.rdude.rpg.game.logic.gameStates.GameState;
import ru.rdude.rpg.game.logic.stats.Stat;
import ru.rdude.rpg.game.logic.stats.Stats;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SkillData extends EntityData {

    private static Map<Long, SkillData> skills = new HashMap<>();

    private SkillType type;
    private Coefficients coefficients;
    private Coefficients buffCoefficients;
    private String damage;
    private Map<Class<Stat>, String> stats;
    private Transformation transformation;
    private List<Long> summon; // by guid
    private Map<Long, Integer> receiveItems; // by guid
    private Requirements requirements;
    private boolean permanent;
    private AttackType attackType;
    private Set<Element> elements;
    private int staminaReq;
    private int concentrationReq;
    private String durationInTurns;
    private String durationInMinutes;
    private double actsEveryMinute;
    private double actsEveryTurn;
    private boolean recalculateStatsEveryIteration;
    // Ends after:
    private String hitsReceived;
    private String hitsMade;
    private String damageReceived;
    private String damageMade;
    private Map<Class<? extends GameState>, Boolean> usableInGameStates;
    private boolean canBeBlocked;
    private boolean canBeDodged;
    private boolean canBeResisted;
    private SkillEffect effect;
    //private SkillOverlay overlay;
    //private Target[] targets;
    private Map<Long, Float> skillsCouldCast;
    private Map<Long, Float> skillsMustCast;

    public SkillData(long guid) {
        super(guid);
    }
    // Skills casted after specific being action:
    private Map<BeingAction.Action, Long> skillsOnBeingAction;
    private boolean onBeingActionCastToEnemy; // if true skill will be casted to interactor else from initial buff caster to buff holder


    public static SkillData getSkillByGuid(long guid) {
        return skills.get(guid);
    }


    public SkillEffect getEffect() {
        return effect;
    }

    public void setEffect(SkillEffect effect) {
        this.effect = effect;
    }

    public SkillType getType() {
        return type;
    }

    public void setType(SkillType type) {
        this.type = type;
    }

    public Coefficients getCoefficients() {
        return coefficients;
    }

    public void setCoefficients(Coefficients coefficients) {
        this.coefficients = coefficients;
    }

    public String getDamage() {
        return damage;
    }

    public void setDamage(String damage) {
        this.damage = damage;
    }

    public Map<Class<Stat>, String> getStats() {
        return stats;
    }

    public void setStats(Map<Class<Stat>, String> stats) {
        this.stats = stats;
    }

    public Transformation getTransformation() {
        return transformation;
    }

    public void setTransformation(Transformation transformation) {
        this.transformation = transformation;
    }

    public List<Long> getSummon() {
        return summon;
    }

    public void setSummon(List<Long> summon) {
        this.summon = summon;
    }

    public Map<Long, Integer> getReceiveItems() {
        return receiveItems;
    }

    public void setReceiveItems(Map<Long, Integer> receiveItems) {
        this.receiveItems = receiveItems;
    }

    public Requirements getRequirements() {
        return requirements;
    }

    public void setRequirements(Requirements requirements) {
        this.requirements = requirements;
    }

    public boolean isPermanent() {
        return permanent;
    }

    public void setPermanent(boolean permanent) {
        this.permanent = permanent;
    }

    public AttackType getAttackType() {
        return attackType;
    }

    public void setAttackType(AttackType attackType) {
        this.attackType = attackType;
    }

    public Set<Element> getElements() {
        return elements;
    }

    public void setElements(Set<Element> elements) {
        this.elements = elements;
    }

    public int getStaminaReq() {
        return staminaReq;
    }

    public void setStaminaReq(int staminaReq) {
        this.staminaReq = staminaReq;
    }

    public int getConcentrationReq() {
        return concentrationReq;
    }

    public void setConcentrationReq(int concentrationReq) {
        this.concentrationReq = concentrationReq;
    }

    public String getDurationInTurns() {
        return durationInTurns;
    }

    public void setDurationInTurns(String durationInTurns) {
        this.durationInTurns = durationInTurns;
    }

    public String getDurationInMinutes() {
        return durationInMinutes;
    }

    public void setDurationInMinutes(String durationInMinutes) {
        this.durationInMinutes = durationInMinutes;
    }

    public String getHitsReceived() {
        return hitsReceived;
    }

    public void setHitsReceived(String hitsReceived) {
        this.hitsReceived = hitsReceived;
    }

    public String getHitsMade() {
        return hitsMade;
    }

    public void setHitsMade(String hitsMade) {
        this.hitsMade = hitsMade;
    }

    public String getDamageReceived() {
        return damageReceived;
    }

    public void setDamageReceived(String damageReceived) {
        this.damageReceived = damageReceived;
    }

    public String getDamageMade() {
        return damageMade;
    }

    public void setDamageMade(String damageMade) {
        this.damageMade = damageMade;
    }

    public Map<Class<? extends GameState>, Boolean> getUsableInGameStates() {
        return usableInGameStates;
    }

    public boolean isCanBeBlocked() {
        return canBeBlocked;
    }

    public void setCanBeBlocked(boolean canBeBlocked) {
        this.canBeBlocked = canBeBlocked;
    }

    public boolean isCanBeDodged() {
        return canBeDodged;
    }

    public void setCanBeDodged(boolean canBeDodged) {
        this.canBeDodged = canBeDodged;
    }

    public boolean isCanBeResisted() {
        return canBeResisted;
    }

    public void setCanBeResisted(boolean canBeResisted) {
        this.canBeResisted = canBeResisted;
    }

    public Map<Long, Float> getSkillsCouldCast() {
        return skillsCouldCast;
    }

    public void setSkillsCouldCast(Map<Long, Float> skillsCouldCast) {
        this.skillsCouldCast = skillsCouldCast;
    }

    public Map<Long, Float> getSkillsMustCast() {
        return skillsMustCast;
    }

    public void setSkillsMustCast(Map<Long, Float> skillsMustCast) {
        this.skillsMustCast = skillsMustCast;
    }

    public double getActsEveryMinute() {
        return actsEveryMinute;
    }

    public void setActsEveryMinute(double actsEveryMinute) {
        this.actsEveryMinute = actsEveryMinute;
    }

    public double getActsEveryTurn() {
        return actsEveryTurn;
    }

    public void setActsEveryTurn(double actsEveryTurn) {
        this.actsEveryTurn = actsEveryTurn;
    }

    public Map<BeingAction.Action, Long> getSkillsOnBeingAction() {
        return skillsOnBeingAction;
    }

    public Coefficients getBuffCoefficients() {
        return buffCoefficients;
    }

    public void setBuffCoefficients(Coefficients buffCoefficients) {
        this.buffCoefficients = buffCoefficients;
    }

    public void setSkillsOnBeingAction(Map<BeingAction.Action, Long> skillsOnBeingAction) {
        this.skillsOnBeingAction = skillsOnBeingAction;
    }

    public boolean isRecalculateStatsEveryIteration() {
        return recalculateStatsEveryIteration;
    }

    public void setRecalculateStatsEveryIteration(boolean recalculateStatsEveryIteration) {
        this.recalculateStatsEveryIteration = recalculateStatsEveryIteration;
    }

    public boolean isOnBeingActionCastToEnemy() {
        return onBeingActionCastToEnemy;
    }

    public void setOnBeingActionCastToEnemy(boolean onBeingActionCastToEnemy) {
        this.onBeingActionCastToEnemy = onBeingActionCastToEnemy;
    }

    public void setUsableInGameStates(Class<? extends GameState> gameState, boolean value) {
        this.usableInGameStates.put(gameState, value);
    }

    public class Requirements {
        private Stats stats;
        private List<Long> items; // by guid
        private boolean takeItems;

        public Stats getStats() {
            return stats;
        }

        public void setStats(Stats stats) {
            this.stats = stats;
        }

        public List<Long> getItems() {
            return items;
        }

        public void setItems(List<Long> items) {
            this.items = items;
        }

        public boolean isTakeItems() {
            return takeItems;
        }

        public void setTakeItems(boolean takeItems) {
            this.takeItems = takeItems;
        }
    }

    public class Transformation {
        private Set<BeingType> beingTypes;
        private Size size;
        private Set<Element> elements;
        private boolean override;

        public Set<BeingType> getBeingTypes() {
            return beingTypes;
        }

        public void setBeingTypes(Set<BeingType> beingTypes) {
            this.beingTypes = beingTypes;
        }

        public Size getSize() {
            return size;
        }

        public void setSize(Size size) {
            this.size = size;
        }

        public Set<Element> getElements() {
            return elements;
        }

        public void setElements(Set<Element> elements) {
            this.elements = elements;
        }

        public boolean isOverride() {
            return override;
        }

        public void setOverride(boolean override) {
            this.override = override;
        }
    }

}
