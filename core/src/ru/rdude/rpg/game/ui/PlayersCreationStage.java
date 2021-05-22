package ru.rdude.rpg.game.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Tree;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import ru.rdude.rpg.game.logic.entities.beings.Party;
import ru.rdude.rpg.game.logic.entities.beings.Player;
import ru.rdude.rpg.game.logic.game.Game;
import ru.rdude.rpg.game.logic.gameStates.Map;

import java.util.ArrayList;
import java.util.List;

public class PlayersCreationStage extends Stage {

    private static PlayersCreationStage instance;
    
    private final Group playerCreationTabsGroup = new VerticalGroup();
    private Tree<PlayerCreationElement, Player> playersList = new Tree<>(UiData.DEFAULT_SKIN);


    public PlayersCreationStage() {
        super();
        Table mainTable = new Table();
        mainTable.add(playerCreationTabsGroup);

        TextButton addPlayerButton = new TextButton("Add player", UiData.DEFAULT_SKIN, UiData.SMALL_TEXT_STYLE);
        addPlayerButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (playersList.getNodes().size < 5) {
                    addPlayer();
                }
            }
        });

        playersList.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) {
                if (playerCreationTabsGroup.hasChildren()) {
                    playerCreationTabsGroup.removeActor(playerCreationTabsGroup.getChild(0));
                }
                playerCreationTabsGroup.addActor(playersList.getSelectedNode().getPlayerCreationVisual());
            }
        });

        VerticalGroup playersListVerticalGroup = new VerticalGroup();
        Table playersListTable = new Table();
        Table rightTable = new Table();
        playersListTable.background(UiData.DEFAULT_SKIN.getDrawable("Window_Transparent_9p"));
        playersListTable.add(playersListVerticalGroup).fillY().expand();
        rightTable.add(playersListTable).fillY().expand();
        rightTable.row().space(10f);
        playersListTable.pack();
        playersListVerticalGroup.addActor(playersList);
        playersListVerticalGroup.addActor(addPlayerButton);
        playersListVerticalGroup.align(Align.top);

        TextButton startGameButton = new TextButton("Start game", UiData.DEFAULT_SKIN, UiData.YES_BUTTON_STYLE);
        startGameButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Game.getGameVisual().clearMainMenus();
                Map map = Game.getCurrentGame().getGameMap();
                map.placePlayerOnStartPosition();
                List<PlayerVisual> playerVisuals = new ArrayList<>();
                List<Player> players = new ArrayList<>();
                playersList.getNodes().forEach(el -> {
                    PlayerAvatar avatar = el.getPlayerCreationVisual().getAvatar();
                    el.getPlayerCreationVisual().defaultAvatarSize();
                    Player player = el.getPlayerCreationVisual().getPlayer();
                    players.add(player);
                    player.setName(el.getPlayerCreationVisual().getNameTextField().getText());
                    playerVisuals.add(new PlayerVisual(player, avatar));
                });
                Game.getCurrentGame().setCurrentPlayers(new Party(players));
                map.createStage();
                Game.getGameVisual().addStage(map.getMapStage());
                Game.getGameVisual().setUi(new UIStage(playerVisuals.toArray(PlayerVisual[]::new)));
                Game.getCurrentGame().getGameStateHolder().setGameState(map);
            }
        });
        rightTable.add(startGameButton);
        rightTable.row().space(10f);

        TextButton backButton = new TextButton("<< Back", UiData.DEFAULT_SKIN, UiData.BIG_TEXT_STYLE);
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Game.getGameVisual().backMenuStage();
            }
        });
        rightTable.add(backButton);
        rightTable.row().space(10f);
        rightTable.pack();

        mainTable.add(rightTable).align(Align.top).fillY();
        mainTable.pack();
        addActor(mainTable);
        mainTable.setY((Gdx.graphics.getHeight() - mainTable.getHeight()) / 2);
        mainTable.setX((Gdx.graphics.getWidth() - mainTable.getWidth()) / 2);
        addPlayer();
    }

    public static PlayersCreationStage getInstance() {
        if (Game.getGameVisual().isJustOpenedMainMenu()) {
            instance = new PlayersCreationStage();
        }
        return instance;
    }

    private void addPlayer() {
        PlayerCreationVisual visual = new PlayerCreationVisual();
        PlayerCreationElement element = new PlayerCreationElement(visual);
        if (playerCreationTabsGroup.hasChildren()) {
            playerCreationTabsGroup.removeActor(playerCreationTabsGroup.getChild(0));
        }
        playerCreationTabsGroup.addActor(visual);
        playersList.add(element);
        playersList.getSelection().set(element);
        element.onDeletePressed(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (playersList.getNodes().size <= 1) {
                    return;
                }
                boolean wasSelected = playersList.getSelectedNode() == element;
                playersList.remove(element);
                if (wasSelected) {
                    playerCreationTabsGroup.removeActor(playerCreationTabsGroup.getChild(0));
                    playerCreationTabsGroup.addActor(playersList.getNodes().first().getPlayerCreationVisual());
                    playersList.getSelection().set(playersList.getNodes().first());
                }
            }
        });
        visual.getNameTextField().addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) {
                String text = visual.getNameTextField().getText();
                element.changeName(text.isBlank() ? "Default Player Name" : text);
            }
        });
    }
}
