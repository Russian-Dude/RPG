package ru.rdude.rpg.game.logic.playerClass;

import ru.rdude.rpg.game.logic.data.AbilityData;
import ru.rdude.rpg.game.utils.Pair;

import java.util.*;

public class ClassAbilitiesDataTree {

    private final Map<Long, Node> nodes = new HashMap<>();

    public ClassAbilitiesDataTree(Set<AbilityData> abilities) {
        abilities.forEach(abilityData -> createNode(abilityData, abilities));
    }

    private void createNode(AbilityData abilityData, Set<AbilityData> abilityDataSet) {
        if (nodes.containsKey(abilityData.getGuid())) {
            return;
        }
        for (Long guid : abilityData.requirements.keySet()) {
            createNode(abilityDataSet.stream().filter(data -> data.getGuid() == guid).findAny().orElseThrow(), abilityDataSet);
        }
        nodes.put(abilityData.getGuid(), new Node(abilityData));
    }

    public Map<Long, Node> getNodes() {
        return nodes;
    }

    public void add(AbilityData abilityData) {
        nodes.put(abilityData.getGuid(), new Node(abilityData));
    }

    public void remove(AbilityData abilityData) {
        Node node = nodes.get(abilityData.getGuid());
        for (Node child : node.children) {
            if (child.requirements.size() > 1) {
                child.requirements.remove(node);
            }
            else {
                remove(child.abilityData);
            }
        }
        for (Node parent : node.requirements.keySet()) {
            parent.children.remove(node);
        }
        nodes.remove(node.abilityData.getGuid());
    }

    public class Node {

        final AbilityData abilityData;
        final Map<Node, Integer> requirements = new HashMap<>();
        final Set<Node> children = new HashSet<>();
        final int depth;

        public Node(AbilityData abilityData) {
            this.abilityData = abilityData;
            abilityData.requirements.forEach((guid, amount) -> requirements.put(nodes.get(guid), amount));
            requirements.keySet().forEach(node -> node.children.add(this));
            depth = findDepth(new Pair<>(this, 0)).getSecond();
        }

        private Pair<Node, Integer> findDepth(Pair<Node, Integer> current) {
            if (current.getFirst().requirements.isEmpty()) {
                return current;
            }
            return current.getFirst().requirements.keySet().stream()
                    .map(node -> findDepth(new Pair<>(node, current.getSecond())))
                    .max(Comparator.comparingInt(Pair::getSecond))
                    .orElseThrow();
        }

        public AbilityData getAbilityData() {
            return abilityData;
        }

        public Map<Node, Integer> getRequirements() {
            return requirements;
        }

        public Set<Node> getChildren() {
            return children;
        }

        public int getDepth() {
            return depth;
        }
    }
}
