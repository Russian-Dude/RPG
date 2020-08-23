package ru.rdude.rpg.game.logic.coefficients;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Coefficient<T extends Enum<T>> {

    private Map<T, Double> coefficients;
    private Class<T> type;

    public Coefficient(Class<T> cl) {
        coefficients = new HashMap<>();
        type = cl;
        if (cl == null || !cl.isEnum()) throw new IllegalArgumentException();
        for (T value : cl.getEnumConstants()) {
            coefficients.put(value, 1d);
        }
    }

    public double getValue(T t) {
        return coefficients.get(t);
    }

    public double getValue(Set<T> set) {
        return set.stream()
                .map(coefficients::get)
                .reduce(1d, (before, after) -> (before - 1) + (after - 1) + 1);
    }

    public Map<T, Double> getCoefficientsMap() { return coefficients; }

    public Class<T> getType() {
        return type;
    }

    // return last value
    public double set(T t, double value) {
        return coefficients.put(t, value);
    }

    public void addSumOf(Coefficient<T>... coefficientsInput) {
        for (Coefficient<T> coefficient : coefficientsInput) {
            coefficient.coefficients.forEach((k, v) -> {
                double newValue = ((this.coefficients.get(k) - 1) + (v - 1)) + 1;
                this.coefficients.put(k, newValue);
            });
        }
    }

    public void removeSumOf(Coefficient<T>... coefficientsInput) {
        for (Coefficient<T> coefficient : coefficientsInput) {
            coefficient.coefficients.forEach((k, v) -> {
                double newValue = ((this.coefficients.get(k) - 1) - (v - 1)) + 1;
                this.coefficients.put(k, newValue);
            });
        }
    }
}
