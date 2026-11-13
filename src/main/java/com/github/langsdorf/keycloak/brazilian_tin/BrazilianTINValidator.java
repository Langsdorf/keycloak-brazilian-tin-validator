package com.github.langsdorf.keycloak.brazilian_tin;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

import org.keycloak.validate.ValidationContext;
import org.keycloak.validate.Validator;
import org.keycloak.validate.ValidatorConfig;
import org.keycloak.validate.ValidationError;

public class BrazilianTINValidator implements Validator {

    @Override
    public ValidationContext validate(Object value, String inputHint, ValidationContext context,
            ValidatorConfig config) {
        boolean enableBypass = Boolean.TRUE.equals(config.getBoolean("enableBypass"));
        String bypassString = config.getString("bypassString");

        // Sem valor não é documento inválido: quem exige o campo é o `required` do perfil (que vale
        // só para o próprio usuário). O Keycloak entrega o atributo ausente como lista vazia, e
        // recusá-la aqui bloqueava toda gravação administrativa de uma conta sem documento — até
        // desativá-la.
        for (String document : values(value)) {
            if (enableBypass && document.equals(bypassString)) {
                continue;
            }

            if (!ValidationUtils.isValid(document)) {
                context.addError(new ValidationError(BrazilianTINValidatorFactory.ID, inputHint, "tin.invalid"));
            }
        }

        return context;
    }

    /** Os valores preenchidos do atributo (o Keycloak entrega uma lista; brancos não contam). */
    static List<String> values(Object value) {
        if (value == null) {
            return List.of();
        }

        Collection<?> items = value instanceof Collection<?> collection ? collection : List.of(value);

        return items.stream()
                .filter(Objects::nonNull)
                .map(Object::toString)
                .map(String::trim)
                .filter(item -> !item.isEmpty())
                .toList();
    }

}
