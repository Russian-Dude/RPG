package ru.rdude.rpg.game.ui;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Tooltip;
import ru.rdude.rpg.game.logic.coefficients.Coefficients;
import ru.rdude.rpg.game.logic.data.ItemData;
import ru.rdude.rpg.game.logic.data.SkillData;
import ru.rdude.rpg.game.logic.entities.items.Item;
import ru.rdude.rpg.game.logic.enums.Element;

import java.util.concurrent.atomic.AtomicInteger;

public class ItemInfoTooltip extends Tooltip<Table> {

    private Table mainTable;
    private Label name;
    private Label type;
    private Label damage;
    private Label stats;
    private Label coefficients;
    private Label skillsOnUse;
    private Label skillsOnEquip;
    private Label requirements;
    private Label description;

    public ItemInfoTooltip(Item item) {
        super(new Table(UiData.DEFAULT_SKIN));
        ItemData itemData = item.getItemData();
        mainTable = getActor();
        mainTable.background(UiData.DEFAULT_SKIN.getDrawable("Window_Transparent_9p"));
        setInstant(true);

        // name
        name = new Label(itemData.getName(), UiData.DEFAULT_SKIN, UiData.BIG_TEXT_STYLE);
        mainTable.add(name);
        mainTable.row();

        // type
        String typeString = "";
        if (itemData.isWeapon()) {
            typeString += itemData.getWeaponData().isDualHanded() ? "TWO-HANDED " : "ONE-HANDED ";
        }
        typeString += itemData.getItemType().name();
        type = new Label(typeString, UiData.DEFAULT_SKIN, UiData.SMALL_TEXT_STYLE);
        mainTable.add(type);
        mainTable.row();

        // weapon damage
        if (itemData.isWeapon()) {
            StringBuilder damageBuilder = new StringBuilder();
            damageBuilder
                    .append((int) itemData.getWeaponData().getMinDmg())
                    .append(" - ")
                    .append((int) itemData.getWeaponData().getMaxDmg())
                    .append(" ");
            for (Element element : itemData.getElements()) {
                damageBuilder.append(element.name()).append(" ");
            }
            damageBuilder.append(itemData.getWeaponData().getAttackType().name())
                    .append(" ");
            damageBuilder.append("damage");
            damage = new Label(damageBuilder.toString(), UiData.DEFAULT_SKIN, UiData.SMALL_TEXT_STYLE);
            mainTable.add(damage);
            mainTable.row();
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
            if (!result.isEmpty()) {
                this.coefficients = new Label(result, UiData.DEFAULT_SKIN, UiData.SMALL_TEXT_STYLE);
                mainTable.add(this.coefficients);
                mainTable.row();
            }
        }

        // stats
        if (itemData.getStats() != null) {
            AtomicInteger count = new AtomicInteger();
            StringBuilder builder = new StringBuilder();
            itemData.getStats().forEachWithNestedStats(stat -> {
                if (stat.value() != 0) {
                    if (count.get() > 2) {
                        builder.append("\r\n");
                    }
                    builder.append(stat.getName())
                            .append(" ")
                            .append((int) stat.value())
                            .append(" ");
                    count.getAndIncrement();
                }
            });
            String result = builder.toString();
            if (!result.isEmpty()) {
                stats = new Label(result, UiData.DEFAULT_SKIN, UiData.SMALL_TEXT_STYLE);
                mainTable.add(stats);
                mainTable.row();
            }
        }

        // skills on use
        if (itemData.getSkillsOnUse() != null && !itemData.getSkillsOnUse().isEmpty()) {
            StringBuilder builder = new StringBuilder();
            itemData.getSkillsOnUse().forEach(skill -> builder.append(SkillData.getSkillByGuid(skill).getName())
                    .append(" "));
            if (!builder.toString().isEmpty()) {
                builder.append(" when used");
            }
            String s = builder.toString();
            if (!s.isEmpty()) {
                skillsOnUse = new Label(s, UiData.DEFAULT_SKIN, UiData.SMALL_TEXT_STYLE);
                mainTable.add(skillsOnUse);
                mainTable.row();
            }
        }

        // skills on equip
        if (itemData.getSkillsEquip() != null && !itemData.getSkillsOnUse().isEmpty()) {
            StringBuilder builder = new StringBuilder();
            builder.append("allows to use ");
            itemData.getSkillsEquip().forEach(skill -> builder.append(SkillData.getSkillByGuid(skill))
                    .append(" "));
            builder.append("when equipped");
            skillsOnEquip = new Label(builder.toString(), UiData.DEFAULT_SKIN, UiData.SMALL_TEXT_STYLE);
            mainTable.add(skillsOnEquip);
            mainTable.row();
        }

        // requirements
        if (itemData.getRequirements() != null) {
            StringBuilder builder = new StringBuilder();
            AtomicInteger count = new AtomicInteger();
            itemData.getRequirements().forEachWithNestedStats(stat -> {
                if (stat.value() > 0) {
                    if (count.get() > 2) {
                        builder.append("\r\n");
                    }
                    builder.append(stat.getName())
                            .append(" ")
                            .append((int) stat.value())
                            .append("   ");
                    count.getAndIncrement();
                }
            });
            String s = builder.toString();
            if (!s.isEmpty()) {
                mainTable.add(new Label("REQUIREMENTS", UiData.DEFAULT_SKIN, UiData.SMALL_TEXT_STYLE));
                requirements = new Label(s, UiData.DEFAULT_SKIN, UiData.SMALL_TEXT_STYLE);
                mainTable.add(requirements);
                mainTable.row();
            }
        }

        // description
        if (itemData.getDescription() != null && !itemData.getDescription().isEmpty()) {
            description = new Label(itemData.getDescription(), UiData.DEFAULT_SKIN, UiData.SMALL_TEXT_STYLE);
            mainTable.add(description);
        }

        mainTable.setSize(mainTable.getPrefWidth(), mainTable.getPrefHeight());
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
                    .append(percents)
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
}
