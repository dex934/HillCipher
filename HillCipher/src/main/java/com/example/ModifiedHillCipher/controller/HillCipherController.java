package com.example.ModifiedHillCipher.controller;

import com.example.ModifiedHillCipher.model.KeyMatrices;
import com.example.ModifiedHillCipher.service.HillCipherService;
import org.ejml.simple.SimpleMatrix;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class HillCipherController {

    private final HillCipherService hillCipherService;

    @Autowired
    public HillCipherController(HillCipherService hillCipherService) {
        this.hillCipherService = hillCipherService;
    }

    @GetMapping("/encryption")
    public String encryptionPage(Model model) {
        // This ensures that the form will be displayed properly even before any submission
        model.addAttribute("showResult", false);
        return "encryption";
    }

    @PostMapping("/encrypt")
    public String encrypt(@RequestParam("plaintext") String plaintext, Model model) {
        KeyMatrices keyMatrices = hillCipherService.generateKeyMatrices(plaintext.length());
        String encryptedText = hillCipherService.encrypt(plaintext, keyMatrices.getShiftedKeyMatrix());
        String keyMatrixString = getKeyMatrixAsString(keyMatrices.getShiftedKeyMatrix());

        model.addAttribute("encryptedText", encryptedText);
        model.addAttribute("keyMatrixString", keyMatrixString);
        model.addAttribute("showResult", true); // Indicate that results should be shown

        return "encryption";
    }

    @GetMapping("/decryption")
    public String decryptionPage(Model model) {
        model.addAttribute("showResult", false);
        return "decryption";
    }

    @PostMapping("/decrypt")
    public String decrypt(@RequestParam("encryptedText") String encryptedText, @RequestParam("keyMatrix") String keyMatrixString, Model model) {
        String decryptedText = hillCipherService.decrypt(encryptedText, keyMatrixString);

        model.addAttribute("decryptedText", decryptedText);
        model.addAttribute("showResult", true); // Indicate that results should be shown

        return "decryption";
    }

    private String getKeyMatrixAsString(SimpleMatrix keyMatrix) {
        StringBuilder keyMatrixString = new StringBuilder();
        for (int i = 0; i < keyMatrix.numRows(); i++) {
            for (int j = 0; j < keyMatrix.numCols(); j++) {
                keyMatrixString.append((char) ('A' + (int) keyMatrix.get(i, j) % 26));
            }
        }
        return keyMatrixString.toString();
    }

    private SimpleMatrix parseKeyMatrix(String keyMatrixString) {
        int size = (int) Math.sqrt(keyMatrixString.length());
        double[][] matrixData = new double[size][size];
        int index = 0;

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                matrixData[i][j] = keyMatrixString.charAt(index++) - 'A';
            }
        }

        return new SimpleMatrix(matrixData);
    }
}
