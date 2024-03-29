package teste;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.*;
import java.util.Base64;

public class ImplRSA {
    PublicKey chavePublica;
    PrivateKey chavePrivada;
    public ImplRSA() throws NoSuchAlgorithmException {
        gerarChaves();
    }
    private void gerarChaves() throws NoSuchAlgorithmException {
        KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
        SecureRandom sr = new SecureRandom();
        kpg.initialize(1024, sr);
        KeyPair kp = kpg.generateKeyPair();
        chavePublica = kp.getPublic();
        String chavePublicaBase64 =
                Base64
                        .getEncoder()
                        .encodeToString(chavePublica.getEncoded());
        System.out.println("Chave pública: " + chavePublicaBase64);
        chavePrivada = kp.getPrivate();
        String chavePrivadaBase64 =
                Base64
                        .getEncoder()
                        .encodeToString(chavePrivada.getEncoded());
        System.out.println("Chave privada: " + chavePrivadaBase64);
    }
    public String cifrar(String msg) throws NoSuchAlgorithmException,
            NoSuchPaddingException,
            InvalidKeyException,
            IllegalBlockSizeException,
            BadPaddingException
    {
        Cipher cifrador = Cipher.getInstance("RSA");
        cifrador.init(Cipher.ENCRYPT_MODE, chavePrivada);
        byte[] bytesMsgCifrada = cifrador.doFinal(msg.getBytes());
        String msgCifradaBase64 =
                Base64
                        .getEncoder()
                        .encodeToString(bytesMsgCifrada);
        return msgCifradaBase64;
    }
    public String decifrar(String msgCifradaBase64) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException
    {
        byte[] bytesMsgCifrada =
                Base64
                        .getDecoder()
                        .decode(msgCifradaBase64);

        Cipher decifrador = Cipher.getInstance("RSA");
        decifrador.init(Cipher.DECRYPT_MODE, chavePublica);
        byte[] bytesMsgDecifrada =
                decifrador
                        .doFinal(bytesMsgCifrada);
        return new String(bytesMsgDecifrada);
    }
}