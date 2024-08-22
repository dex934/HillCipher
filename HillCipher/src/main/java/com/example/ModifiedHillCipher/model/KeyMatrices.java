package com.example.ModifiedHillCipher.model;
import org.ejml.simple.SimpleMatrix;

public class KeyMatrices {
    private final SimpleMatrix originalKeyMatrix;
    private final SimpleMatrix shiftedKeyMatrix;

    public KeyMatrices(SimpleMatrix originalKeyMatrix, SimpleMatrix shiftedKeyMatrix) {
        this.originalKeyMatrix = originalKeyMatrix;
        this.shiftedKeyMatrix = shiftedKeyMatrix;
    }

    public SimpleMatrix getOriginalKeyMatrix() {
        return originalKeyMatrix;
    }

    public SimpleMatrix getShiftedKeyMatrix() {
        return shiftedKeyMatrix;
    }
}
