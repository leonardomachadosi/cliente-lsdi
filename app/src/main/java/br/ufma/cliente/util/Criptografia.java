package br.ufma.cliente.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by Renan on 04/09/16.
 */
public class Criptografia {
    private static MessageDigest md = null;

    /**
     * Metodo estático para a geração do algoritmo de criptografia md5.
     */
    static {
        try {
            md = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Criptografa a senha.
     *
     * @param password senha sem criptografia.
     * @return senha com criptografia.
     */
    public static String md5(String password) {
        if (md != null) {
            return new String(hexCodes(md.digest(password.getBytes())));
        }
        return null;
    }

    private static char[] hexCodes(byte[] text) {
        char[] hexOutput = new char[text.length * 2];
        String hexString;

        for (int i = 0; i < text.length; i++) {
            hexString = "00" + Integer.toHexString(text[i]);
            hexString.getChars(hexString.length() - 2, hexString.length(), hexOutput, i * 2);
        }
        return hexOutput;
    }
}
