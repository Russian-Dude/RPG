package ru.rdude.rpg.game.utils;

import com.badlogic.gdx.math.RandomXS128;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class Functions {

    private static RandomXS128 random = new RandomXS128();

    public static int random(int to) {
        return random.nextInt(to);
    }

    public static int random(int min, int max) {
        return random.nextInt(max - min) + min;
    }

    public static float random(float min, float max) {
        return (float) ThreadLocalRandom.current().nextDouble(min, max);
    }

    public static double random(double min, double max) {
        return ThreadLocalRandom.current().nextDouble(min, max);
    }

    public static long random(long min, long max) {
        return random.nextLong(min - max) + min;
    }

    public static float random(float max) {
        return (float) ThreadLocalRandom.current().nextDouble(max);
    }

    public static double random(double max) {
        return ThreadLocalRandom.current().nextDouble(max);
    }

    public static float random(long max) {
        return random.nextLong(max);
    }

    public static float random(String min, String max) {
        return (float) random(Double.parseDouble(min), Double.parseDouble(max));
    }

    public static <T> T random(List<T> list) {
        if (list.size() == 1)
            return list.get(0);
        return list.get(random(0, list.size()));
    }

    public static <T> T random(Set<T> set) {
        return set.stream()
                .skip(random(set.size()))
                .findAny()
                .orElse(null);
    }

    public static <T> T random(T... values) {
        if (values.length < 1) return null;
        return values[random(0, values.length)];
    }

    public static <T> T randomMapKey(Map<T, ?> map) {
        if (map.isEmpty()) return null;
        return random(map.keySet());
    }

    public static <T> T randomMapValue(Map<?, T> map) {
        if (map.isEmpty()) return null;
        return random(map.entrySet()).getValue();
    }

    public static boolean randomBoolean() {
        return random.nextBoolean();
    }

    public static <K> K randomWithWeights(Map<K, Double> map) {
        if (map.isEmpty()) return null;
        double sum = map.values().stream().reduce(0d, Double::sum);
        double current = 0d;
        double random = random(0d, sum);
        for (Map.Entry<K, Double> entry : map.entrySet()) {
            current += entry.getValue();
            if (current >= random) return entry.getKey();
        }
        throw new IllegalArgumentException("this exception must be unreachable");
    }

    public static long generateGuid() {
        try {
            long date = new Date().getTime();
            long randomNumber = random.nextLong();
            randomNumber = randomNumber < 0 ? randomNumber * (-1) : randomNumber;
            String string = date + String.valueOf(randomNumber).substring(12, 17);
            return Long.parseLong(string);
        } catch (StringIndexOutOfBoundsException e) {
            return generateGuid();
        }
    }

    public static String addSlashToString(String string) {
        return string.endsWith("\\") || string.endsWith("/") ? string : string + "\\";
    }

    public static String trimPath(String string) {
        return string.substring(Math.max(string.lastIndexOf("/"), string.lastIndexOf("\\")) + 1);
    }

    /*
    public static class Visual {

        public static double getGlobalX (Node node) {
            return node.localToScene(node.getLayoutX() + node.getTranslateX(),
                    node.getLayoutY() + node.getTranslateY()).getX();
        }

        public static double getGlobalY (Node node) {
            return node.localToScene(node.getLayoutX() + node.getTranslateX(),
                    node.getLayoutY() + node.getTranslateY()).getY();
        }

        public static void setGlobalX (Node node, double value) {
            node.relocate(value, getGlobalY(node));
        }

        public static void setGlobalX (Node node, Node moveTo) {
            setGlobalX(node, getGlobalX(moveTo));
        }

        public static void setGlobalY (Node node, double value) {
            node.relocate(getGlobalY(node), value);
        }

        public static void setGlobalY (Node node, Node moveTo) {
            setGlobalY(node, getGlobalY(moveTo));
        }

        public static void setGlobalXY (Node node, double X, double Y) {
            node.relocate(X, Y);
        }

        public static void setGlobalXY (Node node, Node moveTo) {
            setGlobalXY(node, getGlobalX(moveTo), getGlobalY(moveTo));
        }
    }

     */
}
