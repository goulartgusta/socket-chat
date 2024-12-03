package br.com.almaviva.socket.chat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientHandler implements Runnable {
    private Socket clientSocket;
    private PrintWriter escreveMensagem;
    private BufferedReader enviaMensagem;
    private String usuario;

    public ClientHandler(Socket socket) {
        this.clientSocket = socket;
        try {
            enviaMensagem = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            escreveMensagem = new PrintWriter(clientSocket.getOutputStream(), true);
        } catch (IOException e) {
        	System.err.println("Erro na configuração de conexões do cliente: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            usuario = getUsuario();
            System.out.println("Usuário [" + usuario + "] conectado.");
            escreveMensagem.println("Bem-vindo ao chat, [" + usuario + "]!");
            ChatServer.broadcast("Usuário " + usuario + " entrou no chat!", this);

            String entradaUsuario;
            while ((entradaUsuario = enviaMensagem.readLine()) != null) {                
                System.out.println("[" + usuario + "]: " + entradaUsuario);
                ChatServer.broadcast("[" + usuario + "]: " + entradaUsuario, this);
                if (enviaMensagem.readLine().equalsIgnoreCase("sair")) {
                    System.out.println("Obrigado por participar do chat, volte sempre!");  

                    ChatServer.removerClient(this);
                    break;
                }
            }
            
            enviaMensagem.close();
            escreveMensagem.close();
            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getUsuario() throws IOException {
        escreveMensagem.println("Digite seu nome:");
        String nomeUsuario = enviaMensagem.readLine();
               
        return nomeUsuario;
        
    }

    public void enviarMensagem(String mensagem) {
        escreveMensagem.println(mensagem);
    }
}