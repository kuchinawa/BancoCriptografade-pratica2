package Banco;

import Criptografia.*;
import model.Usuario;

import java.io.*;
import java.math.BigInteger;
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
    static BigInteger[][] assinador;


    static GeradorSimplesChavesRSA geradorChaves = new GeradorSimplesChavesRSA();
    public Banco()  {
        contas = new HashMap<>();
       //salvarDados();
        carregarDados();
    }
    @Override

   //     }
    public String logar(String assinatura, String ipCliente, String cpf, String senha) throws Exception {
        String hmac = ImplHMAC.gerarHMAC(cpf, clientes.get(ipCliente).CHAVE_HMAC);
//        System.out.println("Hmac: " + clientes.get(ipCliente).CHAVE_HMAC);
        if (geradorChaves.verificarAssinatura(hmac, assinatura, clientes.get(ipCliente).chavePublica)) {
//        	System.out.println("oiiiii ");
            if (clientes.containsKey(ipCliente)) {
//            	System.out.println("Chegou aq");
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
        }
        else {
            System.out.println("Assinatura invalida!!!");
        }return null;
    }

    @Override
    public String cadastro(String assinatura,String ipCliente, String dados) throws Exception {
        System.out.println("Assinatura: " + assinatura);
        String hmac = ImplHMAC.gerarHMAC(dados, clientes.get(ipCliente).CHAVE_HMAC);

        if (geradorChaves.verificarAssinatura(hmac, assinatura, clientes.get(ipCliente).chavePublica)) {
            System.out.println("Assinatura valida!!!");
            if (clientes.containsKey(ipCliente)) {
                Chaves chave = clientes.get(ipCliente);
                String dadosDescriptografados = AES.decifrar(dados, chave.CHAVE_AES);
                dadosDescriptografados = Vernam.decifrar(dadosDescriptografados, chave.CHAVE_VERNAM);


                String[] dadosUsuario = dadosDescriptografados.split("-");
                String cpf = dadosUsuario[1];

                if (!contas.containsKey(cpf)) {
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
        }else {
            System.out.println("Assinatura invalida!!!");
        }return null;
    }

    @Override
    public String sacar(String assinatura,String ipCliente, String cpf, String valor) throws Exception {
        String hmac = ImplHMAC.gerarHMAC(cpf, clientes.get(ipCliente).CHAVE_HMAC);
        if (geradorChaves.verificarAssinatura(hmac, assinatura, clientes.get(ipCliente).chavePublica)) {
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

                    String hmacServidor = ImplHMAC.gerarHMAC(dadosCriptografados, clientes.get(ipCliente).CHAVE_HMAC);
                    String assinaturaServidor = geradorChaves.assinarMensagem(hmacServidor, assinador[1]);
                    return dadosCriptografados + "§"+ assinaturaServidor;
                }
            }
        }else {
            System.out.println("Assinatura invalida!!!");
        }
        return  null;
    }

    @Override
    public String depositar(String assinatura,String ipCliente, String cpf, String valor) throws Exception {

        String hmac = ImplHMAC.gerarHMAC(cpf, clientes.get(ipCliente).CHAVE_HMAC);
        if (geradorChaves.verificarAssinatura(hmac, assinatura, clientes.get(ipCliente).chavePublica)) {
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

                String hmacServidor = ImplHMAC.gerarHMAC(dadosCriptografados, clientes.get(ipCliente).CHAVE_HMAC);
                String assinaturaServidor = geradorChaves.assinarMensagem(hmacServidor, assinador[1]);
                return dadosCriptografados + "§"+ assinaturaServidor;


            }
        }else{
            System.out.println("Assinatura invalida!!!");
        }
        return null;
    }

    @Override
    public String transferir(String assinatura,String ipCliente, String cpfOrigem, String cpfDestino, String valor) throws Exception {
        String hmac = ImplHMAC.gerarHMAC(cpfOrigem, clientes.get(ipCliente).CHAVE_HMAC);
        if (geradorChaves.verificarAssinatura(hmac, assinatura, clientes.get(ipCliente).chavePublica)) {
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

                    String hmacServidor = ImplHMAC.gerarHMAC(retorno, clientes.get(ipCliente).CHAVE_HMAC);
                    String assinaturaServidor = geradorChaves.assinarMensagem(hmacServidor, assinador[1]);
                    return retorno + "§"+ assinaturaServidor;

                }
                return null;
            }
        }else{
            System.out.println("Assinatura Invalida!!!");
        }return null;
    }

    @Override
    public String getSaldo(String assinatura, String ipCliente, String cpf) throws Exception {

        String hmac = ImplHMAC.gerarHMAC(cpf, clientes.get(ipCliente).CHAVE_HMAC);
        if (geradorChaves.verificarAssinatura(hmac, assinatura, clientes.get(ipCliente).chavePublica)) {
            if (clientes.containsKey(ipCliente)) {
                Chaves chave = clientes.get(ipCliente);
                String cpfDescriptografado = AES.decifrar(cpf, chave.CHAVE_AES);
                cpfDescriptografado = Vernam.decifrar(cpfDescriptografado, chave.CHAVE_VERNAM);

                if (!contas.containsKey(cpfDescriptografado)) {
                    return null;
                }
                Usuario usuario = contas.get(cpfDescriptografado);

                String saldoCriptografo = Vernam.cifrar(usuario.getSaldo(), chave.CHAVE_VERNAM);
                saldoCriptografo = AES.cifrar(saldoCriptografo, chave.CHAVE_AES);

                String hmacServidor = ImplHMAC.gerarHMAC(saldoCriptografo, clientes.get(ipCliente).CHAVE_HMAC);
                String assinaturaServidor = geradorChaves.assinarMensagem(hmacServidor, assinador[1]);
                return saldoCriptografo + "§"+ assinaturaServidor;
            }

        }else{
            System.out.println("Asinatura invalida!!!");
        }return null;
    }
    @Override
    public void receberChave(String ipCliente, Chaves chave) throws RemoteException {
        clientes.put(ipCliente, chave);
    }


    @Override
    public String investirPoupanca(String assinatura, String ipCliente, String cpf) throws Exception {

        String hmac = ImplHMAC.gerarHMAC(cpf, clientes.get(ipCliente).CHAVE_HMAC);
        if (geradorChaves.verificarAssinatura(hmac, assinatura, clientes.get(ipCliente).chavePublica)) {
            if (clientes.containsKey(ipCliente)) {
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


                String hmacServidor = ImplHMAC.gerarHMAC(retorno, clientes.get(ipCliente).CHAVE_HMAC);
                String assinaturaServidor = geradorChaves.assinarMensagem(hmacServidor, assinador[1]);
                return retorno + "§"+ assinaturaServidor;

            }
        }else {
            System.out.println("Assinatura invalida!!!");
        } return null;
    }

    @Override
    public String investirRendaFixa(String assinatura, String ipCliente, String cpf, String valor) throws RemoteException, Exception {

        String hmac = ImplHMAC.gerarHMAC(cpf, clientes.get(ipCliente).CHAVE_HMAC);
        if (geradorChaves.verificarAssinatura(hmac, assinatura, clientes.get(ipCliente).chavePublica)) {
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


                String hmacServidor = ImplHMAC.gerarHMAC(retorno, clientes.get(ipCliente).CHAVE_HMAC);
                String assinaturaServidor = geradorChaves.assinarMensagem(hmacServidor, assinador[1]);
                return retorno + "§"+ assinaturaServidor;
            }
            System.out.println("INVASOR DETECTADO!!!");
            return null;
        }else{
            System.out.println("Assinatura invalida!!!");
        }return null;
    }
    public BigInteger[] getChavePublica() {
        return assinador[0];
    }

    public static void main(String[] args) {
        geradorChaves = new GeradorSimplesChavesRSA();
        assinador = geradorChaves.gerarParChaves(1327); //rsa 400
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
