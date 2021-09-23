package ru.rdude.rpg.game.logic.game;

import ru.rdude.rpg.game.logic.entities.beings.*;
import ru.rdude.rpg.game.ui.DefeatStage;
import ru.rdude.rpg.game.utils.jsonextension.JsonPolymorphicSubType;

@JsonPolymorphicSubType("defeatManager")
public class DefeatManager implements PartyObserver, BeingActionObserver {

    private Party playersParty;

    private DefeatManager() {}

    public DefeatManager(Party playersParty) {
        this.playersParty = playersParty;
        playersParty.subscribe(this);
        playersParty.getBeings().forEach(being -> being.subscribe(this));
    }

    @Override
    public void update(BeingAction action, Being<?> being) {
        if (action.action() == BeingAction.Action.DIE && !playersParty.stream().allMatch(Being::isAlive)) {
           Game.getGameVisual().setMenuStage(DefeatStage.getInstance());
        }
    }

    @Override
    public void partyUpdate(Party party, boolean added, Being<?> being, int position) {
        if (added) {
            being.subscribe(this);
        }
        else {
            being.unsubscribe(this);
        }
    }
}
