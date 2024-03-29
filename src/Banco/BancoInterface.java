package Banco;

import Criptografia.Chaves;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface BancoInterface extends Remote {
    String logar(String ipCliente, String cpf, String senha) throws Exception;

    String cadastro(String ipCliente, String dados) throws Exception;

    String sacar(String ipCliente, String cpf, String valor) throws Exception;

    String depositar(String ipCliente, String cpf, String valor) throws Exception;


    String transferir(String ipCliente, String contaOrigem, String contaDestino, String valor) throws Exception;

    void receberChave(String ipCliente, Chaves chave) throws RemoteException;
    String getSaldo(String ipCliente, String cpf) throws Exception;

    String investirPoupanca(String ipCliente, String cpf) throws RemoteException, Exception;
    String investirRendaFixa(String ipCliente, String cpf, String valor) throws RemoteException, Exception;

}
