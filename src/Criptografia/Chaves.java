package Criptografia;

import java.io.Serializable;
import java.math.BigInteger;

public class Chaves implements Serializable {
    public  String CHAVE_AES = "chaveAES";
    public String CHAVE_VERNAM = "chaveVernam";
    public String CHAVE_HMAC = "";

    public BigInteger[] chavePublica;
}
