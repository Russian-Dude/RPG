package ru.rdude.rpg.game.logic.gameStates;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.scenes.scene2d.Stage;
import ru.rdude.rpg.game.logic.data.ItemData;
import ru.rdude.rpg.game.logic.data.io.GameMapFileLoader;
import ru.rdude.rpg.game.logic.entities.beings.Party;
import ru.rdude.rpg.game.logic.entities.beings.Player;
import ru.rdude.rpg.game.logic.game.Game;
import ru.rdude.rpg.game.logic.map.Cell;
import ru.rdude.rpg.game.ui.LoadingStage;
import ru.rdude.rpg.game.ui.MapInfo;
import ru.rdude.rpg.game.ui.PlayerVisual;
import ru.rdude.rpg.game.ui.UIStage;

import java.util.List;
import java.util.stream.Collectors;

public class GameStateSwitcher {

    // selected from start menu map
    private MapInfo selectedMap;
    // players from creation stage
    private List<Player> createdPlayers;

    public void setSelectedMap(MapInfo selectedMap) {
        this.selectedMap = selectedMap;
    }

    public void setCreatedPlayers(List<Player> createdPlayers) {
        this.createdPlayers = createdPlayers;
    }

    public void startNewGame() {
        Game.getGameVisual().clearMainMenus();
        final Map map = new Map(GameMapFileLoader.load(selectedMap.mapFile));
        Game.getCurrentGame().setGameMap(map);
        map.placePlayerOnStartPosition();
        createdPlayers.forEach(player -> {
            String name = player.getName();
            player.setName(name.isBlank() ? "Default Player Name" : name); });
        final Party party = new Party(createdPlayers);
        Game.getCurrentGame().setCurrentPlayers(party);
        Game.getGameVisual().addStage(map.getStage());
        Game.getGameVisual().setUi(new UIStage());
        Game.getCurrentGame().getGameStateHolder().setGameState(map);
        Game.getEntityFactory().monsters().createMonstersOnMap(Game.getCurrentGame().getGameMap());
        Game.getCurrentGame().getGameMap().getStage().playerChangedPosition(
                Game.getCurrentGame().getGameMap().getPlayerPosition(), Game.getCurrentGame().getGameMap().getPlayerPosition());
        // TODO: 14.06.2021 remove this test
        for (ItemData itemData : ItemData.getItems().values()) {
            int c = itemData.isStackable() ? 30 : 1;
            for (int i = 0; i < c; i++) {
                Game.getCurrentGame().getCurrentPlayers().getBeings().get(0)
                        .receive(Game.getEntityFactory().items().get(itemData));
            }
        }
    }

    public void loadGame(FileHandle saveFile) {
        Runnable loadingRunnable = () -> {
            Game.getGameVisual().clear();
            Game game = Game.getGameLoader().load(saveFile);
            Game.setCurrentGame(game);
            Game.getCurrentGame().getGameMap().getStage(); // to load map stage even if is not map currently
            Game.getGameVisual().addStage((Stage) Game.getCurrentGame().getCurrentGameState().getStage());
            Game.getGameVisual().setUi(new UIStage());
        };
        Runnable onEndLoading = () -> Game.getGameVisual().closeMenus();
        Game.getGameVisual().setMenuStage(LoadingStage.instance("Loading", loadingRunnable, onEndLoading));
    }

    public void switchToMainMenu() {
        Game.getGameVisual().closeMenus();
        Game.getGameVisual().setJustOpenedMainMenu(true);
        Game.getGameVisual().goToMainMenu();
        Game.initNewGame();
    }

    public void switchToBattle(Map.MonstersOnCell monsters, Cell cell) {
        final Party monstersParty = Game.getEntityFactory().monsters().createParty(monsters);
        Battle battle = new Battle(monstersParty, cell);
        Game.getCurrentGame().getGameStateHolder().setGameState(battle);
        Game.getGameVisual().clearNonUiStages();
        Game.getGameVisual().addStage((Stage) battle.getStage());
    }

    public void switchToMap() {
        Game.getCurrentGame().getGameStateHolder().setGameState(Game.getCurrentGame().getGameMap());
        Game.getGameVisual().clearNonUiStages();
        Game.getGameVisual().addStage(Game.getCurrentGame().getGameMap().getStage());
    }

}
