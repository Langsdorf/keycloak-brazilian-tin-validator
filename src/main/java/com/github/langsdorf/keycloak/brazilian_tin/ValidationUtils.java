package com.github.langsdorf.keycloak.brazilian_tin;

import java.util.InputMismatchException;

//https://gist.github.com/clairtonluz/0e82a03e8b6c148608f1
public class ValidationUtils {

    public static boolean isValid(String value) {
        if (value == null || value.isEmpty())
            return false;

        // remove espaços nas extremidades antes de qualquer processamento
        value = value.trim();

        // CPF: permanece puramente numérico
        String cpf = value.replaceAll("\\D", "");
        if (cpf.length() == 11)
            return isValidCPF(cpf);

        // CNPJ: remove tudo que não é alfanumérico (máscara . / -, espaços e os
        // colchetes que o Keycloak adiciona ao serializar a lista de valores),
        // preserva as letras do formato alfanumérico e normaliza para maiúsculo
        String cnpj = value.replaceAll("[^0-9A-Za-z]", "").toUpperCase();
        if (cnpj.length() == 14)
            return isValidCNPJ(cnpj);

        return false;
    }

    /**
     * Realiza a validação do CPF.
     * 
     * @param cpf número de CPF a ser validado pode ser passado no formado
     *            999.999.999-99 ou
     *            99999999999
     * @return true se o CPF é válido e false se não é válido
     */
    private static boolean isValidCPF(String cpf) {
        if (cpf == null) {
            return false;
        }
        cpf = cpf.replace(".", "");
        cpf = cpf.replace("-", "");

        if (cpf.equals("00000000000") ||
                cpf.equals("11111111111") ||
                cpf.equals("22222222222") || cpf.equals("33333333333") ||
                cpf.equals("44444444444") || cpf.equals("55555555555") ||
                cpf.equals("66666666666") || cpf.equals("77777777777") ||
                cpf.equals("88888888888") || cpf.equals("99999999999") ||
                (cpf.length() != 11))
            return (false);

        if (!cpf.chars().allMatch(Character::isDigit)) {
            return false;
        }

        int d1, d2;
        int digito1, digito2, resto;
        int digitoCPF;
        String nDigResult;

        d1 = d2 = 0;
        digito1 = digito2 = resto = 0;

        for (int nCount = 1; nCount < cpf.length() - 1; nCount++) {
            digitoCPF = Integer.valueOf(cpf.substring(nCount - 1, nCount)).intValue();

            // multiplique a ultima casa por 2 a seguinte por 3 a seguinte por 4
            // e assim por diante.
            d1 = d1 + (11 - nCount) * digitoCPF;

            // para o segundo digito repita o procedimento incluindo o primeiro
            // digito calculado no passo anterior.
            d2 = d2 + (12 - nCount) * digitoCPF;
        }
        ;

        // Primeiro resto da divisão por 11.
        resto = (d1 % 11);

        // Se o resultado for 0 ou 1 o digito é 0 caso contrário o digito é 11
        // menos o resultado anterior.
        if (resto < 2)
            digito1 = 0;
        else
            digito1 = 11 - resto;

        d2 += 2 * digito1;

        // Segundo resto da divisão por 11.
        resto = (d2 % 11);

        // Se o resultado for 0 ou 1 o digito é 0 caso contrário o digito é 11
        // menos o resultado anterior.
        if (resto < 2)
            digito2 = 0;
        else
            digito2 = 11 - resto;

        // Digito verificador do CPF que está sendo validado.
        String nDigVerific = cpf.substring(cpf.length() - 2, cpf.length());

        // Concatenando o primeiro resto com o segundo.
        nDigResult = String.valueOf(digito1) + String.valueOf(digito2);

        // comparar o digito verificador do cpf com o primeiro resto + o segundo
        // resto.
        return nDigVerific.equals(nDigResult);
    }

    /**
     * Realiza a validação de um CNPJ, incluindo o formato alfanumérico definido
     * pela Receita Federal (IN RFB nº 2.229/2024, vigência a partir de julho/2026).
     *
     * Regras:
     * - 14 posições no total;
     * - as 12 primeiras posições podem ser alfanuméricas (0-9, A-Z);
     * - os 2 dígitos verificadores permanecem numéricos (0-9);
     * - o DV é calculado pelo módulo 11, usando o valor (ASCII - 48) de cada caractere,
     *   o que mapeia '0'..'9' para 0..9 e 'A'..'Z' para 17..42.
     *
     * @param cnpj String - o CNPJ já sem máscara e em maiúsculo (ex.: 99999999999999
     *             ou 12ABC34501DE35)
     * @return boolean
     */
    private static boolean isValidCNPJ(String cnpj) {
        if (cnpj == null || cnpj.length() != 14)
            return false;

        // 12 primeiras posições: alfanuméricas (0-9, A-Z); 2 últimas: numéricas
        if (!cnpj.substring(0, 12).matches("[0-9A-Z]{12}"))
            return false;
        if (!cnpj.substring(12).matches("[0-9]{2}"))
            return false;

        char dig13, dig14;
        int sm, i, r, num, peso; // "try" - protege o código para eventuais
                                 // erros de conversao de tipo (int)
        try {
            // Calculo do 1o. Digito Verificador
            sm = 0;
            peso = 2;
            for (i = 11; i >= 0; i--) {
                // converte o i-ésimo caractere do CNPJ em um número: // por
                // exemplo, transforma o caractere '0' no inteiro 0 // (48 eh a
                // posição de '0' na tabela ASCII)
                num = (int) (cnpj.charAt(i) - 48);
                sm = sm + (num * peso);
                peso = peso + 1;
                if (peso == 10)
                    peso = 2;
            }

            r = sm % 11;
            if ((r == 0) || (r == 1))
                dig13 = '0';
            else
                dig13 = (char) ((11 - r) + 48);

            // Calculo do 2o. Digito Verificador
            sm = 0;
            peso = 2;
            for (i = 12; i >= 0; i--) {
                num = (int) (cnpj.charAt(i) - 48);
                sm = sm + (num * peso);
                peso = peso + 1;
                if (peso == 10)
                    peso = 2;
            }
            r = sm % 11;
            if ((r == 0) || (r == 1))
                dig14 = '0';
            else
                dig14 = (char) ((11 - r) + 48);
            // Verifica se os dígitos calculados conferem com os dígitos
            // informados.
            if ((dig13 == cnpj.charAt(12)) && (dig14 == cnpj.charAt(13)))
                return (true);
            else
                return (false);
        } catch (InputMismatchException erro) {
            return (false);
        }
    }
}
