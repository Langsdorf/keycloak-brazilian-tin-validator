package com.github.langsdorf.keycloak.brazilian_tin;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;


public class BrazilianTINValidatorTest {
    @Test
    @DisplayName("Valid CPF and CNPJ")
    public void shouldAnswerWithTrue() {
        String validMaskedCPF = "326.725.020-05";
        String validMaskedCNPJ = "17.886.604/0001-80";
        String validUnmaskedCPF = "32672502005";
        String validUnmaskedCNPJ = "17886604000180";

        assertTrue(ValidationUtils.isValid(validMaskedCPF));
        assertTrue(ValidationUtils.isValid(validMaskedCNPJ));
        assertTrue(ValidationUtils.isValid(validUnmaskedCPF));
        assertTrue(ValidationUtils.isValid(validUnmaskedCNPJ));
    }

    @Test
    @DisplayName("Invalid CPF and CNPJ")
    public void shouldAnswerWithFalse() {
        String invalidCPF = "123.456.789-00";
        String invalidCNPJ = "12.345.678/0001-00";
        String invalidLength = "123456789";
        String nullValue = null;
        String emptyValue = "";

        assertTrue(!ValidationUtils.isValid(invalidCPF));
        assertTrue(!ValidationUtils.isValid(invalidCNPJ));
        assertTrue(!ValidationUtils.isValid(invalidLength));
        assertTrue(!ValidationUtils.isValid(nullValue));
        assertTrue(!ValidationUtils.isValid(emptyValue));
    }
}
