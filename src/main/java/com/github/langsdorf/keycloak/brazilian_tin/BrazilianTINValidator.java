package com.github.langsdorf.keycloak.brazilian_tin;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.keycloak.userprofile.AttributeContext;
import org.keycloak.userprofile.UserProfileAttributeValidationContext;
import org.keycloak.validate.ValidationContext;
import org.keycloak.validate.ValidationError;
import org.keycloak.validate.Validator;
import org.keycloak.validate.ValidatorConfig;

public class BrazilianTINValidator implements Validator {

    @Override
    public ValidationContext validate(Object value, String inputHint, ValidationContext context,
            ValidatorConfig config) {
        Boolean enableBypass = config.getBoolean("enableBypass");
        String bypassString = "[" + config.getString("bypassString") + "]"; // O valor é retornado como uma lista

        if (value == null) {
            context.addError(new ValidationError(BrazilianTINValidatorFactory.ID, inputHint, "tin.required"));
            return context;
        }

        if (enableBypass != null && enableBypass && bypassString != null && bypassString.equals(value.toString())) {
            return context;
        }

        // O CNPJ alfanumérico (Receita Federal) é definido em maiúsculo. Como um
        // Validator não pode transformar o valor diretamente, normalizamos o
        // atributo que será persistido para maiúsculo. CPF (numérico) não é afetado.
        normalizeToUpperCase(context);

        Boolean isValid = ValidationUtils.isValid(value.toString());

        if (!isValid) {
            context.addError(new ValidationError(BrazilianTINValidatorFactory.ID, inputHint, "tin.invalid"));
        }

        return context;
    }

    /**
     * Reescreve, em maiúsculo, o valor do atributo em validação para que seja
     * gravado no formato exigido pela Receita Federal. Só atua quando a validação
     * ocorre dentro do framework de User Profile do Keycloak; em outros contextos
     * (ex.: testes unitários diretos) a chamada é ignorada com segurança.
     */
    private void normalizeToUpperCase(ValidationContext context) {
        try {
            AttributeContext attributeContext = UserProfileAttributeValidationContext.from(context)
                    .getAttributeContext();
            Map.Entry<String, List<String>> attribute = attributeContext.getAttribute();
            List<String> values = attribute.getValue();
            if (values != null) {
                List<String> upper = values.stream()
                        .map(v -> v == null ? null : v.toUpperCase())
                        .collect(Collectors.toList());
                attribute.setValue(upper);
            }
        } catch (Exception ignored) {
            // Contexto não pertence ao User Profile; nada a normalizar.
        }
    }

}
