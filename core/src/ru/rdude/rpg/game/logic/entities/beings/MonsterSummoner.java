package ru.rdude.rpg.game.logic.entities.beings;

import ru.rdude.rpg.game.logic.data.SkillData;
import ru.rdude.rpg.game.logic.enums.GameState;
import ru.rdude.rpg.game.logic.game.Game;
import ru.rdude.rpg.game.logic.gameStates.Battle;

public class MonsterSummoner {

    public void summon(Being<?> summoner, Being<?> target, Minion minion, SkillData withSkill) {
        Party party;
        if (target instanceof Player || Game.getCurrentGame().getCurrentGameState().getEnumValue() != GameState.BATTLE) {
            party = Game.getCurrentGame().getCurrentPlayers();
        }
        else if (Game.getCurrentGame().getCurrentGameState().getEnumValue() == GameState.BATTLE) {
            party = ((Battle) Game.getCurrentGame().getCurrentGameState()).getEnemySide();
        }
        else {
            throw new IllegalArgumentException("Do not know where to summon");
        }
        summonToParty(party, summoner, target, minion, withSkill);
    }

    private void summonToParty(Party party, Being<?> summoner, Being<?> target, Minion minion, SkillData withSkill) {
        party.addToTheRightFrom(target, minion);
        target.addMinion(minion);
        summoner.notifySubscribers(new BeingAction(BeingAction.Action.SUMMON, minion, withSkill, 1d), summoner);
    }

}
