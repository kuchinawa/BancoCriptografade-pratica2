package teste;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Base64;

public class GeradorSimplesChavesRSA {
    private final SecureRandom aleatorio = new SecureRandom();

    public BigInteger[][] gerarParChaves(int tamanhoBit) {
        BigInteger p = BigInteger.probablePrime(tamanhoBit / 2, aleatorio);
        BigInteger q = BigInteger.probablePrime(tamanhoBit / 2, aleatorio);
        BigInteger n = p.multiply(q);
        BigInteger m = (p.subtract(BigInteger.ONE)).multiply(q.subtract(BigInteger.ONE));
        BigInteger e = BigInteger.probablePrime(tamanhoBit / 2, aleatorio);
        while (m.gcd(e).intValue() > 1) {
            e = e.add(new BigInteger("2"));
        }
        BigInteger d = e.modInverse(m);
        BigInteger[] chavePublica = {e, n};
        BigInteger[] chavePrivada = {d, n};
        return new BigInteger[][]{chavePublica, chavePrivada};
    }

    public String assinarMensagem(String mensagem, BigInteger[] chavePrivada) {
        BigInteger m = new BigInteger(mensagem.getBytes());
        BigInteger d = chavePrivada[0];
        BigInteger n = chavePrivada[1];
        BigInteger assinatura = m.modPow(d, n);
        return Base64.getEncoder().encodeToString(assinatura.toByteArray());
    }

    public boolean verificarAssinatura(String mensagem, String assinaturaBase64, BigInteger[] chavePublica) {
        BigInteger m = new BigInteger(mensagem.getBytes());
        BigInteger e = chavePublica[0];
        BigInteger n = chavePublica[1];
        BigInteger assinatura = new BigInteger(Base64.getDecoder().decode(assinaturaBase64));
        BigInteger mVerificado = assinatura.modPow(e, n);
        return m.equals(mVerificado);
    }

    public static void main(String[] args) {
        GeradorSimplesChavesRSA rsa = new GeradorSimplesChavesRSA();
        BigInteger[][] chaves = rsa.gerarParChaves(1024);
        BigInteger[] chavePublica = chaves[0];
        BigInteger[] chavePrivada = chaves[1];
        String mensagem = "mensagem secreta";
        System.out.println("Mensagem original: " + mensagem);
        System.out.println("Chave pública: " + chavePublica[0] + ", " + chavePublica[1]);
        System.out.println(rsa.verificarAssinatura(mensagem, rsa.assinarMensagem(mensagem, chavePrivada), chavePublica));
    }
}