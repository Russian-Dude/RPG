package ru.rdude.rpg.game.settings;

public final class GameSettings {

    private GameSettings() { }

    public enum MovingSpeed {
        SLOW(1),
        MEDIUM(2),
        FAST(4);

        public final int value;

        MovingSpeed(int value) {
            this.value = value;
        }
    }

    private static boolean cameraFollowPlayers = false;
    private static MovingSpeed playersMovingSpeedOnMap = MovingSpeed.MEDIUM;

    public static boolean isCameraFollowPlayers() {
        return cameraFollowPlayers;
    }

    public static void setCameraFollowPlayers(boolean cameraFollowPlayers) {
        GameSettings.cameraFollowPlayers = cameraFollowPlayers;
    }

    public static MovingSpeed getPlayersMovingSpeedOnMap() {
        return playersMovingSpeedOnMap;
    }

    public static void setPlayersMovingSpeedOnMap(MovingSpeed playersMovingSpeedOnMap) {
        GameSettings.playersMovingSpeedOnMap = playersMovingSpeedOnMap;
    }
}
