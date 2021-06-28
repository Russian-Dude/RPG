package ru.rdude.rpg.game.logic.coefficients;

import ru.rdude.rpg.game.logic.enums.AttackType;
import ru.rdude.rpg.game.logic.enums.BeingType;
import ru.rdude.rpg.game.logic.enums.Element;
import ru.rdude.rpg.game.logic.enums.Size;

public class Coefficients {

    private CoefficientsContainer atk;
    private CoefficientsContainer def;

    public Coefficients() {
        atk = new CoefficientsContainer();
        def = new CoefficientsContainer();
    }


    public CoefficientsContainer atk() { return atk; }
    public CoefficientsContainer def() { return def; }

    public void addSumOf(Coefficients... coefficients) {
        for (Coefficients c : coefficients) {
            this.atk.attackType.addSumOf(c.atk.attackType);
            this.def.attackType.addSumOf(c.def.attackType);
            this.atk.beingType.addSumOf(c.atk.beingType);
            this.def.beingType.addSumOf(c.def.beingType);
            this.atk.element.addSumOf(c.atk.element);
            this.def.element.addSumOf(c.def.element);
            this.atk.size.addSumOf(c.atk.size);
            this.def.size.addSumOf(c.def.size);
        }
    }
    
    public void removeSumOf(Coefficients... coefficients) {
        for (Coefficients c : coefficients) {
            this.atk.attackType.removeSumOf(c.atk.attackType);
            this.def.attackType.removeSumOf(c.def.attackType);
            this.atk.beingType.removeSumOf(c.atk.beingType);
            this.def.beingType.removeSumOf(c.def.beingType);
            this.atk.element.removeSumOf(c.atk.element);
            this.def.element.removeSumOf(c.def.element);
            this.atk.size.removeSumOf(c.atk.size);
            this.def.size.removeSumOf(c.def.size);
        }
    }

    public boolean isEmpty() {
        return
                atk.attackType.getCoefficientsMap().values().stream().noneMatch(d -> d == 0.0)
                && def.attackType.getCoefficientsMap().values().stream().noneMatch(d -> d == 0.0)
                && atk.beingType.getCoefficientsMap().values().stream().noneMatch(d -> d == 0.0)
                && def.beingType.getCoefficientsMap().values().stream().noneMatch(d -> d == 0.0)
                && atk.element.getCoefficientsMap().values().stream().noneMatch(d -> d == 0.0)
                && def.element.getCoefficientsMap().values().stream().noneMatch(d -> d == 0.0)
                && atk.size.getCoefficientsMap().values().stream().noneMatch(d -> d == 0.0)
                && def.size.getCoefficientsMap().values().stream().noneMatch(d -> d == 0.0);

    }


    public static Coefficients getSumOf(Coefficients... coefficients) {
        Coefficients result = new Coefficients();
        result.addSumOf(coefficients);
        return result;
    }


    public static class CoefficientsContainer {
        private Coefficient<AttackType> attackType;
        private Coefficient<BeingType> beingType;
        private Coefficient<Element> element;
        private Coefficient<Size> size;

        private CoefficientsContainer() {
            attackType = new Coefficient<>(AttackType.class);
            beingType = new Coefficient<>(BeingType.class);
            element = new Coefficient<>(Element.class);
            size = new Coefficient<>(Size.class);
        }

        public Coefficient<AttackType> attackType() { return attackType; }
        public Coefficient<BeingType> beingType() { return beingType; }
        public Coefficient<Element> element() { return element; }
        public Coefficient<Size> size() { return size; }
    }
}
