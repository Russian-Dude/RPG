package ru.rdude.rpg.game.logic.entities.beings;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import ru.rdude.rpg.game.logic.data.MonsterData;
import ru.rdude.rpg.game.logic.data.SkillData;
import ru.rdude.rpg.game.logic.entities.skills.SkillUser;
import ru.rdude.rpg.game.logic.enums.AttackType;
import ru.rdude.rpg.game.logic.enums.Target;
import ru.rdude.rpg.game.logic.game.Game;
import ru.rdude.rpg.game.utils.jsonextension.JsonPolymorphicSubType;

@JsonPolymorphicSubType("monster")
public class Monster extends Being<MonsterData> {

    @JsonCreator
    protected Monster(@JsonProperty("entityData") long guid) {
        super(guid);
    }

    public Monster(MonsterData monsterData) {
        super(monsterData);
        monsterData.getStats().copyTo(stats);
    }

    public void applyStartBuffs() {
        entityData.getStartBuffs().forEach(guid ->
                Game.getSkillUser().use(Game.getEntityFactory().skills().describerToReal(guid), this, Target.SELF));
    }

    @Override
    public AttackType getAttackType() {
        return entityData.getDefaultAttackType();
    }

    @Override
    public boolean canBlock() {
        return entityData.isCanBlock();
    }

    @Override
    public boolean canParry() {
        return entityData.isCanParry();
    }

    @Override
    public String getName() {
        return entityData.getName();
    }

    @Override
    protected MonsterData getDataByGuid(long guid) {
        return MonsterData.getMonsterByGuid(guid);
    }
}
