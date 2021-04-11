package ru.rdude.rpg.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import ru.rdude.rpg.game.logic.data.io.ModuleFileLoader;
import ru.rdude.rpg.game.logic.enums.Biom;
import ru.rdude.rpg.game.logic.enums.Relief;
import ru.rdude.rpg.game.logic.game.Game;
import ru.rdude.rpg.game.logic.map.GameMap;
import ru.rdude.rpg.game.logic.map.GameMapSize;
import ru.rdude.rpg.game.logic.map.Generator;
import ru.rdude.rpg.game.logic.map.bioms.BiomCellProperty;
import ru.rdude.rpg.game.logic.map.reliefs.ReliefCellProperty;
import ru.rdude.rpg.game.mapVisual.MapStage;
import ru.rdude.rpg.game.mapVisual.MapVisual;
import ru.rdude.rpg.game.ui.*;

public class GameApp extends ApplicationAdapter {

    @Override
    public void create() {

        // player creation
/*        PlayerCreationVisual playerCreationVisual = new PlayerCreationVisual();
        stage.addActor(playerCreationVisual);
        playerCreationVisual.setY(Gdx.graphics.getHeight() - playerCreationVisual.getHeight());*/

        GameMap gameMap = new Generator(GameMapSize.XS).createMap();
        Game.getGameVisual().addStage(new MapStage(gameMap));
        Game.getGameVisual().setUi(new UIStage());
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(0, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        Game.getGameVisual().draw();
    }

    @Override
    public void dispose() {
    }
}
