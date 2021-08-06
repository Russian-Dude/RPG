package ru.rdude.rpg.game.logic.data.io.saves;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import ru.rdude.rpg.game.logic.gameStates.Map;

public class CellsVisibilitySaveData {

    @JsonProperty("visibilityData")
    public final String visibilityData;

    @JsonCreator
    public CellsVisibilitySaveData(@JsonProperty("visibilityData") String visibilityData) {
        this.visibilityData = visibilityData;
    }

    public CellsVisibilitySaveData(Map.CellProperties[][] properties) {
        StringBuilder stringBuilder = new StringBuilder();
        boolean current = properties[0][0].isVisible();
        int count = 0;
        for (int x = 0; x < properties.length; x++) {
            for (int y = 0; y < properties[0].length; y++) {
                final boolean visible = properties[x][y].isVisible();
                if (current == visible) {
                    count++;
                }
                else {
                    String s = (current ? "t" : "f") + count;
                    stringBuilder.append(s);
                    count = 1;
                    current = visible;
                }
            }
        }
        String s = (current ? "t" : "f") + count;
        stringBuilder.append(s);
        visibilityData = stringBuilder.toString();
    }

    public void acceptVisibilityTo(Map.CellProperties[][] properties) {
        final String[] counts = visibilityData.split("[tf]");
        int currentCounts = 1;
        int i = Integer.parseInt(counts[1]);
        boolean visibility = visibilityData.charAt(0) == 't';
        for (int x = 0; x < properties.length; x++) {
            for (int y = 0; y < properties[0].length; y++) {
                if (i <= 0) {
                    currentCounts++;
                    i = Integer.parseInt(counts[currentCounts]);
                    visibility = !visibility;
                }
                properties[x][y].setVisible(visibility);
                i--;
            }
        }
    }
}
