import org.ejml.simple.SimpleMatrix;

import java.util.Random;
import java.util.Scanner;

public class HillCipher {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("Choose an option:");
            System.out.println("1. Encrypt");
            System.out.println("2. Decrypt");
            System.out.println("3. Exit");

            int choice = scanner.nextInt();

            switch (choice) {
                case 1:
                    encryptMessage(scanner);
                    break;
                case 2:
                    decryptMessage(scanner);
                    break;
                case 3:
                    System.out.println("Exiting...");
                    scanner.close();
                    return;
                default:
                    System.out.println("Invalid choice. Please choose again.");
            }
        }
    }

    private static void encryptMessage(Scanner scanner) {
        System.out.print("Enter the plaintext (in uppercase): ");
        String plaintext = scanner.next();

        KeyMatrices keyMatrices = generateKeyMatrices(plaintext.length());
        System.out.println("Key Matrix:");
        printKeyMatrixAsString(keyMatrices.originalKeyMatrix);

        System.out.println("Shifted Key Matrix:");
        printKeyMatrixAsString(keyMatrices.shiftedKeyMatrix);

        String encryptedText = encrypt(plaintext, keyMatrices.shiftedKeyMatrix);
        System.out.println("Encrypted Text: " + encryptedText);
    }

    private static void decryptMessage(Scanner scanner) {
        System.out.print("Enter the encrypted text (in uppercase): ");
        String encryptedText = scanner.next();

        System.out.print("Enter the shifted key matrix (9 characters in uppercase): ");
        String keyMatrixString = scanner.next();

        SimpleMatrix shiftedKeyMatrix = parseKeyMatrix(keyMatrixString);

        String decryptedText = decrypt(encryptedText, shiftedKeyMatrix);
        System.out.println("Decrypted Text: " + decryptedText);
    }

    private static class KeyMatrices {
        SimpleMatrix originalKeyMatrix;
        SimpleMatrix shiftedKeyMatrix;

        public KeyMatrices(SimpleMatrix originalKeyMatrix, SimpleMatrix shiftedKeyMatrix) {
            this.originalKeyMatrix = originalKeyMatrix;
            this.shiftedKeyMatrix = shiftedKeyMatrix;
        }
    }

    private static KeyMatrices generateKeyMatrices(int size) {
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

    private static SimpleMatrix shiftRows(SimpleMatrix matrix) {
        SimpleMatrix shiftedMatrix = matrix.copy();

        for (int i = 0; i < matrix.numRows(); i++) {
            for (int j = 0; j < matrix.numCols(); j++) {
                shiftedMatrix.set(i, j, matrix.get(i, (j + i) % matrix.numCols()));
            }
        }

        return shiftedMatrix;
    }

    private static String encrypt(String plaintext, SimpleMatrix shiftedKeyMatrix) {
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

    private static String decrypt(String ciphertext, SimpleMatrix keyMatrix) {
        StringBuilder decryptedText = new StringBuilder();

        int keySize = keyMatrix.numRows();

        if (ciphertext.length() % keySize != 0) {
            System.out.println("Invalid ciphertext length.");
            return "";
        }

        SimpleMatrix keyInverse = matrixModInverse(keyMatrix, 26);

        int blockSize = keySize;

        for (int i = 0; i < ciphertext.length(); i += blockSize) {
            double[][] block = new double[keySize][1];
            for (int j = 0; j < keySize; j++) {
                block[j][0] = ciphertext.charAt(i + j) - 'A';
            }

            SimpleMatrix resultMatrix = keyInverse.mult(new SimpleMatrix(block));

            for (int j = 0; j < keySize; j++) {
                int charValue = (int) Math.floorMod(Math.round(resultMatrix.get(j, 0)), 26);
                decryptedText.append((char) (charValue + 'A'));
            }
        }

        return decryptedText.toString();
    }

    private static SimpleMatrix parseKeyMatrix(String keyMatrixString) {
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
    private static int modInverse(int a, int m) {
        a = a % m;
        for (int x = 1; x < m; x++) {
            if ((a * x) % m == 1) {
                return x;
            }
        }
        return -1; // Return -1 if the modular inverse doesn't exist
    }

    private static SimpleMatrix matrixModInverse(SimpleMatrix matrix, int m) {
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

    private static SimpleMatrix adjugate(SimpleMatrix matrix) {
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



    private static void printKeyMatrixAsString(SimpleMatrix keyMatrix) {
        StringBuilder keyString = new StringBuilder();
        for (int i = 0; i < keyMatrix.numRows(); i++) {
            for (int j = 0; j < keyMatrix.numCols(); j++) {
                keyString.append((char) ('A' + (int) keyMatrix.get(i, j) % 26));
            }
        }
        System.out.println(keyString.toString());
    }
}
