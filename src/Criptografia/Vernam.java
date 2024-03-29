package Criptografia;

public class Vernam {
    public static String cifrar(String texto, String chave) {
        StringBuilder cifrado = new StringBuilder();
        for (int i = 0; i < texto.length(); i++) {
            int textoChar = texto.charAt(i);
            int chaveChar = chave.charAt(i % chave.length());
            int cifradoChar = textoChar ^ chaveChar;
            cifrado.append((char) cifradoChar);
        }
        return cifrado.toString();
    }

    public static String decifrar(String textoCifrado, String chave) {
        return cifrar(textoCifrado, chave);
    }
}
