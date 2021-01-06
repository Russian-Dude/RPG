package ru.rdude.rpg.game.logic.data;

import ru.rdude.rpg.game.logic.coefficients.Coefficients;
import ru.rdude.rpg.game.logic.entities.beings.BeingAction;
import ru.rdude.rpg.game.logic.enums.*;
import ru.rdude.rpg.game.logic.gameStates.GameStateBase;
import ru.rdude.rpg.game.logic.stats.Stats;

import java.util.*;
import java.util.stream.Stream;

public class SkillData extends EntityData {

    private static Map<Long, SkillData> skills = new HashMap<>();

    private SkillVisualData visualData;

    private SkillType type;
    private Coefficients coefficients;
    private Coefficients buffCoefficients;
    private String damage;
    private Map<StatName, String> stats;
    private double timeChange;
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
    private Map<GameState, Boolean> usableInGameStates;
    private boolean canBeBlocked;
    private boolean canBeDodged;
    private boolean canBeResisted;
    private SkillEffect effect;
    private SkillOverlay overlay;
    private Target mainTarget;
    private List<Target> targets;
    private Map<Long, Float> skillsCouldCast;
    private Map<Long, Float> skillsMustCast;
    // Skills casted after specific being action:
    private Map<BeingAction.Action, Map<Long, Float>> skillsOnBeingAction;
    private boolean onBeingActionCastToEnemy; // if true skill will be casted to interactor else from initial buff caster to buff holder
    // buff type - using magic or physic resistance to resist:
    private BuffType buffType;

    SkillData() {
        super();
    }

    public SkillData(long guid) {
        super(guid);
        coefficients = new Coefficients();
        stats = new HashMap<>();
        transformation = new Transformation();
        summon = new ArrayList<>();
        receiveItems = new HashMap<>();
        requirements = new Requirements();
        elements = new HashSet<>();
        usableInGameStates = new HashMap<>();
        targets = new ArrayList<>();
        skillsCouldCast = new HashMap<>();
        skillsMustCast = new HashMap<>();
        skillsOnBeingAction = new HashMap<>();
    }


    public static SkillData getSkillByGuid(long guid) {
        return skills.get(guid);
    }


    public SkillVisualData getVisualData() {
        return visualData;
    }

    public void setVisualData(SkillVisualData visualData) {
        this.visualData = visualData;
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

    public Map<StatName, String> getStats() {
        return stats;
    }

    public void setStats(Map<StatName, String> stats) {
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

    public Map<BeingAction.Action, Map<Long, Float>> getSkillsOnBeingAction() {
        return skillsOnBeingAction;
    }

    public void setSkillsOnBeingAction(Map<BeingAction.Action, Map<Long, Float>> skillsOnBeingAction) {
        this.skillsOnBeingAction = skillsOnBeingAction;
    }

    public Coefficients getBuffCoefficients() {
        return buffCoefficients;
    }

    public void setBuffCoefficients(Coefficients buffCoefficients) {
        this.buffCoefficients = buffCoefficients;
    }

    public Map<GameState, Boolean> getUsableInGameStates() {
        return usableInGameStates;
    }

    public void setUsableInGameStates(Map<GameState, Boolean> usableInGameStates) {
        this.usableInGameStates = usableInGameStates;
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

    public void setUsableInGameStates(Class<? extends GameStateBase> gameState, boolean value) {
        this.usableInGameStates.put(GameState.get(gameState), value);
    }

    public void setUsableInGameStates(GameState gameState, boolean value) {
        this.usableInGameStates.put(gameState, value);
    }

    public SkillOverlay getOverlay() {
        return overlay;
    }

    public void setOverlay(SkillOverlay overlay) {
        this.overlay = overlay;
    }

    public Target getMainTarget() {
        return mainTarget;
    }

    public void setMainTarget(Target mainTarget) {
        this.mainTarget = mainTarget;
    }

    public List<Target> getTargets() {
        return targets;
    }

    public void setTargets(List<Target> targets) {
        this.targets = targets;
    }

    public static Map<Long, SkillData> getSkills() {
        return skills;
    }

    public static void setSkills(Map<Long, SkillData> skills) {
        SkillData.skills = skills;
    }

    public BuffType getBuffType() {
        return buffType;
    }

    public void setBuffType(BuffType buffType) {
        this.buffType = buffType;
    }

    public double getTimeChange() {
        return timeChange;
    }

    public void setTimeChange(double timeChange) {
        this.timeChange = timeChange;
    }

    @Override
    public boolean hasEntityDependency(long guid) {
        return
                summon.contains(guid)
                        || receiveItems.containsKey(guid)
                        || requirements.items.containsKey(guid)
                        || skillsCouldCast.containsKey(guid)
                        || skillsMustCast.containsKey(guid)
                        || skillsOnBeingAction.values().stream()
                        .flatMap(map -> map.keySet().stream())
                        .anyMatch(g -> g.equals(guid));
    }

    @Override
    public void replaceEntityDependency(long oldValue, long newValue) {
        summon.replaceAll(guid -> guid == oldValue ? newValue : oldValue);
        if (receiveItems.containsKey(oldValue)) {
            receiveItems.put(newValue, receiveItems.get(oldValue));
            receiveItems.remove(oldValue);
        }
        if (requirements.items.containsKey(oldValue)) {
            requirements.items.put(newValue, requirements.items.get(oldValue));
            requirements.items.remove(oldValue);
        }
        if (skillsCouldCast.containsKey(oldValue)) {
            skillsCouldCast.put(newValue, skillsCouldCast.get(oldValue));
            skillsCouldCast.remove(oldValue);
        }
        if (skillsMustCast.containsKey(oldValue)) {
            skillsMustCast.put(newValue, skillsMustCast.get(oldValue));
            skillsMustCast.remove(oldValue);
        }
        for (Map<Long, Float> map : skillsOnBeingAction.values()) {
            if (map.containsKey(oldValue)) {
                map.put(newValue, map.get(oldValue));
                map.remove(oldValue);
            }
        }
    }

    public class Requirements {
        private Stats stats;
        private Map<Long, Integer> items; // by guid
        private boolean takeItems;

        public Requirements() {
            stats = new Stats(false);
            items = new HashMap<>();
        }

        public Stats getStats() {
            return stats;
        }

        public void setStats(Stats stats) {
            this.stats = stats;
        }

        public Map<Long, Integer> getItems() {
            return items;
        }

        public void setItems(Map<Long, Integer> items) {
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

        public Transformation() {
            beingTypes = new HashSet<>();
            elements = new HashSet<>();
        }

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
