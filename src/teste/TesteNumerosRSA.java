package teste;

import java.math.BigInteger;

public class TesteNumerosRSA {
    private static BigInteger testeRSA100() {
        BigInteger p = new
                BigInteger("40094690950920881030683735292761468389214899724061");
        BigInteger q = new
                BigInteger("37975227936943673922808872755445627854565536638199");
        return p.multiply(q);
    }

    private static BigInteger testeRSA250() {
        return p.multiply(q);
    }

    static BigInteger p = new
            BigInteger("64135289477071580278790190170577389084825014742943447208116859632024532344630238623598752668347708737661925585694639798853367");
    static BigInteger q = new
            BigInteger("33372027594978156556226010605355114227940760344767554666784520987023841729210037080257448673296881877565718986258036932062711");

    public static void main(String[] args) {
        System.out.println(testeRSA100());
        System.out.println();
        System.out.println(testeRSA250());
    }
}