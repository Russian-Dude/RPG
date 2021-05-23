package ru.rdude.rpg.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import ru.rdude.rpg.game.logic.game.Game;

public class GameApp extends ApplicationAdapter {

    @Override
    public void create() {
        GameStarter.startGame();
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        Game.getGameVisual().draw();
    }

    @Override
    public void dispose() {
    }
}
