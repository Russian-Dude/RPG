package ru.rdude.rpg.game.logic.data.io;

import ru.rdude.rpg.game.logic.coefficients.Coefficients;
import ru.rdude.rpg.game.logic.enums.AttackType;
import ru.rdude.rpg.game.logic.enums.BeingType;
import ru.rdude.rpg.game.logic.enums.Element;
import ru.rdude.rpg.game.logic.enums.Size;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Map;

public class CoefficientsSerializable implements Externalizable {

    private Coefficients coefficients;

    public CoefficientsSerializable(Coefficients coefficients) {
        this.coefficients = coefficients;
    }

    public Coefficients getCoefficients() {
        return coefficients;
    }

    @Override
    public void writeExternal(ObjectOutput objectOutput) throws IOException {
        objectOutput.writeObject(coefficients.atk().attackType().getCoefficientsMap());
        objectOutput.writeObject(coefficients.atk().beingType().getCoefficientsMap());
        objectOutput.writeObject(coefficients.atk().element().getCoefficientsMap());
        objectOutput.writeObject(coefficients.atk().size().getCoefficientsMap());

        objectOutput.writeObject(coefficients.def().attackType().getCoefficientsMap());
        objectOutput.writeObject(coefficients.def().beingType().getCoefficientsMap());
        objectOutput.writeObject(coefficients.def().element().getCoefficientsMap());
        objectOutput.writeObject(coefficients.def().size().getCoefficientsMap());

    }

    @Override
    public void readExternal(ObjectInput objectInput) throws IOException, ClassNotFoundException {
        coefficients = new Coefficients();
        ((Map<AttackType, Double>) objectInput.readObject()).forEach((k, v) -> coefficients.atk().attackType().set(k, v));
        ((Map<BeingType, Double>) objectInput.readObject()).forEach((k, v) -> coefficients.atk().beingType().set(k, v));
        ((Map<Element, Double>) objectInput.readObject()).forEach((k, v) -> coefficients.atk().element().set(k, v));
        ((Map<Size, Double>) objectInput.readObject()).forEach((k, v) -> coefficients.atk().size().set(k, v));

        ((Map<AttackType, Double>) objectInput.readObject()).forEach((k, v) -> coefficients.def().attackType().set(k, v));
        ((Map<BeingType, Double>) objectInput.readObject()).forEach((k, v) -> coefficients.def().beingType().set(k, v));
        ((Map<Element, Double>) objectInput.readObject()).forEach((k, v) -> coefficients.def().element().set(k, v));
        ((Map<Size, Double>) objectInput.readObject()).forEach((k, v) -> coefficients.def().size().set(k, v));

    }
}
