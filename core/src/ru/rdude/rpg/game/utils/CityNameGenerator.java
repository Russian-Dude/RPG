package ru.rdude.rpg.game.utils;

import com.badlogic.gdx.Gdx;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public final class CityNameGenerator {

    private static List<String> start;
    private static List<String> middle;
    private static List<String> end;

    private CityNameGenerator() {}

    public static String generate() {
        if (start == null) {
            createLists();
        }
        int middles = Functions.random(0, 3);
        StringBuilder stringBuilder = new StringBuilder();
        // start
        stringBuilder.append(Functions.random(start));
        // middle
        for (int i = 0; i < middles; i++) {
            stringBuilder.append(Functions.random(middle));
        }
        // end
        stringBuilder.append(Functions.random(end));
        return stringBuilder.toString();
    }

    private static void createLists() {
        try {
            start = Gdx.files.internal("names\\city\\start").readString().lines().collect(Collectors.toList());
            middle = Gdx.files.internal("names\\city\\middle").readString().lines().collect(Collectors.toList());
            end = Gdx.files.internal("names\\city\\end").readString().lines().collect(Collectors.toList());
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

}
