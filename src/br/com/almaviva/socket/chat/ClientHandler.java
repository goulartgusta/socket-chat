package br.com.almaviva.socket.chat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientHandler implements Runnable {
    private Socket clientSocket;
    private PrintWriter enviaMensagem;
    private BufferedReader recebeMensagem;

    public ClientHandler(Socket socket) {
        this.clientSocket = socket;
        try {
            recebeMensagem = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            enviaMensagem = new PrintWriter(clientSocket.getOutputStream(), true);
            
        } catch (IOException e) {
        	System.err.println("Erro na configuração de conexões do cliente: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            String usuario = recebeMensagem.readLine();
            System.out.println("Usuário [" + usuario + "] conectado.");
            enviaMensagem.println("Bem-vindo ao chat, [" + usuario + "]!");
            enviaMensagem.flush();
            ChatServer.broadcast("Usuário " + usuario + " entrou no chat!", this);

            String entradaUsuario;
            while ((entradaUsuario = recebeMensagem.readLine()) != null) {
                if (entradaUsuario.equalsIgnoreCase("sair")) {
                    System.out.println("Usuário [" + usuario + "] saiu do chat.");
                    ChatServer.broadcast("Usuário " + usuario + " saiu do chat.", this);
                    break;
                }
                System.out.println("[" + usuario + "]: " + entradaUsuario);
                ChatServer.broadcast("[" + usuario + "]: " + entradaUsuario, this);
            }
            recebeMensagem.close();
            enviaMensagem.close();
            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void enviarMensagem(String mensagem) {
        enviaMensagem.println(mensagem);
    }
}