package ru.rdude.rpg.game.logic.data;

import ru.rdude.rpg.game.logic.data.resources.EventResources;
import ru.rdude.rpg.game.logic.enums.Biom;
import ru.rdude.rpg.game.logic.enums.Relief;
import ru.rdude.rpg.game.logic.stats.Stats;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class EventData extends EntityData {

    public enum EventActionTarget {NO, SELECTED_PLAYER, RANDOM_PLAYER, ALL_PLAYERS}

    public static Map<Long, EventData> events = new HashMap<>();

    public final EventAction END_EVENT = new EventAction();


    public Set<EventAction> actions = new HashSet<>();
    public Set<Biom> bioms = Arrays.stream(Biom.values()).collect(Collectors.toSet());
    public Set<Relief> reliefs = Arrays.stream(Relief.values()).collect(Collectors.toSet());
    public double minLvl = 0d;
    public double maxLvl = Double.POSITIVE_INFINITY;

    public EventData(long guid) {
        super(guid);
        setResources(new EventResources());
        events.put(guid, this);
    }

    public Set<EventAction> getActions() {
        return actions;
    }

    public void setActions(Set<EventAction> actions) {
        this.actions = actions;
    }

    public Set<Biom> getBioms() {
        return bioms;
    }

    public void setBioms(Set<Biom> bioms) {
        this.bioms = bioms;
    }

    public Set<Relief> getReliefs() {
        return reliefs;
    }

    public void setReliefs(Set<Relief> reliefs) {
        this.reliefs = reliefs;
    }

    public double getMinLvl() {
        return minLvl;
    }

    public void setMinLvl(double minLvl) {
        this.minLvl = minLvl;
    }

    public double getMaxLvl() {
        return maxLvl;
    }

    public void setMaxLvl(double maxLvl) {
        this.maxLvl = maxLvl;
    }

    @Override
    public boolean hasEntityDependency(long guid) {
        return actions.stream()
                .map(action -> Stream.of(
                        action.skillsCouldCast.keySet(),
                        action.skillsMustCast.keySet(),
                        action.summon.keySet(),
                        action.receiveItems.keySet())
                        .flatMap(Set::stream))
                .reduce(Stream::concat)
                .orElse(Stream.of())
                .anyMatch(g -> g.equals(guid));
    }

    @Override
    public void replaceEntityDependency(long oldValue, long newValue) {
        for (EventAction action : actions) {
            if (action.skillsCouldCast.containsKey(oldValue)) {
                action.skillsCouldCast.put(newValue, action.skillsCouldCast.get(oldValue));
                action.skillsCouldCast.remove(oldValue);
            }
            if (action.skillsMustCast.containsKey(oldValue)) {
                action.skillsMustCast.put(newValue, action.skillsMustCast.get(oldValue));
                action.skillsMustCast.remove(oldValue);
            }
            if (action.summon.containsKey(oldValue)) {
                action.summon.put(newValue, action.summon.get(oldValue));
                action.summon.remove(oldValue);
            }
            if (action.receiveItems.containsKey(oldValue)) {
                action.receiveItems.put(newValue, action.receiveItems.get(oldValue));
                action.receiveItems.remove(oldValue);
            }
        }
    }

    public class EventAction {

        private EventActionTarget target = EventActionTarget.NO;
        private Map<Long, Float> skillsCouldCast = new HashMap<>();
        private Map<Long, Float> skillsMustCast = new HashMap<>();
        private Set<Long> startQuest = new HashSet<>();
        private double timeChange = 0d;
        private Requirements requirements = new Requirements();
        private Map<Long, Float> summon; // by guid
        private Map<Long, Integer> receiveItems; // by guid

        public EventActionTarget getTarget() {
            return target;
        }

        public void setTarget(EventActionTarget target) {
            this.target = target;
        }

        public Map<Long, Float> getSkillsCouldCast() {
            return skillsCouldCast;
        }

        public void setSkillsCouldCast(Map<Long, Float> skillsCouldCast) {
            this.skillsCouldCast = skillsCouldCast;
        }

        public Map<Long, Float> getSkillsMustCast() {
            return skillsMustCast;
        }

        public void setSkillsMustCast(Map<Long, Float> skillsMustCast) {
            this.skillsMustCast = skillsMustCast;
        }

        public Set<Long> getStartQuest() {
            return startQuest;
        }

        public void setStartQuest(Set<Long> startQuest) {
            this.startQuest = startQuest;
        }

        public double getTimeChange() {
            return timeChange;
        }

        public void setTimeChange(double timeChange) {
            this.timeChange = timeChange;
        }

        public Requirements getRequirements() {
            return requirements;
        }

        public void setRequirements(Requirements requirements) {
            this.requirements = requirements;
        }

        public Map<Long, Float> getSummon() {
            return summon;
        }

        public void setSummon(Map<Long, Float> summon) {
            this.summon = summon;
        }

        public Map<Long, Integer> getReceiveItems() {
            return receiveItems;
        }

        public void setReceiveItems(Map<Long, Integer> receiveItems) {
            this.receiveItems = receiveItems;
        }

        public class Requirements {
            private Stats stats;
            private Map<Long, Integer> items; // by guid
            private boolean takeItems;

            public Requirements() {
                stats = new Stats(false);
                items = new HashMap<>();
            }

            public Stats getStats() {
                return stats;
            }

            public void setStats(Stats stats) {
                this.stats = stats;
            }

            public Map<Long, Integer> getItems() {
                return items;
            }

            public void setItems(Map<Long, Integer> items) {
                this.items = items;
            }

            public boolean isTakeItems() {
                return takeItems;
            }

            public void setTakeItems(boolean takeItems) {
                this.takeItems = takeItems;
            }

        }
    }
}
