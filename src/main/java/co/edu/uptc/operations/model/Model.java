package co.edu.uptc.operations.model;

import co.edu.uptc.operations.exceptions.ByZeroException;
import co.edu.uptc.operations.exceptions.InvalidOptionException;

public class Model {
    private int option;
    private float num1;
    private float num2;

    public Model(int option, float num1, float num2) {
        this.option = option;
        this.num1 = num1;
        this.num2 = num2;
    }

    public float executeCase() {
        switch (option) {
            case 1:
                return (float) (num1 + num2);
            case 2:
                return (float) (num1 - num2);
            case 3:
                return (float) (num1 * num2);
            case 4:
                if (num2 != 0) {
                    return (float) (num1 / num2);
                } else {
                    throw new ByZeroException(num1 + " / " + num2);
                }
            default:
                throw new InvalidOptionException("" + option);
        }
    }
}

