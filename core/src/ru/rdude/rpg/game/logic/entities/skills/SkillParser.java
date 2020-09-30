package ru.rdude.rpg.game.logic.entities.skills;

import ru.rdude.rpg.game.logic.data.SkillData;
import ru.rdude.rpg.game.logic.entities.beings.Being;

import javax.script.Bindings;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

public class SkillParser {

    private static ScriptEngineManager scriptEngineManager;
    private static ScriptEngine engine;
    private Bindings bindings;

    private SkillData skillData;
    private Being caster;
    private Being target;

    public SkillParser(SkillData skillData, Being caster, Being target) {
        if (scriptEngineManager == null) {
            scriptEngineManager = new ScriptEngineManager();
            engine = scriptEngineManager.getEngineByName("JavaScript");
        }
        this.skillData = skillData;
        this.caster = caster;
        this.target = target;
    }

    public double parse(String string) {
        createBindings();
        double result = Float.MIN_VALUE;
        try {
            Object preResult = engine.eval(string, bindings);
            if (preResult instanceof Float) result = (double) (float) preResult;
            else if (preResult instanceof Double) result = (double) preResult;
            else if (preResult instanceof Integer) result = (double) (int) preResult;
        } catch (ScriptException e) {
            e.printStackTrace();
            System.out.printf("Parse trouble in skill: %s, target: %s, caster: %s", skillData.getName(), target.getBeingData().getName(), caster.getBeingData().getName());
            return result;
        }
        return result;
    }

    private void createBindings() {
        bindings = engine.createBindings();
        bindings.put("LVL", caster.stats().lvlValue());
        bindings.put("TLVL", target.stats().lvlValue());
        bindings.put("EXP", caster.stats().lvl().expValue());
        bindings.put("TEXP", target.stats().lvl().expValue());
        bindings.put("DEF", caster.stats().defValue());
        bindings.put("TDEF", target.stats().defValue());
        bindings.put("AGI", caster.stats().agiValue());
        bindings.put("DEX", caster.stats().dexValue());
        bindings.put("INT", caster.stats().intelValue());
        bindings.put("LUCK", caster.stats().luckValue());
        bindings.put("STR", caster.stats().strValue());
        bindings.put("VIT", caster.stats().vitValue());
        bindings.put("TAGI", target.stats().agiValue());
        bindings.put("TDEX", target.stats().dexValue());
        bindings.put("TINT", target.stats().intelValue());
        bindings.put("TLUCK", target.stats().luckValue());
        bindings.put("TSTR", target.stats().strValue());
        bindings.put("TVIT", target.stats().vitValue());
        bindings.put("STM", caster.stats().stmValue());
        bindings.put("TSTM", target.stats().stmValue());
        bindings.put("STMATK", caster.stats().stm().perHitValue());
        bindings.put("TSTMATK", target.stats().stm().perHitValue());
        bindings.put("STMMAX", caster.stats().stm().maxValue());
        bindings.put("TSTMMAX", target.stats().stm().maxValue());
        bindings.put("STMREST", caster.stats().stm().recoveryValue());
        bindings.put("TSTMREST", target.stats().stm().recoveryValue());
        bindings.put("HPMAX", caster.stats().hp().maxValue());
        bindings.put("THPMAX", target.stats().hp().maxValue());
        bindings.put("HPREST", caster.stats().hp().recovery().value());
        bindings.put("THPREST", target.stats().hp().recovery().value());
        bindings.put("ATK", caster.stats().dmg().get(caster.getAttackType()).randomValue());
        bindings.put("ATKMIN", caster.stats().dmg().get(caster.getAttackType()).minValue());
        bindings.put("ATKMAX", caster.stats().dmg().get(caster.getAttackType()).maxValue());
        bindings.put("TATK", target.stats().dmg().get(target.getAttackType()).randomValue());
        bindings.put("TATKMIN", target.stats().dmg().get(target.getAttackType()).minValue());
        bindings.put("TATKMAX", target.stats().dmg().get(target.getAttackType()).maxValue());
        bindings.put("MELEEATKMIN", caster.stats().dmg().melee().minValue());
        bindings.put("MELEEATKMAX", caster.stats().dmg().melee().maxValue());
        bindings.put("RANGEATKMIN", caster.stats().dmg().range().minValue());
        bindings.put("RANGEATKMAX", caster.stats().dmg().range().maxValue());
        bindings.put("MAGICATKMIN", caster.stats().dmg().magic().minValue());
        bindings.put("MAGICATKMAX", caster.stats().dmg().magic().maxValue());
        bindings.put("TMELEEATKMIN", target.stats().dmg().melee().minValue());
        bindings.put("TMELEEATKMAX", target.stats().dmg().melee().maxValue());
        bindings.put("TRANGEATKMIN", target.stats().dmg().range().minValue());
        bindings.put("TRANGEATKMAX", target.stats().dmg().range().maxValue());
        bindings.put("TMAGICATKMIN", target.stats().dmg().magic().minValue());
        bindings.put("TMAGICATKMAX", target.stats().dmg().magic().maxValue());
        bindings.put("CRIT", caster.stats().crit().value());
        bindings.put("TCRIT", target.stats().crit().value());
        bindings.put("PARRY", caster.stats().parry().value());
        bindings.put("TPARRY", target.stats().parry().value());
        bindings.put("HIT", caster.stats().hit().value());
        bindings.put("THIT", target.stats().hit().value());
        bindings.put("BLOCK", caster.stats().block().value());
        bindings.put("TBLOCK", target.stats().block().value());
        bindings.put("CONC", caster.stats().concentration().value());
        bindings.put("TCONC", target.stats().concentration().value());
        bindings.put("LKYDODGE", caster.stats().flee().luckyDodgeChance().value());
        bindings.put("TLKYDODGE", target.stats().flee().luckyDodgeChance().value());
        bindings.put("FLEE", caster.stats().flee().value());
        bindings.put("TFLEE", target.stats().flee().value());
        bindings.put("PRES", caster.stats().physicResistance().value());
        bindings.put("TPRES", target.stats().physicResistance().value());
        bindings.put("MRES", caster.stats().magicResistance().value());
        bindings.put("TMRES", target.stats().magicResistance().value());
    }


}
