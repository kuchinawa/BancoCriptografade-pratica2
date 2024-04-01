package Banco;

import Criptografia.Chaves;

import java.math.BigInteger;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface BancoInterface extends Remote {
    String logar(String assinatura, String ipCliente, String cpf, String senha) throws Exception;

    String cadastro(String assinatura,String ipCliente, String dados) throws Exception;

    String sacar(String assinatura,String ipCliente, String cpf, String valor) throws Exception;

    String depositar(String assinatura,String ipCliente, String cpf, String valor) throws Exception;


    String transferir(String assinatura,String ipCliente, String contaOrigem, String contaDestino, String valor) throws Exception;

    void receberChave(String ipCliente, Chaves chave) throws RemoteException;
    String getSaldo(String assinatura,String ipCliente, String cpf) throws Exception;

    String investirPoupanca(String assinatura,String ipCliente, String cpf) throws RemoteException, Exception;
    String investirRendaFixa(String assinatura,String ipCliente, String cpf, String valor) throws RemoteException, Exception;

    BigInteger[] getChavePublica() throws Exception;
}
