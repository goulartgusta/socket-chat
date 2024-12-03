package br.com.almaviva.socket.chat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class ChatClient {
    private static final String SERVER_ADDRESS = "192.168.208.73";
    private static final int SERVER_PORT = 1234;
    private static BufferedReader recebeMensagem;
    private static PrintWriter enviaMensagem;
    private static Scanner sc = new Scanner(System.in);
    private static Socket socket;

    public static void main(String[] args) {
        conectarServer();
        escolherNomeUsuario();
        iniciarThread();
        iniciarChat();
    }

    private static void conectarServer() {
        try {
            socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
            recebeMensagem = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            enviaMensagem = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException e) {
            System.err.println("Erro ao conectar ao servidor: " + e.getMessage());
        }
    }

    private static void escolherNomeUsuario() {
        System.out.print("Conexão ao chat bem-sucedida! (para sair, digite 'sair').\nPor favor, digite seu nome: ");
        String nomeUsuario = sc.nextLine();
        enviaMensagem.println(nomeUsuario);
        enviaMensagem.flush();
    }

    private static void iniciarThread() {
        new Thread(() -> {
            try {
                String respostaDoServer;
                while ((respostaDoServer = recebeMensagem.readLine()) != null) {
                    System.out.println(respostaDoServer);
                }
            } catch (IOException e) {
                System.err.println("Conexão interrompida: " + e.getMessage());
            }
        }).start();
    }

    private static void iniciarChat() {
        String entradaDoUsuario = "";
        while (!entradaDoUsuario.equalsIgnoreCase("sair")) {
            entradaDoUsuario = sc.nextLine();

            if (entradaDoUsuario.equalsIgnoreCase("sair")) {
                System.out.println("Saindo do chat...");
                enviaMensagem.println("sair");
                enviaMensagem.flush();
                sair();
                break;
            }
            enviaMensagem.println(entradaDoUsuario);
            enviaMensagem.flush();
        }
    }

    private static void sair() {
        try {
            if (sc != null) sc.close();
            if (enviaMensagem != null) enviaMensagem.close();
            if (recebeMensagem != null) recebeMensagem.close();
            if (socket != null) socket.close();
            System.out.println("Conexão encerrada com sucesso.");
        } catch (IOException e) {
            System.err.println("Erro ao encerrar conexões: " + e.getMessage());
        }
    }
}
