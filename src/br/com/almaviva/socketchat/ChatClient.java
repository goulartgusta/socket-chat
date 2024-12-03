package br.com.almaviva.socketchat;

import java.io.*;
import java.net.*;
import java.util.Scanner;
import java.io.*;
import java.net.*;
import java.util.Scanner;


public class ChatClient {
	
    private static final String SERVER_ADDRESS = "192.168.15.8";
    private static final int SERVER_PORT = 1234;
    
	public static void main(String[] args) {
    	
        try {
            Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
        	System.out.println("Conexão ao Chat bem sucedida! para sair, digite 'sair'.");

            BufferedReader enviaMensagem = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter escreveMensagem = new PrintWriter(socket.getOutputStream(), true);

            new Thread(() -> {
                try {
                    String respostaDoServer;
                    while ((respostaDoServer = enviaMensagem.readLine()) != null) {
                        System.out.println(respostaDoServer);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();

            Scanner sc = new Scanner(System.in);
            String entradaDoUsuario = "";
            while (!entradaDoUsuario.equalsIgnoreCase("sair")) {
            	entradaDoUsuario = sc.nextLine();
                escreveMensagem.println(entradaDoUsuario);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
