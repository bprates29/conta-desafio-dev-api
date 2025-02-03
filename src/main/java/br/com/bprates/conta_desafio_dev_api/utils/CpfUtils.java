package br.com.bprates.conta_desafio_dev_api.utils;

public class CpfUtils {
    /**
     * Valida se um CPF é válido.
     * @param cpf String contendo apenas dígitos do CPF (sem pontuação).
     * @return true se for um CPF válido, false caso contrário.
     */
    public static boolean isValidCPF(String cpf) {
        if (cpf == null) return false;

        cpf = cpf.replaceAll("\\D", "");

        if (cpf.length() != 11) {
            return false;
        }

        if (cpf.matches("(\\d)\\1{10}")) {
            return false;
        }

        int soma = 0;
        int peso = 10;
        for (int i = 0; i < 9; i++) {
            int num = (cpf.charAt(i) - '0');
            soma += num * peso;
            peso--;
        }
        int resto = 11 - (soma % 11);
        char dig10 = (resto == 10 || resto == 11) ? '0' : (char) (resto + '0');

        soma = 0;
        peso = 11;
        for (int i = 0; i < 10; i++) {
            int num = (cpf.charAt(i) - '0');
            soma += num * peso;
            peso--;
        }
        resto = 11 - (soma % 11);
        char dig11 = (resto == 10 || resto == 11) ? '0' : (char) (resto + '0');

        return (dig10 == cpf.charAt(9)) && (dig11 == cpf.charAt(10));
    }
}
