package ru.rdude.rpg.game.logic.data;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import ru.rdude.rpg.game.logic.data.resources.PlayerResources;
import ru.rdude.rpg.game.logic.enums.BeingType;
import ru.rdude.rpg.game.logic.enums.Element;
import ru.rdude.rpg.game.logic.enums.Size;
import ru.rdude.rpg.game.logic.statistics.PlayerStatistics;
import ru.rdude.rpg.game.utils.Functions;

import java.util.HashSet;
import java.util.Set;

@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE,
        fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class PlayerData extends BeingData {

    private PlayerStatistics statistics;

    public PlayerData() {
        super(Functions.generateGuid());
        statistics = new PlayerStatistics();
        Set<BeingType> beingTypes = new HashSet<>();
        beingTypes.add(BeingType.HUMAN);
        super.setBeingTypes(beingTypes);
        Set<Element> elements = new HashSet<>();
        elements.add(Element.NEUTRAL);
        super.setElements(elements);
        super.setSize(Size.MEDIUM);
        super.setResources(new PlayerResources());
    }

    @Override
    public boolean hasEntityDependency(long guid) {
        return false;
    }

    @Override
    public void replaceEntityDependency(long oldValue, long newValue) {
    }
}
