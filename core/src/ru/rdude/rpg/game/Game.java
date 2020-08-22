package ru.rdude.rpg.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextTooltip;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import ru.rdude.rpg.game.logic.entities.beings.Player;
import ru.rdude.rpg.game.ui.*;

public class Game extends ApplicationAdapter {

    Stage stage;

    @Override
    public void create() {
        stage = new Stage(new ScreenViewport());
        stage.addActor(new PlayersVisualBottom());
        Player player = new Player();
        stage.addActor(new EquipmentWindow(player));
        BackpackWindow backpackWindow = new BackpackWindow(player);
        stage.addActor(backpackWindow);
        //ItemVisual itemVisual = new ItemVisual();
        //stage.addActor(itemVisual);
        //((ItemSlotVisual) backpackWindow.getCells().first().getActor()).setItemVisual(itemVisual);


        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(0, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();
    }

    @Override
    public void dispose() {
    }
}
