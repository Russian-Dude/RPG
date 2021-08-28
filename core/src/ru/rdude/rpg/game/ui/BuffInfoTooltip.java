package ru.rdude.rpg.game.ui;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Tooltip;
import com.badlogic.gdx.utils.Align;
import com.fasterxml.jackson.annotation.JsonIgnoreType;
import ru.rdude.rpg.game.logic.coefficients.Coefficients;
import ru.rdude.rpg.game.logic.data.ItemData;
import ru.rdude.rpg.game.logic.data.MonsterData;
import ru.rdude.rpg.game.logic.data.SkillData;
import ru.rdude.rpg.game.logic.entities.skills.Buff;
import ru.rdude.rpg.game.logic.entities.skills.BuffObserver;
import ru.rdude.rpg.game.logic.entities.skills.SkillDuration;
import ru.rdude.rpg.game.logic.enums.SkillType;
import ru.rdude.rpg.game.logic.time.Duration;
import ru.rdude.rpg.game.logic.time.DurationObserver;
import ru.rdude.rpg.game.utils.Functions;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@JsonIgnoreType
public class BuffInfoTooltip extends Tooltip<Table> implements DurationObserver, BuffObserver {

    private Label timeLeft;
    private Label forcedCancelAfter;

    public BuffInfoTooltip(Buff buff) {
        super(new Table());
        setInstant(true);
        buff.subscribe(this);
        buff.getDuration().subscribe(this);
        SkillData skillData = buff.getEntityData();
        Table mainTable = getActor();
        mainTable.columnDefaults(0).space(10f);

        // name and type
        Label nameLabel = new Label(skillData.getName() + stringType(skillData.getType()), UiData.DEFAULT_SKIN, UiData.BIG_TEXT_STYLE);
        nameLabel.setAlignment(Align.center);
        mainTable.add(nameLabel)
                .align(Align.center)
                .row();

        // time left
        if (skillData.hasDuration()) {
            timeLeft = new Label(getTimeLeftString(buff.getDuration()), UiData.DEFAULT_SKIN, UiData.BIG_TEXT_STYLE);
            mainTable.add(timeLeft)
                    .align(Align.center)
                    .row();
        }

        // forced cancel
        if (skillData.canBeForceCanceled()) {
            forcedCancelAfter = new Label(getForceCancelString(buff.getDuration()), UiData.DEFAULT_SKIN, UiData.BIG_TEXT_STYLE);
            mainTable.add(forcedCancelAfter)
                    .align(Align.center)
                    .row();
        }

        // periodic
        final double actsMinute = skillData.getActsEveryMinute();
        final double actsTurn = skillData.getActsEveryTurn();
        if (actsMinute > 0 || actsTurn > 0) {
            String minutes = actsMinute > 0 ? Functions.trimDouble(actsMinute) + " minutes" : "";
            String turns = actsTurn > 0 ? Functions.trimDouble(actsTurn) + " turns" : "";
            String actEvery = "Every " + Stream.of(minutes, turns)
                    .filter(s -> !s.isEmpty())
                    .collect(Collectors.joining(", "));
            Label actsEveryLabel = new Label(actEvery, UiData.DEFAULT_SKIN, UiData.SMALL_TEXT_STYLE);
            mainTable.add(actsEveryLabel)
                    .align(Align.center)
                    .row();
        }

        // damage
        if (buff.getDamage().isPresent()) {
            String stringDamage;
            final double damageValue = buff.getDamage().get().value();
            if (damageValue > 0) {
                stringDamage = "Receive " + Functions.trimDouble(damageValue) + " damage";
            } else {
                stringDamage = "Heal for " + Functions.trimDouble(damageValue * (-1));
            }
            Label damageLabel = new Label(stringDamage, UiData.DEFAULT_SKIN, UiData.SMALL_TEXT_STYLE);
            mainTable.add(damageLabel)
                    .align(Align.center)
                    .row();
        }

        // transformation
        final SkillData.Transformation transformation = skillData.getTransformation();
        Stream<? extends Set<? extends Enum<? extends Enum<?>>>> transformationStream
                = Stream.of(transformation.getBeingTypes(), transformation.getElements());
        if (transformation.getSize() != null) {
            transformationStream = Stream.concat(transformationStream, Stream.of(Set.of(transformation.getSize())));
        }
        String transformationString = transformationStream
                .flatMap(Collection::stream)
                .map(Enum::name)
                .collect(Collectors.joining(", "));
        if (!transformationString.isEmpty()) {
            Label transformationLabel = new Label("Transform into " + transformationString, UiData.DEFAULT_SKIN, UiData.SMALL_TEXT_STYLE);
            mainTable.add(transformationLabel)
                    .align(Align.center)
                    .row();
        }

        // stats
        if (buff.getStats().isPresent()) {
            Table statsTable = new Table();
            buff.getStats().get().streamWithNestedStats()
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
        }

        // coefficients
        if (skillData.getBuffCoefficients() != null) {
            Coefficients coefficients = skillData.getBuffCoefficients();
            Set<Label> labels = new HashSet<>();
            coefficients.atk().attackType().getCoefficientsMap().forEach((type, value) -> {
                String s = createCoefficientString(type, value, "deal ", "with ", " attacks");
                if (!s.isBlank()) {
                    labels.add(new Label(s, UiData.DEFAULT_SKIN, UiData.SMALL_TEXT_STYLE));
                }
            });
            coefficients.atk().beingType().getCoefficientsMap().forEach((type, value) -> {
                String s = createCoefficientString(type, value, "deal ", "to ", " enemies");
                if (!s.isBlank()) {
                    labels.add(new Label(s, UiData.DEFAULT_SKIN, UiData.SMALL_TEXT_STYLE));
                }

            });
            coefficients.atk().element().getCoefficientsMap().forEach((type, value) -> {
                String s = createCoefficientString(type, value, "deal ", "to ", " enemies");
                if (!s.isBlank()) {
                    labels.add(new Label(s, UiData.DEFAULT_SKIN, UiData.SMALL_TEXT_STYLE));
                }

            });
            coefficients.atk().size().getCoefficientsMap().forEach((type, value) -> {
                String s = createCoefficientString(type, value, "deal ", "to ", " enemies");
                if (!s.isBlank()) {
                    labels.add(new Label(s, UiData.DEFAULT_SKIN, UiData.SMALL_TEXT_STYLE));
                }

            });

            coefficients.def().attackType().getCoefficientsMap().forEach((type, value) -> {
                String s = createCoefficientString(type, value, "receive ", "from ", " attacks");
                if (!s.isBlank()) {
                    labels.add(new Label(s, UiData.DEFAULT_SKIN, UiData.SMALL_TEXT_STYLE));
                }

            });
            coefficients.def().beingType().getCoefficientsMap().forEach((type, value) -> {
                String s = createCoefficientString(type, value, "receive ", "from ", " enemies");
                if (!s.isBlank()) {
                    labels.add(new Label(s, UiData.DEFAULT_SKIN, UiData.SMALL_TEXT_STYLE));
                }

            });
            coefficients.def().element().getCoefficientsMap().forEach((type, value) -> {
                String s = createCoefficientString(type, value, "receive ", "from ", " attacks");
                if (!s.isBlank()) {
                    labels.add(new Label(s, UiData.DEFAULT_SKIN, UiData.SMALL_TEXT_STYLE));
                }

            });
            coefficients.def().size().getCoefficientsMap().forEach((type, value) -> {
                String s = createCoefficientString(type, value, "receive ", "from ", " enemies");
                if (!s.isBlank()) {
                    labels.add(new Label(s, UiData.DEFAULT_SKIN, UiData.SMALL_TEXT_STYLE));
                }

            });
            labels.forEach(label -> {
                mainTable.add(label)
                        .align(Align.center);
                mainTable.row();
            });
        }

        // summon
        if (skillData.getSummon() != null && !skillData.getSummon().isEmpty()) {
            String summonString = "Summon " + skillData.getSummon().stream()
                    .collect(Collectors.collectingAndThen(Collectors.toMap(s -> MonsterData.getMonsterByGuid(s.getGuid()).getName(), SkillData.Summon::getChance), Functions::normalizePercentsMap))
                    .entrySet().stream()
                    .map(entry -> {
                        String name = entry.getKey();
                        String percent = " (" + Functions.trimDouble(entry.getValue()) + "%)";
                        return name + percent;
                    })
                    .collect(Collectors.joining(", "));
            Label summonLabel = new Label(summonString, UiData.DEFAULT_SKIN, UiData.SMALL_TEXT_STYLE);
            mainTable.add(summonLabel)
                    .align(Align.center)
                    .row();
        }

        // receive items
        if (!skillData.getReceiveItems().isEmpty()) {
            String receiveString = "Receive " + skillData.getReceiveItems().entrySet().stream()
                    .map(entry -> {
                        String name = ItemData.getItemDataByGuid(entry.getKey()).getName();
                        String amount = " (" + entry.getValue() + ")";
                        return name + amount;
                    })
                    .collect(Collectors.joining(", "));
            Label receiveLabel = new Label(receiveString, UiData.DEFAULT_SKIN, UiData.SMALL_TEXT_STYLE);
            mainTable.add(receiveLabel)
                    .align(Align.center)
                    .row();
        }

        // skills could cast
        if (!skillData.getSkillsCouldCast().isEmpty()) {
            String couldCast = "Chance to cast " + skillData.getSkillsCouldCast().entrySet().stream()
                    .map(entry -> {
                        String name = SkillData.getSkillByGuid(entry.getKey()).getName();
                        String percent = " (" + Functions.trimDouble(entry.getValue()) + "%)";
                        return name + percent;
                    })
                    .collect(Collectors.joining(", "));
            Label couldCastLabel = new Label(couldCast, UiData.DEFAULT_SKIN, UiData.SMALL_TEXT_STYLE);
            mainTable.add(couldCastLabel)
                    .align(Align.center)
                    .row();
        }

        // skills must cast
        if (!skillData.getSkillsMustCast().isEmpty()) {
            String mustCast;
            if (skillData.getSkillsMustCast().size() == 1) {
                mustCast = "Cast " + SkillData.getSkillByGuid(skillData.getSkillsMustCast().keySet().stream().findAny().get()).getName();
            }
            else {
                mustCast = "Cast one of: " + Functions.normalizePercentsMap(skillData.getSkillsMustCast()).entrySet().stream()
                        .map(entry -> {
                            String name = SkillData.getSkillByGuid(entry.getKey()).getName();
                            String percent = " (" + Functions.trimDouble(entry.getValue()) + "%)";
                            return name + percent;
                        })
                        .collect(Collectors.joining(", "));
            }
            Label mustCastLabel = new Label(mustCast, UiData.DEFAULT_SKIN, UiData.SMALL_TEXT_STYLE);
            mainTable.add(mustCastLabel)
                    .align(Align.center)
                    .row();
        }

        // skills on being action
        if (skillData.getSkillsOnBeingAction() != null && !skillData.getSkillsOnBeingAction().isEmpty()) {
            Set<Label> set = new HashSet<>();
            skillData.getSkillsOnBeingAction().forEach((action, map) -> {
                String actionString = "After " + action.prettyStringAfter + ", cast: ";
                String skills = map.entrySet().stream()
                        .map(entry -> {
                            String name = SkillData.getSkillByGuid(entry.getKey()).getName();
                            String percent = " (" + Functions.trimDouble(entry.getValue()) + "%)";
                            return name + percent;
                        })
                        .collect(Collectors.joining(", "));
                set.add(new Label(actionString + skills, UiData.DEFAULT_SKIN, UiData.SMALL_TEXT_STYLE));
            });
            set.forEach(label -> mainTable.add(label).align(Align.center).row());
        }

        mainTable.pack();
        mainTable.background(UiData.DEFAULT_SKIN.getDrawable("Window_Transparent_9p"));
    }


    private String getTimeLeftString(Duration duration) {
        String turns = duration.getTurnsLeft()
                .map(d -> (d % 2 == 0 ? d : d - 1) / 2)
                .map(d -> Functions.trimDouble(d) + " turns")
                .orElse("");
        String minutes = duration.getMinutesLeft()
                .map(d -> Functions.trimDouble(d) + " minutes")
                .orElse("");
        return Stream.of(turns, minutes)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.joining(", ")) + " left";
    }

    private String getForceCancelString(SkillDuration skillDuration) {
        String damageMade = skillDuration.getDamageMadeLeft()
                .map(d -> Functions.trimDouble(d) + " damage made")
                .orElse("");
        String damageReceived = skillDuration.getDamageReceivedLeft()
                .map(d -> Functions.trimDouble(d) + " damage received")
                .orElse("");
        String hitsMade = skillDuration.getHitsMadeLeft()
                .map(d -> Functions.trimDouble(d) + " hits made")
                .orElse("");
        String hitsReceived = skillDuration.getHitsReceivedLeft()
                .map(d -> Functions.trimDouble(d) + " hits received")
                .orElse("");
        return "Ends after " + Stream.of(damageMade, damageReceived, hitsMade, hitsReceived)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.joining(" or "));
    }

    private String stringType(SkillType type) {
        return type == SkillType.NO_TYPE ? "" : " (" + type.name() + ")";
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
                    .append(targets);
        }
        return builder.toString();
    }

    @Override
    public void update(Duration duration, boolean ends) {
        if (timeLeft != null) {
            timeLeft.setText(getTimeLeftString(duration));
        }
        if (forcedCancelAfter != null) {
            forcedCancelAfter.setText(getForceCancelString((SkillDuration) duration));
        }
    }

    @Override
    public void update(Buff buff, boolean ends) {
        if (ends) {
            buff.getDuration().unsubscribe(this);
            buff.unsubscribe(this);
        }
    }
}
