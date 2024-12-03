package br.com.almaviva.socket.jokenpo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class ChatClient {
    private static final String SERVER_ADDRESS = "192.168.208.73";
    private static final int SERVER_PORT = 1234;

    public static void main(String[] args) {
        try {
            Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
            System.out.println("Conexão ao chat bem sucedida! Para jogar jokenpo, digite 'jogar'. Para sair, digite 'sair'.");

            BufferedReader enviaMensagem = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter escreveMensagem = new PrintWriter(socket.getOutputStream(), true);

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        String respostaDoServer;
                        while ((respostaDoServer = enviaMensagem.readLine()) != null) {
                            System.out.println(respostaDoServer);
                        }
                    } catch (IOException e) {
                        System.err.println("Conexão interrompida pelo servidor ou erro de rede: " + e.getMessage());
                    }
                }
            }).start();


            Scanner sc = new Scanner(System.in);
            String entradaDoUsuario = "";
            while (!entradaDoUsuario.equalsIgnoreCase("sair")) {
                entradaDoUsuario = sc.nextLine();

                if (entradaDoUsuario.equalsIgnoreCase("sair")) {
                    System.out.println("Obrigado por participar do chat. Volte sempre!");
                    break;
                }
                escreveMensagem.println(entradaDoUsuario);
            }
        } catch (IOException e) {
            System.err.println("Erro ao conectar ao servidor... " + e.getMessage());
        }
    }
}
