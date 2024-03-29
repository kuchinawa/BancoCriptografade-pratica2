package teste;

import java.math.BigInteger;

public class TesteBigInteger {
    public static void main(String[] args) {
        // Exemplo de add: adicionar dois BigIntegers
        BigInteger num1 = new BigInteger("1234567890104681103177");
        BigInteger num2 = new BigInteger("9876543210986399863987");
        BigInteger soma = num1.add(num2);
        System.out.println("Soma: " + soma);


        // Exemplo de subtract: subtrair um BigInteger de outro
        BigInteger subtracao = num2.subtract(num1);
        System.out.println("Subtração: " + subtracao);


        // Exemplo de multiply: multiplicar dois BigIntegers
        BigInteger produto = num1.multiply(num2);
        System.out.println("Produto da multiplicação: " + produto);



        // Exemplo de mod: calcular o resto da divisão de um BigInteger por outro
        BigInteger dividendo = new BigInteger("9876543210986399863987");
        BigInteger divisor = new BigInteger("1234567890104681103177");
        BigInteger resto = dividendo.mod(divisor);
        System.out.println("Resto da divisão: " + resto);

        // Exemplo de divide: dividir um BigInteger por outro
        BigInteger quociente = dividendo.divide(divisor);
        System.out.println("Quociente da divisão: " + quociente);



        // Exemplo de modPow: calcular (base^exponente) % m
        BigInteger base = new BigInteger("6050279225333");
        BigInteger exponente = new BigInteger("214032465024");
        BigInteger m = new BigInteger("1000000003");
        BigInteger resultadoModPow = base.modPow(exponente, m);
        System.out.println("Resultado de modPow: " + resultadoModPow);


        // Exemplo de modInverse: calcular o inverso modular de base % m
        BigInteger inverso = base.modInverse(m);
        System.out.println("Inverso modular: " + inverso);


        // Exemplo de bitLength: obter o número de bits necessários para
        //representar o valor
        BigInteger valor = new
                BigInteger("64135289477071580278790190170577389084825014742943447208116859632024532344630238623598752668347708737661925585694639798853367");
        int numBits = valor.bitLength();
        System.out.println("Número de bits: " + numBits);


        // Exemplo de compareTo: comparar dois BigIntegers
        int comparacao = num1.compareTo(num2);
        if (comparacao < 0) {
            System.out.println("num1 é menor que num2");
        } else if (comparacao > 0) {
            System.out.println("num1 é maior que num2");
        } else {
            System.out.println("num1 é igual a num2");
        }



        // Definindo dois números BigIntegers
        BigInteger n1 = new BigInteger("1000000000000006259");
        BigInteger n2 = new BigInteger("1000000000000006301");


        // Calculando o MDC usando o método gcd()
        BigInteger mdc = n1.gcd(n2);


        // Exibindo o MDC
        System.out.println("O Máximo Divisor Comum (MDC) de " +
                n1 +
                " e " +
                n2 +
                " é: " +
                mdc);
    }

}