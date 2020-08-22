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

    public boolean testParse(String string) {
        return parse(string) != Float.MIN_VALUE;
    }

    private void createBindings() {
        bindings = engine.createBindings();
        bindings.put("LVL", caster.stats().lvlValue());
        bindings.put("TLVL", target.stats().lvlValue());
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
        bindings.put("STMREC", caster.stats().stm().recoveryValue());
        bindings.put("TSTMREC", target.stats().stm().recoveryValue());
        bindings.put("MAXHP", caster.stats().hp().maxValue());
        bindings.put("TMAXHP", target.stats().hp().maxValue());

        double atk;
        switch (caster.getAttackType()) {
            case MELEE:
                atk = caster.stats().dmg().melee().randomValue();
                break;
            case RANGE:
                atk = caster.stats().dmg().range().randomValue();
                break;
            case MAGIC:
                atk = caster.stats().dmg().magic().randomValue();
                break;
            case WEAPON_TYPE:
                atk = caster.stats().dmg().value();
                break;
            default:
                atk = 0;
        }
        bindings.put("ATK", atk);

        switch (target.getAttackType()) {
            case MELEE:
                atk = caster.stats().dmg().melee().randomValue();
                break;
            case RANGE:
                atk = caster.stats().dmg().range().randomValue();
                break;
            case MAGIC:
                atk = caster.stats().dmg().magic().randomValue();
                break;
            case WEAPON_TYPE:
                atk = caster.stats().dmg().value();
                break;
        };
        bindings.put("TATK", atk);
    }


}
