package ru.rdude.rpg.game.logic.data.io;

import ru.rdude.rpg.game.logic.stats.Stats;
import ru.rdude.rpg.game.logic.stats.primary.Lvl;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

public class StatsSerializable implements Externalizable {

    private Stats stats;

    public StatsSerializable(Stats stats) {
        this.stats = stats;
    }

    public Stats getStats() {
        return stats;
    }

    @Override
    public void writeExternal(ObjectOutput objectOutput) throws IOException {
        //primary:
        objectOutput.writeBoolean(stats.isCalculatable());
        objectOutput.writeDouble(stats.agiValue());
        objectOutput.writeDouble(stats.dexValue());
        objectOutput.writeDouble(stats.intelValue());
        objectOutput.writeDouble(stats.luckValue());
        objectOutput.writeDouble(stats.strValue());
        objectOutput.writeDouble(stats.vitValue());

        //lvl:
        objectOutput.writeDouble(stats.lvlValue());
        objectOutput.writeDouble(stats.lvl().exp().getMax());
        objectOutput.writeDouble(stats.lvl().exp().value());
        objectOutput.writeDouble(stats.lvl().skillPointsValue());
        objectOutput.writeDouble(stats.lvl().statPointsValue());

        //secondary:
        objectOutput.writeDouble(stats.blockValue());
        objectOutput.writeDouble(stats.concentrationValue());
        objectOutput.writeDouble(stats.critValue());
        objectOutput.writeDouble(stats.defValue());
        objectOutput.writeDouble(stats.flee().pureValue());
        objectOutput.writeDouble(stats.flee().luckyDodgeChance().value());
        objectOutput.writeDouble(stats.hitValue());
        objectOutput.writeDouble(stats.magicResistanceValue());
        objectOutput.writeDouble(stats.parryValue());
        objectOutput.writeDouble(stats.physicResistanceValue());

        //dmg:
        objectOutput.writeDouble(stats.dmg().magic().minValue());
        objectOutput.writeDouble(stats.dmg().magic().maxValue());
        objectOutput.writeDouble(stats.dmg().melee().minValue());
        objectOutput.writeDouble(stats.dmg().melee().maxValue());
        objectOutput.writeDouble(stats.dmg().range().minValue());
        objectOutput.writeDouble(stats.dmg().range().maxValue());

        //hp:
        objectOutput.writeDouble(stats.hpValue());
        objectOutput.writeDouble(stats.hp().recoveryValue());
        objectOutput.writeDouble(stats.hp().maxValue());

        //stm:
        objectOutput.writeDouble(stats.stmValue());
        objectOutput.writeDouble(stats.stm().recoveryValue());
        objectOutput.writeDouble(stats.stm().perHitValue());
        objectOutput.writeDouble(stats.stm().maxValue());
        objectOutput.writeDouble(stats.stm().hardness().value());
    }

    @Override
    public void readExternal(ObjectInput objectInput) throws IOException, ClassNotFoundException {
        //primary
        stats = new Stats(objectInput.readBoolean());
        stats.agi().set(objectInput.readDouble());
        stats.dex().set(objectInput.readDouble());
        stats.intel().set(objectInput.readDouble());
        stats.luck().set(objectInput.readDouble());
        stats.str().set(objectInput.readDouble());
        stats.vit().set(objectInput.readDouble());

        //lvl:
        stats.lvl().set(objectInput.readDouble());
        stats.lvl().exp().setMax(objectInput.readDouble());
        stats.lvl().exp().set(objectInput.readDouble());
        stats.lvl().skillPoints().set(objectInput.readDouble());
        stats.lvl().statPoints().set(objectInput.readDouble());

        //secondary:
        stats.block().set(objectInput.readDouble());
        stats.concentration().set(objectInput.readDouble());
        stats.crit().set(objectInput.readDouble());
        stats.def().set(objectInput.readDouble());
        stats.flee().set(objectInput.readDouble());
        stats.flee().luckyDodgeChance().set(objectInput.readDouble());
        stats.hit().set(objectInput.readDouble());
        stats.magicResistance().set(objectInput.readDouble());
        stats.parry().set(objectInput.readDouble());
        stats.physicResistance().set(objectInput.readDouble());

        //dmg:
        stats.dmg().magic().min().set(objectInput.readDouble());
        stats.dmg().magic().max().set(objectInput.readDouble());
        stats.dmg().melee().min().set(objectInput.readDouble());
        stats.dmg().melee().max().set(objectInput.readDouble());
        stats.dmg().range().min().set(objectInput.readDouble());
        stats.dmg().range().max().set(objectInput.readDouble());

        //hp:
        stats.hp().set(objectInput.readDouble());
        stats.hp().recovery().set(objectInput.readDouble());
        stats.hp().max().set(objectInput.readDouble());

        //stm:
        stats.stm().set(objectInput.readDouble());
        stats.stm().recovery().set(objectInput.readDouble());
        stats.stm().perHit().set(objectInput.readDouble());
        stats.stm().max().set(objectInput.readDouble());
        stats.stm().hardness().set(objectInput.readDouble());
    }
}
