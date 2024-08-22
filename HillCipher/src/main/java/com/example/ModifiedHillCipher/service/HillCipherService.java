package com.example.ModifiedHillCipher.service;

import com.example.ModifiedHillCipher.model.KeyMatrices;
import org.ejml.simple.SimpleMatrix;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class HillCipherService {

    public KeyMatrices generateKeyMatrices(int size) {
        Random random = new Random();
        SimpleMatrix keyMatrix;
        SimpleMatrix shiftedKeyMatrix;

        do {
            keyMatrix = new SimpleMatrix(size, size);

            for (int i = 0; i < size; i++) {
                for (int j = 0; j < size; j++) {
                    keyMatrix.set(i, j, random.nextInt(26)); // Random value between 0 and 25
                }
            }

            shiftedKeyMatrix = shiftRows(keyMatrix);

        } while (shiftedKeyMatrix.determinant() == 0 || modInverse((int) shiftedKeyMatrix.determinant(), 26) == -1);

        return new KeyMatrices(keyMatrix, shiftedKeyMatrix);
    }

    private SimpleMatrix shiftRows(SimpleMatrix matrix) {
        SimpleMatrix shiftedMatrix = matrix.copy();

        for (int i = 0; i < matrix.numRows(); i++) {
            for (int j = 0; j < matrix.numCols(); j++) {
                shiftedMatrix.set(i, j, matrix.get(i, (j + i) % matrix.numCols()));
            }
        }

        return shiftedMatrix;
    }

    public String encrypt(String plaintext, SimpleMatrix shiftedKeyMatrix) {
        plaintext = plaintext.toUpperCase();
        StringBuilder encryptedText = new StringBuilder();

        StringBuilder plaintextBuilder = new StringBuilder(plaintext);
        while (plaintextBuilder.length() % shiftedKeyMatrix.numRows() != 0) {
            plaintextBuilder.append('X'); // Pad with 'X' if necessary
        }
        plaintext = plaintextBuilder.toString();

        for (int i = 0; i < plaintext.length(); i += shiftedKeyMatrix.numRows()) {
            double[][] block = new double[shiftedKeyMatrix.numRows()][1];
            for (int j = 0; j < shiftedKeyMatrix.numRows(); j++) {
                block[j][0] = plaintext.charAt(i + j) - 'A';
            }

            SimpleMatrix resultMatrix = shiftedKeyMatrix.mult(new SimpleMatrix(block));

            for (int j = 0; j < shiftedKeyMatrix.numRows(); j++) {
                encryptedText.append((char) ((resultMatrix.get(j, 0) % 26) + 'A'));
            }
        }

        return encryptedText.toString();
    }

    public String decrypt(String ciphertext, String keyMatrixString) {
        SimpleMatrix shiftedKeyMatrix = parseKeyMatrix(keyMatrixString);

        StringBuilder decryptedText = new StringBuilder();

        int keySize = shiftedKeyMatrix.numRows();

        // Shift rows in the original key matrix
        SimpleMatrix keyInverse = matrixModInverse(shiftedKeyMatrix, 26);

        int blockSize = keySize;

        for (int i = 0; i < ciphertext.length(); i += blockSize) {
            double[][] block = new double[keySize][1];

            // Ensure the current block does not exceed the length of the ciphertext
            int endIndex = Math.min(i + blockSize, ciphertext.length());

            for (int j = 0; j < endIndex - i; j++) {
                block[j][0] = ciphertext.charAt(i + j) - 'A';
            }

            SimpleMatrix resultMatrix = keyInverse.mult(new SimpleMatrix(block));

            for (int j = 0; j < endIndex - i; j++) {
                int charValue = (int) Math.floorMod(Math.round(resultMatrix.get(j, 0)), 26);
                decryptedText.append((char) (charValue + 'A'));
            }
        }

        return decryptedText.toString();
    }



    private SimpleMatrix parseKeyMatrix(String keyMatrixString) {
        int keySize = (int) Math.sqrt(keyMatrixString.length());
        double[][] matrixData = new double[keySize][keySize];
        int index = 0;

        for (int i = 0; i < keySize; i++) {
            for (int j = 0; j < keySize; j++) {
                matrixData[i][j] = keyMatrixString.charAt(index++) - 'A';
            }
        }

        return new SimpleMatrix(matrixData);
    }

    private int modInverse(int a, int m) {
        a = a % m;
        for (int x = 1; x < m; x++) {
            if ((a * x) % m == 1) {
                return x;
            }
        }
        return -1; // Return -1 if the modular inverse doesn't exist
    }

    private SimpleMatrix matrixModInverse(SimpleMatrix matrix, int m) {
        int det = (int) Math.round(matrix.determinant());
        int detInv = modInverse(det, m);
        SimpleMatrix adj = adjugate(matrix);

        for (int i = 0; i < matrix.numRows(); i++) {
            for (int j = 0; j < matrix.numCols(); j++) {
                double val = (detInv * adj.get(i, j)) % m;
                if (val < 0) val += m;
                matrix.set(i, j, val);
            }
        }

        return matrix;
    }

    private SimpleMatrix adjugate(SimpleMatrix matrix) {
        SimpleMatrix adjugate = new SimpleMatrix(matrix.numRows(), matrix.numCols());

        for (int i = 0; i < matrix.numRows(); i++) {
            for (int j = 0; j < matrix.numCols(); j++) {
                SimpleMatrix subMatrix = new SimpleMatrix(matrix.numRows() - 1, matrix.numCols() - 1);

                int row = 0;
                for (int k = 0; k < matrix.numRows(); k++) {
                    if (k == i) continue;
                    int col = 0;
                    for (int l = 0; l < matrix.numCols(); l++) {
                        if (l == j) continue;
                        subMatrix.set(row, col, matrix.get(k, l));
                        col++;
                    }
                    row++;
                }

                adjugate.set(i, j, Math.pow(-1, i + j) * subMatrix.determinant());
            }
        }

        return adjugate.transpose();
    }
}
