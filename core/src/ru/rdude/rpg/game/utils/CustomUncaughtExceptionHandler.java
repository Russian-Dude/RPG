package ru.rdude.rpg.game.utils;

import ru.rdude.rpg.game.logic.game.Game;
import ru.rdude.rpg.game.settings.GameSettings;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CustomUncaughtExceptionHandler implements Thread.UncaughtExceptionHandler {

    private final Thread.UncaughtExceptionHandler defaultHandler = Thread.getDefaultUncaughtExceptionHandler();

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        e.printStackTrace(printWriter);
        printWriter.flush();

        String fileName = "game-" + new SimpleDateFormat("yyyy-MM-dd-hh-mm-ss").format(new Date());
        try {
            Files.writeString(Path.of(GameSettings.getLogsDirectory().toString(), fileName + ".txt"), stringWriter.toString());
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        e.printStackTrace();

        defaultHandler.uncaughtException(t, e);
    }
}
