package ru.rdude.rpg.game.logic.entities.beings;

import ru.rdude.rpg.game.logic.data.MonsterData;
import ru.rdude.rpg.game.logic.data.SkillData;
import ru.rdude.rpg.game.logic.entities.skills.Buff;
import ru.rdude.rpg.game.logic.entities.skills.SkillApplier;
import ru.rdude.rpg.game.logic.entities.skills.SkillUser;
import ru.rdude.rpg.game.logic.enums.AttackType;
import ru.rdude.rpg.game.logic.game.Game;

public class Monster extends Being {

    public Monster(MonsterData monsterData) {
        super(monsterData);
        monsterData.getStats().copyTo(stats);
    }

    public void applyStartBuffs() {
        ((MonsterData) beingData).getStartBuffs().forEach(guid ->
                Game.getCurrentGame().getSkillUser().use(SkillData.getSkillByGuid(guid), this, this));
    }

    @Override
    public AttackType getAttackType() {
        return ((MonsterData) beingData).getDefaultAttackType();
    }

    @Override
    public boolean canBlock() {
        return ((MonsterData) beingData).isCanBlock();
    }

    @Override
    public boolean canParry() {
        return ((MonsterData) beingData).isCanParry();
    }

    @Override
    public String getName() {
        return beingData.getName();
    }
}
