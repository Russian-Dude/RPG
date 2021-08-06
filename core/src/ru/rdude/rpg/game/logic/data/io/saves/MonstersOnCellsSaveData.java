package ru.rdude.rpg.game.logic.data.io.saves;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import ru.rdude.rpg.game.logic.gameStates.Map;

import java.util.ArrayList;
import java.util.List;

public class MonstersOnCellsSaveData {

    @JsonProperty("monstersOnCellsData")
    public final String monstersOnCellsData;

    @JsonCreator
    public MonstersOnCellsSaveData(@JsonProperty("monstersOnCellsData") String monstersOnCellsData) {
        this.monstersOnCellsData = monstersOnCellsData;
    }

    public MonstersOnCellsSaveData(Map.CellProperties[][] properties) {
        StringBuilder stringBuilder = new StringBuilder();
        int countNoMonsters = 0;
        for (int x = 0; x < properties.length; x++) {
            for (int y = 0; y < properties[0].length; y++) {
                final Map.MonstersOnCell monsters = properties[x][y].getMonsters();
                if (monsters == null || monsters.isEmpty()) {
                    countNoMonsters++;
                }
                else {
                    if (countNoMonsters > 0) {
                        stringBuilder
                                .append("f")
                                .append(countNoMonsters);
                        countNoMonsters = 0;
                    }
                    stringBuilder.append("t");
                    monsters.getMonsters().stream()
                            .map(monster -> "m" + monster.guid + "l" + monster.lvl)
                            .forEach(stringBuilder::append);
                }
            }
        }
        if (countNoMonsters > 0) {
            stringBuilder
                    .append("f")
                    .append(countNoMonsters);
        }
        monstersOnCellsData = stringBuilder.toString();
    }

    public void acceptMonstersTo(Map.CellProperties[][] properties) {
        final String[] blocks = monstersOnCellsData.split("[tf]");
        int i = 1;
        boolean hasMonsters = blocks[i].startsWith("m");
        int currentNoMonstersCount = hasMonsters ? 0 : Integer.parseInt(blocks[i]);
        for (int x = 0; x < properties.length; x++) {
            for (int y = 0; y < properties[0].length; y++) {
                if (hasMonsters) {
                    final Map.CellProperties cell = properties[x][y];
                    List<Map.MonstersOnCell.Monster> monstersOnCell = new ArrayList<>();
                    final String[] monsters = blocks[i].split("m");
                    for (int m = 1; m < monsters.length; m++) {
                        String monsterString = monsters[m];
                        final int index = monsterString.indexOf("l");
                        long guid = Long.parseLong(monsterString.substring(0, index));
                        int lvl = Integer.parseInt(monsterString.substring(index + 1));
                        monstersOnCell.add(new Map.MonstersOnCell.Monster(guid, lvl));
                    }
                    cell.setMonsters(new Map.MonstersOnCell(monstersOnCell));
                    i++;
                    if (i < blocks.length) {
                        hasMonsters = blocks[i].startsWith("m");
                    }
                    if (!hasMonsters) {
                        currentNoMonstersCount = Integer.parseInt(blocks[i]);
                    }
                }
                else {
                   currentNoMonstersCount--;
                   if (currentNoMonstersCount <= 0) {
                       i++;
                       if (i < blocks.length) {
                           hasMonsters = blocks[i].startsWith("m");
                       }
                   }
                }
            }
        }
    }


}
