package Banco;

import Criptografia.AES;
import Criptografia.Chaves;
import Criptografia.Vernam;
import model.Usuario;

import java.io.*;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Map;

public class Banco implements BancoInterface {
    public Map<String, Chaves> clientes = new HashMap<>();
    public Map<String, Usuario> contas;
    private static final String ARQUIVO_DADOS = "dados_banco.txt";
    public Banco() {
        contas = new HashMap<>();
        carregarDados();
    }

    @Override
    public String logar(String ipCliente, String cpf, String senha) throws Exception {
        if (clientes.containsKey(ipCliente)){
            Chaves chave = clientes.get(ipCliente);
            String cpfDescriptografado = AES.decifrar(cpf, chave.CHAVE_AES);
            cpfDescriptografado = Vernam.decifrar(cpfDescriptografado, chave.CHAVE_VERNAM);
            String senhaDescriptografada = AES.decifrar(senha, chave.CHAVE_AES);
            senhaDescriptografada = Vernam.decifrar(senhaDescriptografada, chave.CHAVE_VERNAM);
            if (contas.containsKey(cpfDescriptografado)) {
                Usuario usuario = contas.get(cpfDescriptografado);
                if (usuario.getSenha().equals(senhaDescriptografada)) {
                    String dadosCriptografados = usuario.getNome() + "-" + usuario.getCpf();
                    dadosCriptografados = Vernam.cifrar(dadosCriptografados, chave.CHAVE_VERNAM);
                    dadosCriptografados = AES.cifrar(dadosCriptografados, chave.CHAVE_AES);
                    return dadosCriptografados;
                }
            }
        }
        return null;
    }

    @Override
    public String cadastro(String ipCliente, String dados) throws Exception {
        if (clientes.containsKey(ipCliente)){
            Chaves chave = clientes.get(ipCliente);
            String dadosDescriptografados = AES.decifrar(dados, chave.CHAVE_AES);
            dadosDescriptografados = Vernam.decifrar(dadosDescriptografados, chave.CHAVE_VERNAM);


            String[] dadosUsuario = dadosDescriptografados.split("-");
            String cpf = dadosUsuario[1];

            if (!contas.containsKey(cpf)){
                Usuario usuario = new Usuario(dadosDescriptografados);
                contas.put(usuario.getCpf(), usuario);
                salvarDados();
                String dadosCriptografados = usuario.toString();

                dadosCriptografados = Vernam.cifrar(dadosCriptografados, chave.CHAVE_VERNAM);
                dadosCriptografados = AES.cifrar(dadosCriptografados, chave.CHAVE_AES);
                return dadosCriptografados;
            }
            return null;
        }
        return null;
    }

    @Override
    public String sacar(String ipCliente, String cpf, String valor) throws Exception {
        if (clientes.containsKey(ipCliente)){
            Chaves chave = clientes.get(ipCliente);
            String cpfDescriptografado = AES.decifrar(cpf, chave.CHAVE_AES);
            cpfDescriptografado = Vernam.decifrar(cpfDescriptografado, chave.CHAVE_VERNAM);
            String valorDescriptografado = AES.decifrar(valor, chave.CHAVE_AES);
            valorDescriptografado = Vernam.decifrar(valorDescriptografado, chave.CHAVE_VERNAM);

            if (!contas.containsKey(cpfDescriptografado)) {
                return null;
            }
            Usuario usuario = contas.get(cpfDescriptografado);
            double saldo = Double.parseDouble(usuario.getSaldo());
            double valorSacado = Double.parseDouble(valorDescriptografado);
            if (saldo >= valorSacado) {
                usuario.setSaldo(String.valueOf(saldo - valorSacado));
                salvarDados();
                String dadosCriptografados = usuario.toString();

                dadosCriptografados = Vernam.cifrar(dadosCriptografados, chave.CHAVE_VERNAM);
                dadosCriptografados = AES.cifrar(dadosCriptografados, chave.CHAVE_AES);


                return dadosCriptografados;
            }
        }
        return null;
    }

    @Override
    public String depositar(String ipCliente, String cpf, String valor) throws Exception {
        if (clientes.containsKey(ipCliente)){
            Chaves chave = clientes.get(ipCliente);
            String cpfDescriptografado = AES.decifrar(cpf, chave.CHAVE_AES);
            cpfDescriptografado = Vernam.decifrar(cpfDescriptografado, chave.CHAVE_VERNAM);
            String valorDescriptografado = AES.decifrar(valor, chave.CHAVE_AES);
            valorDescriptografado = Vernam.decifrar(valorDescriptografado, chave.CHAVE_VERNAM);

            if (!contas.containsKey(cpfDescriptografado)) {
                return null;
            }
            Usuario usuario = contas.get(cpfDescriptografado);
            double saldo = Double.parseDouble(usuario.getSaldo());
            double valorDepositado = Double.parseDouble(valorDescriptografado);
            usuario.setSaldo(String.valueOf(saldo + valorDepositado));
            salvarDados();
            String dadosCriptografados = usuario.toString();

            dadosCriptografados = Vernam.cifrar(dadosCriptografados, chave.CHAVE_VERNAM);
            dadosCriptografados = AES.cifrar(dadosCriptografados, chave.CHAVE_AES);

            return dadosCriptografados;
        }
        return null;
    }

    @Override
    public String transferir(String ipCliente, String cpfOrigem, String cpfDestino, String valor) throws Exception {
        if (clientes.containsKey(ipCliente)){
            Chaves chave = clientes.get(ipCliente);
            String cpfOrigemDescriptografado = AES.decifrar(cpfOrigem, chave.CHAVE_AES);
            cpfOrigemDescriptografado = Vernam.decifrar(cpfOrigemDescriptografado, chave.CHAVE_VERNAM);
            String cpfDestinoDescriptografado = AES.decifrar(cpfDestino, chave.CHAVE_AES);
            cpfDestinoDescriptografado = Vernam.decifrar(cpfDestinoDescriptografado, chave.CHAVE_VERNAM);
            String valorDescriptografado = AES.decifrar(valor, chave.CHAVE_AES);
            valorDescriptografado = Vernam.decifrar(valorDescriptografado, chave.CHAVE_VERNAM);

            if (!contas.containsKey(cpfOrigemDescriptografado) || !contas.containsKey(cpfDestinoDescriptografado)) {
                return null;
            }
            Usuario usuarioOrigem = contas.get(cpfOrigemDescriptografado);
            Usuario usuarioDestino = contas.get(cpfDestinoDescriptografado);
            double saldoOrigem = Double.parseDouble(usuarioOrigem.getSaldo());
            double valorTransferido = Double.parseDouble(valorDescriptografado);
            if (saldoOrigem >= valorTransferido) {
                usuarioOrigem.setSaldo(String.valueOf(saldoOrigem - valorTransferido));
                double saldoDestino = Double.parseDouble(usuarioDestino.getSaldo());
                usuarioDestino.setSaldo(String.valueOf(saldoDestino + valorTransferido));
                salvarDados();

                String retorno = usuarioOrigem.getSaldo() + "-" + usuarioDestino.getNome();

                retorno = Vernam.cifrar(retorno, chave.CHAVE_VERNAM);
                retorno = AES.cifrar(retorno, chave.CHAVE_AES);

                return retorno;
            }
            return null;
        }
        return null;
    }

    @Override
    public String getSaldo(String ipCliente, String cpf) throws Exception {
        if (clientes.containsKey(ipCliente)){
            Chaves chave = clientes.get(ipCliente);
            String cpfDescriptografado = AES.decifrar(cpf, chave.CHAVE_AES);
            cpfDescriptografado = Vernam.decifrar(cpfDescriptografado, chave.CHAVE_VERNAM);

            if (!contas.containsKey(cpfDescriptografado)) {
                return null;
            }
            Usuario usuario = contas.get(cpfDescriptografado);

            String saldoCriptografo = Vernam.cifrar(usuario.getSaldo(), chave.CHAVE_VERNAM);
            saldoCriptografo = AES.cifrar(saldoCriptografo, chave.CHAVE_AES);

            return saldoCriptografo;
        }
        return null;
    }

    @Override
    public void receberChave(String ipCliente, Chaves chave) throws RemoteException {
        clientes.put(ipCliente, chave);
    }


    @Override
    public String investirPoupanca(String ipCliente, String cpf) throws Exception {
        if (clientes.containsKey(ipCliente)){
            Chaves chave = clientes.get(ipCliente);
            String cpfDescriptografado = AES.decifrar(cpf, chave.CHAVE_AES);
            cpfDescriptografado = Vernam.decifrar(cpfDescriptografado, chave.CHAVE_VERNAM);

            if (!contas.containsKey(cpfDescriptografado)) {
                return null;
            }
            Usuario usuario = contas.get(cpfDescriptografado);
            double saldo = Double.parseDouble(usuario.getSaldo());

            double taxaJuros = 0.005;
            int vezesPorPeriodo = 1;


            double valorAplicado3Meses = saldo * Math.pow(1 + taxaJuros / vezesPorPeriodo, vezesPorPeriodo * 3);
            double valorAplicado6Meses = saldo * Math.pow(1 + taxaJuros / vezesPorPeriodo, vezesPorPeriodo * 6);
            double valorAplicado12Meses = saldo * Math.pow(1 + taxaJuros / vezesPorPeriodo, vezesPorPeriodo * 12);

            String retorno = usuario.getSaldo() + "-" + String.valueOf(valorAplicado3Meses) + "-" + String.valueOf(valorAplicado6Meses) + "-" + String.valueOf(valorAplicado12Meses);

            retorno = Vernam.cifrar(retorno, chave.CHAVE_VERNAM);
            retorno = AES.cifrar(retorno, chave.CHAVE_AES);
            return retorno;
        }
        return null;
    }

    @Override
    public String investirRendaFixa(String ipCliente, String cpf, String valor) throws RemoteException, Exception {
        if (clientes.containsKey(ipCliente)){
            Chaves chave = clientes.get(ipCliente);
            String cpfDescriptografado = AES.decifrar(cpf, chave.CHAVE_AES);
            cpfDescriptografado = Vernam.decifrar(cpfDescriptografado, chave.CHAVE_VERNAM);
            String valorDescriptografado = AES.decifrar(valor, chave.CHAVE_AES);
            valorDescriptografado = Vernam.decifrar(valorDescriptografado, chave.CHAVE_VERNAM);

            if (!contas.containsKey(cpfDescriptografado)) {
                return null;
            }
            Usuario usuario = contas.get(cpfDescriptografado);
            double saldo = Double.parseDouble(usuario.getSaldo());
            double valorMensal = Double.parseDouble(valorDescriptografado);

            double taxaJuros = 0.015;
            int vezesPorPeriodo = 1;


            double valorAplicado3Meses = 0;
            double valorAplicado6Meses = 0;
            double valorAplicado12Meses = 0;

            for (int i = 0; i < 3; i++) {
                saldo += valorMensal;
                valorAplicado3Meses = saldo * Math.pow(1 + taxaJuros / vezesPorPeriodo, vezesPorPeriodo);
            }

            for (int i = 0; i < 6; i++) {
                saldo += valorMensal;
                valorAplicado6Meses = saldo * Math.pow(1 + taxaJuros / vezesPorPeriodo, vezesPorPeriodo);
            }

            for (int i = 0; i < 12; i++) {
                saldo += valorMensal;
                valorAplicado12Meses = saldo * Math.pow(1 + taxaJuros / vezesPorPeriodo, vezesPorPeriodo);
            }

            String retorno = usuario.getSaldo() + "-" + String.valueOf(valorAplicado3Meses) + "-" + String.valueOf(valorAplicado6Meses) + "-" + String.valueOf(valorAplicado12Meses);

            retorno = Vernam.cifrar(retorno, chave.CHAVE_VERNAM);
            retorno = AES.cifrar(retorno, chave.CHAVE_AES);
            return retorno;
        }
            System.out.println("INVASOR DETECTADO!!!");
        return null;
    }

    public static void main(String[] args) {
        try {
            Banco refObjetoRemoto = new Banco();
            BancoInterface RefServer = (BancoInterface) UnicastRemoteObject
                    .exportObject(refObjetoRemoto, 6002);
            Registry registro = LocateRegistry.createRegistry(6002);
            registro.bind("Banco", RefServer);


        }catch (Exception e) {
            System.err.println("Banco: " + e.toString());
            e.printStackTrace();
        }
    }

    private void salvarDados() {
        try (ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(ARQUIVO_DADOS))) {
            outputStream.writeObject(contas);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void carregarDados() {
        try (ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(ARQUIVO_DADOS))) {
            Object obj = inputStream.readObject();
            if (obj instanceof Map) {
                contas = (Map<String, Usuario>) obj;
            } else {
                System.out.println("Erro ao carregar dados do arquivo.");
            }
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Arquivo de dados ainda não existe ou ocorreu um erro ao ler os dados.");
        }
    }
}
