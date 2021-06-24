package ru.rdude.rpg.game.ui;

import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Tooltip;
import com.badlogic.gdx.utils.Align;
import com.fasterxml.jackson.annotation.JsonIgnoreType;
import ru.rdude.rpg.game.logic.coefficients.Coefficients;
import ru.rdude.rpg.game.logic.data.ItemData;
import ru.rdude.rpg.game.logic.data.SkillData;
import ru.rdude.rpg.game.logic.entities.items.Item;
import ru.rdude.rpg.game.logic.entities.states.StateObserver;
import ru.rdude.rpg.game.logic.enums.Element;
import ru.rdude.rpg.game.utils.Functions;

import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@JsonIgnoreType
public class ItemInfoTooltip extends Tooltip<Table> implements StateObserver<Element> {

    private final Label elements;

    public ItemInfoTooltip(Item item) {
        super(new Table());
        setInstant(true);
        ItemData itemData = item.getEntityData();
        Table mainTable = getActor();
        mainTable.columnDefaults(0).space(10f);

        // name
        Label nameLabel = new Label(itemData.getName(), UiData.DEFAULT_SKIN, UiData.BIG_TEXT_STYLE);
        nameLabel.setAlignment(Align.center);
        mainTable.add(nameLabel)
                .align(Align.center)
                .row();

        // type
        Label itemType = new Label(itemData.getItemType().name(), UiData.DEFAULT_SKIN, UiData.BIG_TEXT_STYLE);
        itemType.setAlignment(Align.center);
        mainTable.add(itemType)
                .align(Align.center)
                .row();

        String elementsString = itemData.getElements().stream()
                .map(Element::name)
                .collect(Collectors.joining(" "));

        // damage
        if (itemData.isWeapon()) {
            String damageString =
                    Functions.trimDouble(itemData.getWeaponData().getMinDmg())
                            + " - "
                            + Functions.trimDouble(itemData.getWeaponData().getMaxDmg())
                            + " " + itemData.getWeaponData().getAttackType().name()
                            + (elementsString.isEmpty() ? "" : " " + elementsString)
                            + " DAMAGE";
            Label damage = new Label(damageString, UiData.DEFAULT_SKIN, UiData.BIG_TEXT_STYLE);
            itemType.setAlignment(Align.center);
            mainTable.add(damage)
                    .align(Align.center)
                    .row();
        }

        // elements
        elements = new Label("", UiData.DEFAULT_SKIN, UiData.SMALL_TEXT_STYLE);
        if (!elementsString.isEmpty() && !itemData.isWeapon()) {
            elements.setText(elementsString);
            itemType.setAlignment(Align.center);
            mainTable.add(elements)
                    .align(Align.center)
                    .row();
        }

        // stats
        Table statsTable = new Table();
        itemData.getStats().streamWithNestedStats()
                .filter(stat -> stat.value() != 0.0)
                .forEach(stat -> {
                    String value = Functions.trimDouble(stat.value());
                    if (stat.value() > 0) {
                        value = "+" + value;
                    }
                    Label statName = new Label(stat.getName(), UiData.DEFAULT_SKIN, UiData.SMALL_TEXT_STYLE);
                    Label statValue = new Label(value, UiData.DEFAULT_SKIN, UiData.SMALL_TEXT_STYLE);
                    statsTable.add(statName)
                            .space(5f)
                            .padRight(10f);
                    statsTable.add(statValue)
                            .align(Align.left)
                            .space(5f);
                    statsTable.row();
                });
        if (statsTable.hasChildren()) {
            statsTable.pack();
            mainTable.add(statsTable)
                    .align(Align.center)
                    .row();
        }

        // skills on use
        String skillsOnUseString = itemData.getSkillsOnUse().stream()
                .map(SkillData::getSkillByGuid)
                .map(SkillData::getName)
                .collect(Collectors.joining(", "));
        if (!skillsOnUseString.isBlank()) {
            Label cast = new Label("Cast: ", UiData.DEFAULT_SKIN, UiData.BIG_TEXT_STYLE);
            Label skillsOnUse = new Label(skillsOnUseString, UiData.DEFAULT_SKIN, UiData.SMALL_TEXT_STYLE);
            HorizontalGroup group = new HorizontalGroup();
            group.addActor(cast);
            group.addActor(skillsOnUse);
            mainTable.add(group)
                    .align(Align.center)
                    .row();
        }

        // skills on equip
        String skillsOnEquipString = itemData.getSkillsEquip().stream()
                .map(SkillData::getSkillByGuid)
                .map(SkillData::getName)
                .collect(Collectors.joining(", "));
        if (!skillsOnEquipString.isBlank()) {
            Label allowsToUse = new Label("Allows to use: ", UiData.DEFAULT_SKIN, UiData.BIG_TEXT_STYLE);
            Label skillsOnEquip = new Label(skillsOnEquipString, UiData.DEFAULT_SKIN, UiData.SMALL_TEXT_STYLE);
            HorizontalGroup group = new HorizontalGroup();
            group.addActor(allowsToUse);
            group.addActor(skillsOnEquip);
            mainTable.add(group)
                    .align(Align.center)
                    .row();
        }

        // coefficients
        if (itemData.getCoefficients() != null) {
            Coefficients coefficients = itemData.getCoefficients();
            StringBuilder builder = new StringBuilder();
            coefficients.atk().attackType().getCoefficientsMap().forEach((type, value) -> {
                String s = createCoefficientString(type, value, "deal ", "with ", " attacks");
                builder.append(s);
            });
            coefficients.atk().beingType().getCoefficientsMap().forEach((type, value) -> {
                String s = createCoefficientString(type, value, "deal ", "to ", " enemies");
                builder.append(s);
            });
            coefficients.atk().element().getCoefficientsMap().forEach((type, value) -> {
                String s = createCoefficientString(type, value, "deal ", "to ", " enemies");
                builder.append(s);
            });
            coefficients.atk().size().getCoefficientsMap().forEach((type, value) -> {
                String s = createCoefficientString(type, value, "deal ", "to ", " enemies");
                builder.append(s);
            });

            coefficients.def().attackType().getCoefficientsMap().forEach((type, value) -> {
                String s = createCoefficientString(type, value, "receive ", "from ", " attacks");
                builder.append(s);
            });
            coefficients.def().beingType().getCoefficientsMap().forEach((type, value) -> {
                String s = createCoefficientString(type, value, "receive ", "from ", " enemies");
                builder.append(s);
            });
            coefficients.def().element().getCoefficientsMap().forEach((type, value) -> {
                String s = createCoefficientString(type, value, "receive ", "from ", " attacks");
                builder.append(s);
            });
            coefficients.def().size().getCoefficientsMap().forEach((type, value) -> {
                String s = createCoefficientString(type, value, "receive ", "from ", " enemies");
                builder.append(s);
            });
            String result = builder.toString();
            if (!result.isBlank()) {
                Label coefficientsLable = new Label(result, UiData.DEFAULT_SKIN, UiData.SMALL_TEXT_STYLE);
                mainTable.add(coefficientsLable);
                mainTable.row();
            }
        }

        // price
        Label price = new Label("Price: " + Functions.trimDouble(itemData.getPrice()), UiData.DEFAULT_SKIN, UiData.SMALL_TEXT_STYLE);
        mainTable.add(price)
                .align(Align.center)
                .row();

        // description
        if (!itemData.getDescription().isBlank()) {
            Label description = new Label(itemData.getDescription(), UiData.DEFAULT_SKIN, UiData.SMALL_TEXT_STYLE);
            mainTable.add(description)
                    .fillX()
                    .align(Align.center)
                    .row();
            description.setWrap(true);
        }

        mainTable.pack();
        mainTable.background(UiData.DEFAULT_SKIN.getDrawable("Window_Transparent_9p"));
    }

    private String createCoefficientString(Enum<?> type,
                                           double value,
                                           String dealOrReceive,
                                           String withOrTo,
                                           String targets) {
        StringBuilder builder = new StringBuilder();
        String lessOrMore = value < 1 ? "less" : "more";
        double percents = value < 1 ? (1 - value) * 100 : (value - 1) * 100;
        if (value != 1) {
            builder.append(dealOrReceive)
                    .append(Functions.trimDouble(percents))
                    .append("% ")
                    .append(lessOrMore)
                    .append(" damage ")
                    .append(withOrTo)
                    .append(type.name().toLowerCase())
                    .append(targets)
                    .append("\r\n");
        }
        return builder.toString();
    }

    @Override
    public void update(Set<Element> current) {
        String elementsString = current.stream()
                .map(Element::name)
                .collect(Collectors.joining(" "));
        elements.setText(elementsString);
    }
}
