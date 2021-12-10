package ru.rdude.rpg.game.logic.gameStates;

import com.fasterxml.jackson.annotation.JsonIgnore;
import ru.rdude.rpg.game.campVisual.CampVisual;
import ru.rdude.rpg.game.logic.entities.beings.Being;
import ru.rdude.rpg.game.logic.entities.beings.Party;
import ru.rdude.rpg.game.logic.enums.GameState;
import ru.rdude.rpg.game.logic.game.Game;
import ru.rdude.rpg.game.logic.map.Cell;
import ru.rdude.rpg.game.utils.jsonextension.JsonPolymorphicSubType;
import ru.rdude.rpg.game.visual.GameStateStage;

import java.util.Map;

@JsonPolymorphicSubType("camp")
public class Camp extends GameStateBase {

    @JsonIgnore
    private CampVisual campStage;
    private Cell cell;

    private Camp() {}

    public Camp(Cell cell) {
        this.cell = cell;
        Game.getCurrentGame().getCurrentPlayers().forEach(being -> being.setReady(true));
    }

    @Override
    public GameState getEnumValue() {
        return GameState.CAMP;
    }

    @Override
    public GameStateStage getStage() {
        if (campStage == null) {
            campStage = new CampVisual(Game.getCurrentGame().getGameMap().getGameMap().cell(cell.getX(), cell.getY()), this);
        }
        return campStage;
    }

    @Override
    public Party getAllySide(Being<?> of) {
        Party party = Game.getCurrentGame().getCurrentPlayers();
        return party.getBeings().contains(of) ? party : null;
    }

    @Override
    public void lose() {

    }
}
