package ru.rdude.rpg.game.mapVisual;

import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import ru.rdude.rpg.game.logic.data.MonsterData;
import ru.rdude.rpg.game.logic.data.resources.Resource;
import ru.rdude.rpg.game.logic.game.Game;
import ru.rdude.rpg.game.logic.gameStates.Map;
import ru.rdude.rpg.game.ui.MonsterOnMapTooltip;

import java.util.Comparator;

public class MonstersOnMap extends Group {

    private final Map.MonstersOnCell monsters;
    private MonsterOnMapTooltip tooltip;

    public MonstersOnMap(Map.MonstersOnCell monsters) {
        super();
        this.monsters = monsters;
        this.tooltip = new MonsterOnMapTooltip(monsters);
        addListener(tooltip);
        monsters.getMonsters().stream()
                .max(Comparator.comparingInt(a -> a.lvl))
                .map(this::createMonsterImage)
                .ifPresent(this::addActor);
    }

    public Map.MonstersOnCell getMonsters() {
        return monsters;
    }

    private Image createMonsterImage(Map.MonstersOnCell.Monster monster) {
        final Resource monsterResource = MonsterData.getMonsterByGuid(monster.guid).getResources().getMonsterImage();
        Image image = new Image(Game.getImageFactory().getRegion(monsterResource.getGuid()));
        image.setSize(150f, 150f);
        return image;
    }
}
