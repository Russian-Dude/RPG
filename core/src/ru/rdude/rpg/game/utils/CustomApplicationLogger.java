package ru.rdude.rpg.game.utils;

import com.badlogic.gdx.ApplicationLogger;
import com.badlogic.gdx.Gdx;

public class CustomApplicationLogger implements ApplicationLogger {

    private final ApplicationLogger defaultLogger = Gdx.app.getApplicationLogger();

    @Override
    public void log(String tag, String message) {
        defaultLogger.log(tag, message);
    }

    @Override
    public void log(String tag, String message, Throwable exception) {
        defaultLogger.log(tag, message, exception);
    }

    @Override
    public void error(String tag, String message) {
        defaultLogger.error(tag, message);
    }

    @Override
    public void error(String tag, String message, Throwable exception) {
        defaultLogger.error(tag, message, exception);
    }

    @Override
    public void debug(String tag, String message) {
        defaultLogger.debug(tag, message);
    }

    @Override
    public void debug(String tag, String message, Throwable exception) {
        defaultLogger.debug(tag, message, exception);
    }
}
