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
    
	public static void main(String[] args) {
    	
        try {
            Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
        	System.out.println("Conexão ao chat bem sucedida! (para sair, digite 'sair'.) \n"+ "Por favor, digite seu nome:");
        	

            BufferedReader recebeMensagem = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter enviaMensagem = new PrintWriter(socket.getOutputStream(), true);
            
            new Thread(() -> {
                try {
                    String respostaDoServer;
                    while ((respostaDoServer = recebeMensagem.readLine()) != null) {
                        System.out.println(respostaDoServer);
                    }
                } catch (IOException e) {
                    System.err.println("Conexão interrompida..." + e.getMessage());
                }
            }).start();

            Scanner sc = new Scanner(System.in);
            String entradaDoUsuario = "";
            while (!entradaDoUsuario.equalsIgnoreCase("sair")) {
            	entradaDoUsuario = sc.next();
                
                if (entradaDoUsuario.equalsIgnoreCase("sair")) {
                    System.out.println("Obrigado por participar do chat, volte sempre!");  
                    break;
                }
                enviaMensagem.println(entradaDoUsuario);
                enviaMensagem.flush();
            }
        } catch (IOException e) {
            System.err.println("Erro ao conectar ao servidor... " + e.getMessage());
        }
    }
}