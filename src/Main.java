import Criptografia.*;
import Banco.BancoInterface;

import java.math.BigInteger;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;

public class Main {
    static BancoInterface stub;
    static String ipCliente = "5553";
    static String nome = null;
    static String cpf = null;

    static boolean autenticado = false;
    static GeradorSimplesChavesRSA gerador = new GeradorSimplesChavesRSA();
    static BigInteger[][] assinador;
    public static void main(String[] args) throws Exception {

        gerador = new GeradorSimplesChavesRSA();

        assinador = gerador.gerarParChaves(1327); //rsa 400

        try {
            Registry registro = LocateRegistry.getRegistry("localhost", 6002);
            stub = (BancoInterface) registro.lookup("Banco");

        } catch (Exception e) {
            System.err.println("Client exception: " + e.toString());
            e.printStackTrace();
        }

        Scanner scanner = new Scanner(System.in);
        boolean parar = false;

        Chaves chave = new Chaves();
        chave.CHAVE_AES = "achoquenaovai321";
        chave.CHAVE_VERNAM = "achoquenaovai333";
        chave.CHAVE_HMAC = "achoquenaovai123";
        chave.chavePublica = assinador[0];



        stub.receberChave(ipCliente, chave);


        while (!parar) {
            String cpfCriptografado;
            String HMAC;
            String assinatura;

            if (autenticado) {
                System.out.println("Bem vindo, "+ nome + ".");
                System.out.println("Escolha uma opção: ");
                System.out.println("1 - Ver Saldo");
                System.out.println("2 - Sacar");
                System.out.println("3 - Depositar");
                System.out.println("4 - Transferir");
                System.out.println("5 - Investir");
                System.out.println("6 - Sair");

                int opcao = scanner.nextInt();
                scanner.nextLine();

                switch (opcao) {

                    case 1:
                        cpfCriptografado = Vernam.cifrar(cpf, chave.CHAVE_VERNAM); //
                        cpfCriptografado = AES.cifrar(cpfCriptografado, chave.CHAVE_AES);
                        HMAC = ImplHMAC.gerarHMAC(cpfCriptografado, chave.CHAVE_HMAC);


                        assinatura = gerador.assinarMensagem(HMAC, assinador[1]);

                        try {
                            String saldoDescriptografado = stub.getSaldo(assinatura, ipCliente, cpfCriptografado);
                            saldoDescriptografado = AES.decifrar(saldoDescriptografado, chave.CHAVE_AES);
                            saldoDescriptografado = Vernam.decifrar(saldoDescriptografado, chave.CHAVE_VERNAM);

                            System.out.println("Seu saldo é de " + saldoDescriptografado + " dinheiros.");
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                        break;
                    case 2:
                        System.out.println("Digite o valor: ");
                        String valor = scanner.nextLine(); //

                        cpfCriptografado = Vernam.cifrar(cpf, chave.CHAVE_VERNAM);
                        cpfCriptografado = AES.cifrar(cpfCriptografado, chave.CHAVE_AES);

                        String valorCriptografado;
                        valorCriptografado = Vernam.cifrar(valor, chave.CHAVE_VERNAM);
                        valorCriptografado = AES.cifrar(valorCriptografado, chave.CHAVE_AES);

                        HMAC = ImplHMAC.gerarHMAC(cpfCriptografado, chave.CHAVE_HMAC);
                        assinatura = gerador.assinarMensagem(HMAC, assinador[1]);

                        try {
                            String saque = stub.sacar(assinatura, ipCliente, cpfCriptografado, valorCriptografado);
                            if (saque == null) {
                                System.out.println("Saldo insuficiente.");
                                break;
                            }
                            saque = AES.decifrar(saque, chave.CHAVE_AES);
                            saque = Vernam.decifrar(saque, chave.CHAVE_VERNAM);
                            saque = saque.split("-")[3];
                            System.out.println("Seu novo saldo é de " + saque);
                        } catch (Exception e) {

                            throw new RuntimeException(e);
                        }
                        break;
                    case 3:
                        System.out.println("Digite o valor: ");
                        valor = scanner.nextLine();

                        cpfCriptografado = Vernam.cifrar(cpf, chave.CHAVE_VERNAM);
                        cpfCriptografado = AES.cifrar(cpfCriptografado, chave.CHAVE_AES);
                        valorCriptografado = Vernam.cifrar(valor, chave.CHAVE_VERNAM);
                        valorCriptografado = AES.cifrar(valorCriptografado, chave.CHAVE_AES);

                        HMAC = ImplHMAC.gerarHMAC(cpfCriptografado, chave.CHAVE_HMAC);
                        assinatura = gerador.assinarMensagem(HMAC, assinador[1]);

                        try {
                            String depositar = stub.depositar(assinatura, ipCliente, cpfCriptografado, valorCriptografado);
                            if (depositar == null) {
                                System.out.println("Erro ao depositar.");
                                break;
                            }
                            depositar = AES.decifrar(depositar, chave.CHAVE_AES);
                            depositar = Vernam.decifrar(depositar, chave.CHAVE_VERNAM);
                            depositar = depositar.split("-")[3];


                            System.out.println("Você depositou " + valor + " dinheiros, seu novo saldo é de: " + depositar);
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                        break;
                    case 4:
                        String contaOrigem = cpf;
                        System.out.println("Digite a conta de destino: ");
                        String contaDestino = scanner.nextLine();
                        System.out.println("Digite o valor: ");
                        valor = scanner.nextLine();

                        String contaOrigemCriptografada = Vernam.cifrar(contaOrigem, chave.CHAVE_VERNAM);
                        contaOrigemCriptografada = AES.cifrar(contaOrigemCriptografada, chave.CHAVE_AES);
                        String contaDestinoCriptografada = Vernam.cifrar(contaDestino, chave.CHAVE_VERNAM);
                        contaDestinoCriptografada = AES.cifrar(contaDestinoCriptografada, chave.CHAVE_AES);
                        valorCriptografado = Vernam.cifrar(valor, chave.CHAVE_VERNAM);
                        valorCriptografado = AES.cifrar(valorCriptografado, chave.CHAVE_AES);

                        HMAC = ImplHMAC.gerarHMAC(contaOrigemCriptografada, chave.CHAVE_HMAC);
                        assinatura = gerador.assinarMensagem(HMAC, assinador[1]);

                        try {
                            String transferiu = stub.transferir(assinatura, ipCliente, contaOrigemCriptografada, contaDestinoCriptografada, valorCriptografado);
                            if(transferiu == null){
                                System.out.println("Erro ao transferir :(");
                                break;
                            }
                            transferiu = AES.decifrar(transferiu, chave.CHAVE_AES);
                            transferiu = Vernam.decifrar(transferiu, chave.CHAVE_VERNAM);
                            System.out.println("Você transferiu " + valor + " dinheiros para a conta de " + transferiu.split("-")[1] + " seu novo saldo é de: " + transferiu.split("-")[0] + ".");

                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                        break;

                    case 5:
                        System.out.println("Escolha uma opção de investimento: ");
                        System.out.println("1 - Poupança");
                        System.out.println("2 - Renda fixa");
                        System.out.println("3 - Voltar");

                        int opcaoInvestimento = scanner.nextInt();
                        scanner.nextLine();

                        switch (opcaoInvestimento) {
                            case 1:
                                cpfCriptografado= Vernam.cifrar(cpf, chave.CHAVE_VERNAM);
                                cpfCriptografado = AES.cifrar(cpfCriptografado, chave.CHAVE_AES);
                                HMAC = ImplHMAC.gerarHMAC(cpfCriptografado, chave.CHAVE_HMAC);
                                assinatura = gerador.assinarMensagem(HMAC, assinador[1]);
                                try {
                                    String poupanca = stub.investirPoupanca(assinatura, ipCliente, cpfCriptografado);
                                    if (poupanca == null) {
                                        System.out.println("Erro ao investir.");
                                        break;
                                    }
                                    poupanca = AES.decifrar(poupanca, chave.CHAVE_AES);
                                    poupanca = Vernam.decifrar(poupanca, chave.CHAVE_VERNAM);

                                    System.out.println("Seu saldo atual é: " + String.format("%.2f", Double.parseDouble(poupanca.split("-")[0]))
                                            + "\n O rendimento aplicado na poupança em 3 meses é de: " + String.format("%.2f", Double.parseDouble(poupanca.split("-")[1]))
                                            + "\n O rendimento aplicado na poupança em 6 meses é de: " + String.format("%.2f", Double.parseDouble(poupanca.split("-")[2]))
                                            + "\n O rendimento aplicado na poupança em 12 meses é de: " + String.format("%.2f", Double.parseDouble(poupanca.split("-")[3])));

                                }catch (Exception e){
                                    throw new RuntimeException(e);
                                }
                                break;
                            case 2:
                                System.out.println("Digite o valor: ");
                                valor = scanner.nextLine();

                                cpfCriptografado = Vernam.cifrar(cpf, chave.CHAVE_VERNAM);
                                cpfCriptografado = AES.cifrar(cpfCriptografado, chave.CHAVE_AES);
                                valorCriptografado = Vernam.cifrar(valor, chave.CHAVE_VERNAM);
                                valorCriptografado = AES.cifrar(valorCriptografado, chave.CHAVE_AES);

                                HMAC = ImplHMAC.gerarHMAC(cpfCriptografado, chave.CHAVE_HMAC);
                                assinatura = gerador.assinarMensagem(HMAC, assinador[1]);
                                try {
                                    String renda = stub.investirRendaFixa(assinatura, ipCliente, cpfCriptografado, valorCriptografado);
                                    if (renda == null) {
                                        System.out.println("Erro ao investir.");
                                        break;
                                    }
                                    renda = AES.decifrar(renda, chave.CHAVE_AES);
                                    renda = Vernam.decifrar(renda, chave.CHAVE_VERNAM);
                                    System.out.println("Seu saldo atual é: " + String.format("%.2f", Double.parseDouble(renda.split("-")[0]))
                                            + "\n O rendimento aplicado na renda fixa de "+ valor + " em 3 meses é de: " + String.format("%.2f", Double.parseDouble(renda.split("-")[1]))
                                            + "\n O rendimento aplicado na renda fixa de "+ valor + " em 6 meses é de: " + String.format("%.2f", Double.parseDouble(renda.split("-")[2]))
                                            + "\n O rendimento aplicado na renda fixa de "+ valor + " em 12 meses é de: " + String.format("%.2f", Double.parseDouble(renda.split("-")[3])));

                                } catch (RemoteException e) {
                                    throw new RuntimeException(e);
                                }
                                break;
                            case 3:
                                break;
                        }
                        break;
                    case 6:
                        parar = true;
                        break;
                }
            }else{
                System.out.println("Bem vindo ao banco, escolha uma opção: ");
                System.out.println("1 - Cadastrar");
                System.out.println("2 - Logar");
                System.out.println("3 - Sair");

                int opcao = scanner.nextInt();
                scanner.nextLine();

                switch (opcao){
                    case 1:
                        System.out.println("Digite o nome: ");
                        nome = scanner.nextLine();
                        System.out.println("Digite o CPF: ");
                        cpf = scanner.nextLine();
                        System.out.println("Digite a senha: ");
                        String senha = scanner.nextLine();
                        String saldo = "0.0";

                        String dados = nome + "-" + cpf + "-" + senha + "-" + saldo;
                        String dadosCriptografados = Vernam.cifrar(dados, chave.CHAVE_VERNAM);
                        dadosCriptografados = AES.cifrar(dadosCriptografados, chave.CHAVE_AES);

                        HMAC = ImplHMAC.gerarHMAC(dadosCriptografados, chave.CHAVE_HMAC);
                        assinatura = gerador.assinarMensagem(HMAC, assinador[1]);

                        try {
                            stub.cadastro(assinatura, ipCliente, dadosCriptografados);
                            System.out.println("Cadastro realizado com sucesso!");
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                        break;
                    case 2:
                        System.out.println("Digite o CPF: ");
                        cpf = scanner.nextLine();
                        System.out.println("Digite a senha: ");
                        senha = scanner.nextLine();

                        cpfCriptografado = Vernam.cifrar(cpf, chave.CHAVE_VERNAM);
                        cpfCriptografado = AES.cifrar(cpfCriptografado, chave.CHAVE_AES);
                        String senhaCriptografada = Vernam.cifrar(senha, chave.CHAVE_VERNAM);
                        senhaCriptografada = AES.cifrar(senhaCriptografada, chave.CHAVE_AES);

                        HMAC = ImplHMAC.gerarHMAC(cpfCriptografado, chave.CHAVE_HMAC);
                        assinatura = gerador.assinarMensagem(HMAC, assinador[1]);
//                    System.out.println(chave.CHAVE_HMAC);
//                    System.out.println(HMAC);
//                    System.out.println(assinatura);
//                    System.out.println(cpfCriptografado);
                        try {
                            String conta = stub.logar(assinatura, ipCliente, cpfCriptografado, senhaCriptografada);

                            if (conta != null) {
                                conta = AES.decifrar(conta, chave.CHAVE_AES);
                                conta = Vernam.decifrar(conta, chave.CHAVE_VERNAM);

                                autenticado = true;
                                nome = conta.split("-")[0];
                                cpf = conta.split("-")[1];
                            }else{
                                System.out.println("CPF ou senha inválidos, tente novamente.");
                            }
                            //   ipCliente = "9999";
                        } catch (RemoteException e) {
                            throw new RuntimeException(e);
                        }

                        break;
                }
            }
        }
    }
}