package ru.rdude.rpg.game.settings;

import com.badlogic.gdx.Gdx;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;

public final class GameSettings {

    private static GameSettings instance = new GameSettings();

    private GameSettings() {
        try (FileReader reader = new FileReader(Gdx.files.local("properties.properties").file())) {
            properties.load(reader);
            cameraFollowPlayers = Boolean.parseBoolean(properties.getProperty("cameraFollowPlayers"));
            playersMovingSpeedOnMap = MovingSpeed.valueOf(properties.getProperty("playersMovingSpeedOnMap"));
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public enum MovingSpeed {
        SLOW(1),
        MEDIUM(2),
        FAST(4);

        public final int value;

        MovingSpeed(int value) {
            this.value = value;
        }
    }

    private Properties properties = new Properties();
    private boolean cameraFollowPlayers;
    private MovingSpeed playersMovingSpeedOnMap;

    public static boolean isCameraFollowPlayers() {
        return instance.cameraFollowPlayers;
    }

    public static void setCameraFollowPlayers(boolean cameraFollowPlayers) {
        instance.cameraFollowPlayers = cameraFollowPlayers;
        instance.properties.setProperty("cameraFollowPlayers", String.valueOf(cameraFollowPlayers));
        try (FileWriter writer = new FileWriter(Gdx.files.local("properties.properties").file())) {
            instance.properties.store(writer, "");
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static MovingSpeed getPlayersMovingSpeedOnMap() {
        return instance.playersMovingSpeedOnMap;
    }

    public static void setPlayersMovingSpeedOnMap(MovingSpeed playersMovingSpeedOnMap) {
        instance.playersMovingSpeedOnMap = playersMovingSpeedOnMap;
        instance.properties.setProperty("playersMovingSpeedOnMap", playersMovingSpeedOnMap.toString());
        try (FileWriter writer = new FileWriter(Gdx.files.local("properties.properties").file())) {
            instance.properties.store(writer, "");
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
