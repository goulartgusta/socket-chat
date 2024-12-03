package br.com.almaviva.socket.chat;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ChatServer {
    private static List<ClientHandler> clients = new ArrayList<>();
    private static ServerSocket serverSocket;

    public static void main(String[] args) {
        iniciarServer();
        receberClient();
    }

    private static void iniciarServer() {
        ConfigPropertiesChat.carregarConfigs();
        try {
            serverSocket = new ServerSocket(ConfigPropertiesChat.getPorta("server.porta"));
            System.out.println("Servidor Iniciado!");
        } catch (IOException e) {
            System.err.println("Erro na inicialização do servidor: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void receberClient() {
        while (!serverSocket.isClosed()) {
            try {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Novo usuário conectado: " + clientSocket);

                ClientHandler clientHandler = new ClientHandler(clientSocket);
                clients.add(clientHandler);
                new Thread(clientHandler).start();
            } catch (IOException e) {
                System.out.println("Erro ao conectar cliente ao servidor: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    public static void broadcast(String mensagem, ClientHandler remetente) {
        for (ClientHandler client : clients) {
            if (client != remetente) {
                client.enviarMensagem(mensagem);
            }
        }
    }
}
