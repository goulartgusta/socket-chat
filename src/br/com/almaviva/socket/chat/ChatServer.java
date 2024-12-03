package br.com.almaviva.socket.chat;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.CopyOnWriteArrayList;

public class ChatServer {
    private static CopyOnWriteArrayList<ClientHandler> clients = new CopyOnWriteArrayList<>();

    public static void main(String[] args) {
    	ConfigPropertiesChat.carregarConfigs();
        try (ServerSocket serverSocket = new ServerSocket(ConfigPropertiesChat.getPorta("server.porta"))) {
            System.out.println("Servidor Iniciado!");

            while (!serverSocket.isClosed()) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Novo usuário conectado: " + clientSocket);

                ClientHandler clientHandler = new ClientHandler(clientSocket);
                clients.add(clientHandler);
                new Thread(clientHandler).start();
            }
        } catch (IOException e) {
            System.err.println("Erro na inicialização do servidor: " + e.getMessage());
            e.printStackTrace();

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
