package br.com.almaviva.socket.jokenpo;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.CopyOnWriteArrayList;

public class ChatServer {
    static CopyOnWriteArrayList<ClientHandler> clients = new CopyOnWriteArrayList<>();

    public static void main(String[] args) {
        ConfigPropertiesChat.carregarConfigs();
        try {
            ServerSocket serverSocket = new ServerSocket(ConfigPropertiesChat.getPorta("server.porta"));
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

    public static void broadcast(String mensagem, ClientHandler donoDaMensagem) {
        for (ClientHandler client : clients) {
            if (client != donoDaMensagem) {
                client.enviarMensagem(mensagem);
            }
        }
    }

    public static void removerClient(ClientHandler client) throws IOException {
        clients.remove(client);
        String mensagem = "Usuário " + client.getUsuario() + " saiu do chat!";
        System.out.println(mensagem);
        broadcast(mensagem, null);
    }

    public static void jogarJokenpo(ClientHandler jogador1, ClientHandler jogador2) {
        try {
            jogador1.enviarMensagem("Digite um número para sua jogada (1-Pedra, 2-Papel ou 3-Tesoura):");
            String jogada1 = jogador1.receberMensagem();
            jogador2.enviarMensagem("Digite um número para sua jogada (1-Pedra, 2-Papel ou 3-Tesoura):");
            String jogada2 = jogador2.receberMensagem();

            String resultado = validarVencedor(jogada1, jogada2);

            jogador1.enviarMensagem("Resultado: " + resultado);
            jogador2.enviarMensagem("Resultado: " + resultado);
            broadcast("O jogo acabou! A jogada do grande duelo entre o " + jogador1.getUsuario() + " e " + jogador2.getUsuario() + " terminou. Resultado: " + resultado, null);

        } catch (IOException e) {
            System.err.println("Erro durante o jogo: " + e.getMessage());
        }
    }

    private static String validarVencedor(String jogada1, String jogada2) {
        if (jogada1.equalsIgnoreCase(jogada2)) {
            return "Empate!";
        }
        if ((jogada1.equalsIgnoreCase("1") && jogada2.equalsIgnoreCase("3")) ||
            (jogada1.equalsIgnoreCase("2") && jogada2.equalsIgnoreCase("1")) ||
            (jogada1.equalsIgnoreCase("3") && jogada2.equalsIgnoreCase("2"))) {
            return "Jogador 1 venceu!";
        } 
        if ((jogada2.equalsIgnoreCase("1") && jogada1.equalsIgnoreCase("3")) ||
                (jogada2.equalsIgnoreCase("2") && jogada1.equalsIgnoreCase("1")) ||
                (jogada2.equalsIgnoreCase("3") && jogada1.equalsIgnoreCase("2"))) {
                return "Jogador 2 venceu!";
            }
        
        return "Jogada inválida";
    }
}
