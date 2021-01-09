package ru.rdude.rpg.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import ru.rdude.rpg.game.logic.GameLogger;
import ru.rdude.rpg.game.logic.data.ItemData;
import ru.rdude.rpg.game.logic.data.ItemVisualData;
import ru.rdude.rpg.game.logic.entities.beings.Player;
import ru.rdude.rpg.game.logic.entities.items.Item;
import ru.rdude.rpg.game.logic.enums.ItemType;
import ru.rdude.rpg.game.logic.map.GameMap;
import ru.rdude.rpg.game.logic.map.Generator;
import ru.rdude.rpg.game.logic.map.bioms.Biom;
import ru.rdude.rpg.game.logic.map.reliefs.Relief;
import ru.rdude.rpg.game.logic.stats.Stats;
import ru.rdude.rpg.game.mapVisual.MapVisual;
import ru.rdude.rpg.game.ui.*;

public class GameApp extends ApplicationAdapter {

    Stage stage;
    OrthographicCamera camera;

    @Override
    public void create() {
        stage = new Stage(new ScreenViewport());
        stage.addActor(new PlayersVisualBottom());
        Player player = new Player();
        stage.addActor(new EquipmentWindow(player));
        BackpackWindow backpackWindow = new BackpackWindow(player);
        stage.addActor(backpackWindow);
        Item item = new Item(new ItemData(35135));
        ItemVisualData itemVisualData = new ItemVisualData();
        item.getItemData().setItemType(ItemType.SWORD);
        item.getItemData().setRequirements(new Stats(false));
        ItemVisual itemVisual = new ItemVisual(item);
        stage.addActor(itemVisual);
        //((ItemSlotVisual) backpackWindow.getCells().first().getActor()).setItemVisual(itemVisual);
        player.receive(item);


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
