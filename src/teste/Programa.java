package teste;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class Programa {
    private static void testeRSA(){
        String msg="mensagem secreta";
        try {
            ImplRSA rsa = new ImplRSA();
            String cifrada =rsa.cifrar(msg);
            System.out.println("Mensagem cifrada: "+cifrada);
            byte[] bytesMensagemCifrada = cifrada.getBytes();
            System.out.println("Bytes da mensagem cifrada: "+
                    Arrays.toString(bytesMensagemCifrada));
            String decifrada =rsa.decifrar(cifrada);
            System.out.println("Mensagem decifrada: "+decifrada);
        } catch(NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch(InvalidKeyException e){
            e.printStackTrace();
        } catch(NoSuchPaddingException e) {
            e.printStackTrace();
        } catch(IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch(BadPaddingException e){
            e.printStackTrace();
        }
    }
    public static void main(String[]args){

        testeRSA();
    }
}

