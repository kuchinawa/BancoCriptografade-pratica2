package Criptografia;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Base64;

public class AES {
    private static final String ALGORITMO = "AES";

    public static String cifrar(String texto, String chave) throws Exception {
        Key key = new SecretKeySpec(chave.getBytes(), ALGORITMO);
        Cipher cipher = Cipher.getInstance(ALGORITMO);
        cipher.init(Cipher.ENCRYPT_MODE, key);

        byte[] textoCifrado = cipher.doFinal(texto.getBytes());
        return Base64.getEncoder().encodeToString(textoCifrado);
    }

    public static String decifrar(String textoCifrado, String chave) throws Exception {
        Key key = new SecretKeySpec(chave.getBytes(), ALGORITMO);
        Cipher cipher = Cipher.getInstance(ALGORITMO);
        cipher.init(Cipher.DECRYPT_MODE, key);

        byte[] textoDecifrado = Base64.getDecoder().decode(textoCifrado);
        return new String(cipher.doFinal(textoDecifrado));
    }
}
