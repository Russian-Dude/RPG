package ru.rdude.rpg.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import ru.rdude.rpg.game.logic.data.io.ModuleFileLoader;
import ru.rdude.rpg.game.logic.game.Game;
import ru.rdude.rpg.game.ui.*;

public class GameApp extends ApplicationAdapter {

    Stage stage;
    OrthographicCamera camera;

    @Override
    public void create() {
        stage = new Stage(new ScreenViewport());

        PlayerCreationVisual playerCreationVisual = new PlayerCreationVisual();
        stage.addActor(playerCreationVisual);
        playerCreationVisual.setY(Gdx.graphics.getHeight() - playerCreationVisual.getHeight());

/*        Player p = new Player();
        p.stats().lvl().increase(5);
        stage.addActor(new PlayersVisualBottom(new Player(), new Player(), new Player(), p));
        Item item = new Item(new ItemData(35135));
        ItemVisualData itemVisualData = new ItemVisualData();
        item.getItemData().setName("Default sword");
        item.getItemData().setItemType(ItemType.SWORD);
        item.getItemData().setRequirements(new Stats(false));
        ItemVisual itemVisual = new ItemVisual(item);
        stage.addActor(itemVisual);
        LoggerVisual loggerVisual = new LoggerVisual();
        stage.addActor(loggerVisual);*/
        //((ItemSlotVisual) backpackWindow.getCells().first().getActor()).setItemVisual(itemVisual);


        Gdx.input.setInputProcessor(stage);

        float w = Gdx.graphics.getWidth();
        float h = Gdx.graphics.getHeight();
        camera = new OrthographicCamera();
        camera.setToOrtho(false, w, h);
        camera.update();


        //stage = new Stage(new FitViewport(w, h));
        //stage = new Stage();

        stage.getViewport().setCamera(camera);



/*        GameMap gameMap = new Generator(128, 64, Biom.getDefaultBiomes(), Relief.getDefaultReliefs(), 0, 0)
                .createMap();
*//*        GameMap gameMap = new Generator(1024, 512, Biom.getDefaultBiomes(), Relief.getDefaultReliefs(), 0, 0)
                .createMap();*//*
        MapVisual mapVisual = new MapVisual(camera, gameMap);
        stage.addActor(mapVisual);

        stage.setDebugAll(true);*/

    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(0, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        camera.update();
        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();
    }

    @Override
    public void dispose() {
    }
}
