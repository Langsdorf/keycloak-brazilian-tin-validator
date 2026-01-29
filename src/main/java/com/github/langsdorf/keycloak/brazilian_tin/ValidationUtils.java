package com.github.langsdorf.keycloak.brazilian_tin;

public class ValidationUtils {

    public static boolean isValid(String value) {
        if (value == null || value.isEmpty())
            return false;

        String doc = value.replaceAll("\\D", "");

        if (doc.length() == 11)
            return isValidCPF(doc);
        if (doc.length() == 14)
            return isValidCNPJ(doc);

        return false;
    }

    private static boolean isValidCPF(String cpf) {
        if (cpf.length() != 11 || cpf.chars().distinct().count() == 1)
            return false;

        int sum = 0;
        for (int i = 0; i < 9; i++)
            sum += (cpf.charAt(i) - '0') * (10 - i);
        int d1 = (11 - (sum % 11)) % 10;

        sum = 0;
        for (int i = 0; i < 10; i++)
            sum += (cpf.charAt(i) - '0') * (11 - i);
        int d2 = (11 - (sum % 11)) % 10;

        return d1 == (cpf.charAt(9) - '0') && d2 == (cpf.charAt(10) - '0');
    }

    private static boolean isValidCNPJ(String cnpj) {
        if (cnpj.length() != 14 || cnpj.chars().distinct().count() == 1)
            return false;

        int[] w1 = { 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2 };
        int[] w2 = { 6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2 };

        int sum = 0;
        for (int i = 0; i < 12; i++) {
            sum += (cnpj.charAt(i) - '0') * w1[i];
        }
        int d1 = 11 - (sum % 11);
        d1 = d1 > 9 ? 0 : d1;

        sum = 0;
        for (int i = 0; i < 13; i++) {
            sum += (cnpj.charAt(i) - '0') * w2[i];
        }
        int d2 = 11 - (sum % 11);
        d2 = d2 > 9 ? 0 : d2;

        return d1 == (cnpj.charAt(12) - '0') &&
                d2 == (cnpj.charAt(13) - '0');
    }
}
