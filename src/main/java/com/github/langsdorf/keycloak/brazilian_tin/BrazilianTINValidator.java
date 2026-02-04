package com.github.langsdorf.keycloak.brazilian_tin;

import org.keycloak.validate.ValidationContext;
import org.keycloak.validate.Validator;
import org.keycloak.validate.ValidatorConfig;
import org.keycloak.validate.ValidationError;

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

        Boolean isValid = ValidationUtils.isValid(value.toString());

        if (!isValid) {
            context.addError(new ValidationError(BrazilianTINValidatorFactory.ID, inputHint, "tin.invalid"));
        }

        return context;
    }

}