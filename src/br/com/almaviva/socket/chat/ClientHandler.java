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
    private String usuario;

    public ClientHandler(Socket socket) {
        this.clientSocket = socket;
        conectarClient();
    }

    private void conectarClient() {
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
            receberNomeUsuario();
            processarMensagens();
        } finally {
            encerrarConexoes();
        }
    }

    private void receberNomeUsuario() {
        try {
            usuario = recebeMensagem.readLine();
            System.out.println("Usuário [" + usuario + "] conectado.");
            enviaMensagem.println("Bem-vindo ao chat, [" + usuario + "]!");
            ChatServer.broadcast("Usuário " + usuario + " entrou no chat!", this);
        } catch (IOException e) {
            System.err.println("Erro ao receber o nome do usuário: " + e.getMessage());
        }
    }

    private void processarMensagens() {
        String entradaUsuario;
        try {
            while ((entradaUsuario = recebeMensagem.readLine()) != null) {
                if (entradaUsuario.equalsIgnoreCase("sair")) {
                    System.out.println("Usuário [" + usuario + "] saiu do chat.");
                    ChatServer.broadcast("Usuário " + usuario + " saiu do chat.", this);
                    break;
                }
                System.out.println("[" + usuario + "]: " + entradaUsuario);
                ChatServer.broadcast("[" + usuario + "]: " + entradaUsuario, this);
            }
        } catch (IOException e) {
            System.err.println("Erro ao processar mensagens do cliente [" + usuario + "]: " + e.getMessage());
        }
    }

    private void encerrarConexoes() {
        try {
            if (recebeMensagem != null) recebeMensagem.close();
            if (enviaMensagem != null) enviaMensagem.close();
            if (clientSocket != null) clientSocket.close();
        } catch (IOException e) {
            System.err.println("Erro ao encerrar conexões do cliente [" + usuario + "]: " + e.getMessage());
        }
    }

    public void enviarMensagem(String mensagem) {
        enviaMensagem.println(mensagem);
    }
}
