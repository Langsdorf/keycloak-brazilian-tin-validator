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
    @DisplayName("Valid CNPJ serialized as a Keycloak list (wrapped in brackets)")
    public void shouldAnswerWithTrueForBracketWrappedCNPJ() {
        // No Keycloak o valor chega como lista; value.toString() vem como "[...]"
        String numericAsList = "[53314754000182]";
        String alphanumericAsList = "[ENKBZ0VL000109]";

        assertTrue(ValidationUtils.isValid(numericAsList));
        assertTrue(ValidationUtils.isValid(alphanumericAsList));
    }

    @Test
    @DisplayName("Valid alphanumeric CNPJ (SEFAZ/Receita format)")
    public void shouldAnswerWithTrueForAlphanumericCNPJ() {
        String validMasked = "12.ABC.345/01DE-35";
        String validUnmasked = "12ABC34501DE35";
        String validLowercase = "12abc34501de35";

        assertTrue(ValidationUtils.isValid(validMasked));
        assertTrue(ValidationUtils.isValid(validUnmasked));
        assertTrue(ValidationUtils.isValid(validLowercase));
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

    @Test
    @DisplayName("Invalid alphanumeric CNPJ")
    public void shouldAnswerWithFalseForAlphanumericCNPJ() {
        String wrongCheckDigits = "12ABC34501DE99"; // DV correto seria 35
        String nonNumericCheckDigits = "12ABC34501DEAB"; // DV deve ser numérico
        String lettersInCheckDigits = "12ABC34501DE3X";

        assertTrue(!ValidationUtils.isValid(wrongCheckDigits));
        assertTrue(!ValidationUtils.isValid(nonNumericCheckDigits));
        assertTrue(!ValidationUtils.isValid(lettersInCheckDigits));
    }
}
