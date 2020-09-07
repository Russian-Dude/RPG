package ru.rdude.rpg.game.logic.data.io;

import ru.rdude.rpg.game.logic.data.SkillData;
import ru.rdude.rpg.game.logic.entities.beings.BeingAction;
import ru.rdude.rpg.game.logic.enums.*;
import ru.rdude.rpg.game.logic.gameStates.GameState;
import ru.rdude.rpg.game.logic.stats.Stat;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SkillDataSerializableObject implements Externalizable {

    private SkillData skillData;

    public SkillDataSerializableObject(SkillData skillData) {
        this.skillData = skillData;
    }

    public SkillData getSkillData() {
        return skillData;
    }

    @Override
    public void writeExternal(ObjectOutput objectOutput) throws IOException {
        objectOutput.writeLong(skillData.getGuid());
        objectOutput.writeObject(skillData.getName());
        objectOutput.writeObject(skillData.getNameInEditor());
        objectOutput.writeObject(skillData.getDescription());

        objectOutput.writeObject(skillData.getType());
        objectOutput.writeObject(new CoefficientsSerializable(skillData.getCoefficients()));
        objectOutput.writeObject(new CoefficientsSerializable(skillData.getBuffCoefficients()));
        objectOutput.writeObject(skillData.getDamage());
        objectOutput.writeObject(skillData.getStats());
        objectOutput.writeDouble(skillData.getTimeChange());
        objectOutput.writeObject(skillData.getTransformation().getBeingTypes());
        objectOutput.writeObject(skillData.getTransformation().getElements());
        objectOutput.writeObject(skillData.getTransformation().getSize());
        objectOutput.writeBoolean(skillData.getTransformation().isOverride());
        objectOutput.writeObject(skillData.getSummon());
        objectOutput.writeObject(skillData.getReceiveItems());
        objectOutput.writeObject(new StatsSerializable(skillData.getRequirements().getStats()));
        objectOutput.writeObject(skillData.getRequirements().getItems());
        objectOutput.writeBoolean(skillData.getRequirements().isTakeItems());
        objectOutput.writeBoolean(skillData.isPermanent());
        objectOutput.writeObject(skillData.getAttackType());
        objectOutput.writeObject(skillData.getElements());
        objectOutput.writeInt(skillData.getStaminaReq());
        objectOutput.writeInt(skillData.getConcentrationReq());
        objectOutput.writeObject(skillData.getDurationInTurns());
        objectOutput.writeObject(skillData.getDurationInMinutes());
        objectOutput.writeDouble(skillData.getActsEveryMinute());
        objectOutput.writeDouble(skillData.getActsEveryTurn());
        objectOutput.writeBoolean(skillData.isRecalculateStatsEveryIteration());
        objectOutput.writeObject(skillData.getHitsReceived());
        objectOutput.writeObject(skillData.getHitsMade());
        objectOutput.writeObject(skillData.getDamageReceived());
        objectOutput.writeObject(skillData.getDamageMade());
        objectOutput.writeObject(skillData.getUsableInGameStates());
        objectOutput.writeBoolean(skillData.isCanBeBlocked());
        objectOutput.writeBoolean(skillData.isCanBeDodged());
        objectOutput.writeBoolean(skillData.isCanBeResisted());
        objectOutput.writeObject(skillData.getEffect());
        objectOutput.writeObject(skillData.getOverlay());
        objectOutput.writeObject(skillData.getMainTarget());
        objectOutput.writeObject(skillData.getTargets());
        objectOutput.writeObject(skillData.getSkillsCouldCast());
        objectOutput.writeObject(skillData.getSkillsMustCast());
        objectOutput.writeObject(skillData.getSkillsOnBeingAction());
        objectOutput.writeBoolean(skillData.isOnBeingActionCastToEnemy());
        objectOutput.writeObject(skillData.getBuffType());
    }

    @Override
    public void readExternal(ObjectInput objectInput) throws IOException, ClassNotFoundException {
        skillData = new SkillData(objectInput.readLong());
        skillData.setName((String) objectInput.readObject());
        skillData.setNameInEditor((String) objectInput.readObject());
        skillData.setDescription((String) objectInput.readObject());

        skillData.setType((SkillType) objectInput.readObject());
        skillData.setCoefficients(((CoefficientsSerializable) objectInput.readObject()).getCoefficients());
        skillData.setBuffCoefficients(((CoefficientsSerializable) objectInput.readObject()).getCoefficients());
        skillData.setDamage((String) objectInput.readObject());
        skillData.setStats((Map<Class<Stat>, String>) objectInput.readObject());
        skillData.setTimeChange(objectInput.readDouble());
        skillData.getTransformation().setBeingTypes((Set<BeingType>) objectInput.readObject());
        skillData.getTransformation().setElements((Set<Element>) objectInput.readObject());
        skillData.getTransformation().setSize((Size) objectInput.readObject());
        skillData.getTransformation().setOverride(objectInput.readBoolean());
        skillData.setSummon((List<Long>) objectInput.readObject());
        skillData.setReceiveItems((Map<Long, Integer>) objectInput.readObject());
        skillData.getRequirements().setStats(((StatsSerializable) objectInput.readObject()).getStats());
        skillData.getRequirements().setItems((List<Long>) objectInput.readObject());
        skillData.getRequirements().setTakeItems(objectInput.readBoolean());
        skillData.setPermanent(objectInput.readBoolean());
        skillData.setAttackType((AttackType) objectInput.readObject());
        skillData.setElements((Set<Element>) objectInput.readObject());
        skillData.setStaminaReq(objectInput.readInt());
        skillData.setConcentrationReq(objectInput.readInt());
        skillData.setDurationInTurns((String) objectInput.readObject());
        skillData.setDurationInMinutes((String) objectInput.readObject());
        skillData.setActsEveryMinute(objectInput.readDouble());
        skillData.setActsEveryTurn(objectInput.readDouble());
        skillData.setRecalculateStatsEveryIteration(objectInput.readBoolean());
        skillData.setHitsReceived((String) objectInput.readObject());
        skillData.setHitsMade((String) objectInput.readObject());
        skillData.setDamageReceived((String) objectInput.readObject());
        skillData.setDamageMade((String) objectInput.readObject());
        skillData.setUsableInGameStates((Map<Class<? extends GameState>, Boolean>) objectInput.readObject());
        skillData.setCanBeBlocked(objectInput.readBoolean());
        skillData.setCanBeDodged(objectInput.readBoolean());
        skillData.setCanBeResisted(objectInput.readBoolean());
        skillData.setEffect((SkillEffect) objectInput.readObject());
        skillData.setOverlay((SkillOverlay) objectInput.readObject());
        skillData.setMainTarget((Target) objectInput.readObject());
        skillData.setTargets((List<Target>) objectInput.readObject());
        skillData.setSkillsCouldCast((Map<Long, Float>) objectInput.readObject());
        skillData.setSkillsMustCast((Map<Long, Float>) objectInput.readObject());
        skillData.setSkillsOnBeingAction((Map<BeingAction.Action, Long>) objectInput.readObject());
        skillData.setOnBeingActionCastToEnemy(objectInput.readBoolean());
        skillData.setBuffType((BuffType) objectInput.readObject());
    }
}
