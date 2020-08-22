package ru.rdude.rpg.game.utils;

import java.util.*;

public class Functions {
    public static float random(float min, float max) {
        return (float) new Random().doubles(1L, min, max).findFirst().getAsDouble();
    }

    public static double random(double min, double max) {
        return new Random().doubles(1L, min, max + 0.00000001d).findFirst().getAsDouble();
    }

    public static int random(int min, int max) {
        return (int) Math.floor(new Random().doubles(1L, min, max + 0.999999).findFirst().getAsDouble());
    }

    public static long random(long min, long max) {
        return (long) Math.floor(new Random().doubles(1L, min, max + 0.999999).findFirst().getAsDouble());
    }

    public static float random(float max) {
        return (float) new Random().doubles(1L, 0, max).findFirst().getAsDouble();
    }

    public static double random(double max) {
        return new Random().doubles(1L, 0, max).findFirst().getAsDouble();
    }

    public static int random(int max) {
        return (int) Math.floor(new Random().doubles(1L, 0, max + 0.999999).findFirst().getAsDouble());
    }

    public static float random(long max) {
        return (float) Math.floor(new Random().doubles(1L, 0, max + 0.999999).findFirst().getAsDouble());
    }

    public static float random(String min, String max) {
        return (float) new Random().doubles(1L, Float.parseFloat(min), Float.parseFloat(max)).findFirst().getAsDouble();
    }

    public static <T> T random(List<T> list) {
        return list.get(random(0, list.size() - 1));
    }

    public static <T> T random(T... values) {
        if (values.length < 1) return null;
        return values[random(0, values.length - 1)];
    }

    public static <T> T randomMapKey(Map<T, ?> map) {
        if (map.isEmpty()) return null;
        return (T) Arrays.asList(map.keySet().toArray()).get(random(0, map.size() - 1));
    }

    public static <T> T randomMapValue(Map<?, T> map) {
        if (map.isEmpty()) return null;
        return (T) Arrays.asList(map.values().toArray()).get(random(0, map.size() - 1));
    }

    public static boolean randomBoolean() {
        return new Random().nextBoolean();
    }

    public static <K> K randomWithWeights(Map<K, Double> map) {
        if (map.isEmpty()) return null;
        double sum = map.values().stream().reduce(0d, Double::sum);
        double current = 0d;
        double random = random(0d, sum);
        //System.out.printf("map size: %d; coefficients sum: %s; random value: %s", map.size(), String.valueOf(sum), String.valueOf(random));
        for (Map.Entry<K, Double> entry : map.entrySet()) {
            current += entry.getValue();
            if (current >= random) return entry.getKey();
        }
        throw new IllegalArgumentException("this exception must be unreachable");
    }

    public static long generateGuid() {
        try {
            long date = new Date().getTime();
            long randomNumber = new Random().nextLong();
            randomNumber = randomNumber < 0 ? randomNumber * (-1) : randomNumber;
            String string = date + String.valueOf(randomNumber).substring(12, 17);
            return Long.parseLong(string);
        } catch (StringIndexOutOfBoundsException e) {
            return generateGuid();
        }
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
