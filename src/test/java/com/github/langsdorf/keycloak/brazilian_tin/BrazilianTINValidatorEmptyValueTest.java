package com.github.langsdorf.keycloak.brazilian_tin;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.keycloak.validate.ValidationContext;
import org.keycloak.validate.ValidatorConfig;

/**
 * O validador recebe o atributo como o Keycloak entrega: uma lista. Atributo ausente chega como
 * lista vazia e não pode virar `tin.invalid` — a obrigatoriedade é do `required` do perfil.
 */
public class BrazilianTINValidatorEmptyValueTest {

    private static final ValidatorConfig CONFIG = new ValidatorConfig(
            Map.of("enableBypass", "true", "bypassString", "ESTRANGEIRO"));

    private static ValidationContext validate(Object value, ValidatorConfig config) {
        return new BrazilianTINValidator().validate(value, "taxIdentification", new ValidationContext(), config);
    }

    @Test
    @DisplayName("Missing document (empty list) is not an error")
    public void emptyListIsNotAnError() {
        assertTrue(validate(new ArrayList<String>(), CONFIG).isValid());
    }

    @Test
    @DisplayName("Null and blank values are not errors")
    public void nullAndBlankAreNotErrors() {
        assertTrue(validate(null, CONFIG).isValid());
        assertTrue(validate(List.of(" "), CONFIG).isValid());
    }

    @Test
    @DisplayName("A valid CPF and the foreign marker still pass")
    public void validValuesPass() {
        assertTrue(validate(List.of("32672502005"), CONFIG).isValid());
        assertTrue(validate(List.of("ESTRANGEIRO"), CONFIG).isValid());
    }

    @Test
    @DisplayName("The foreign marker is rejected when the bypass is off")
    public void foreignMarkerNeedsBypass() {
        assertEquals(1, validate(List.of("ESTRANGEIRO"), new ValidatorConfig(Map.of())).getErrors().size());
    }

    @Test
    @DisplayName("An invalid document is still rejected")
    public void invalidDocumentIsRejected() {
        ValidationContext context = validate(List.of("12345678900"), CONFIG);

        assertEquals(1, context.getErrors().size());
        assertEquals("tin.invalid", context.getErrors().iterator().next().getMessage());
    }
}
