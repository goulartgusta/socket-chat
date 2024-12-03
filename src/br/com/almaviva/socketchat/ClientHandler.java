package br.com.almaviva.socketchat;

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
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            usuario = getUsuario();
            System.out.println("Usuário " + usuario + " conectado.");
            escreveMensagem.println("Bem-vindo ao chat, " + usuario + "!");

            String entradaUsuario;
            while ((entradaUsuario = enviaMensagem.readLine()) != null) {
                System.out.println("[" + usuario + "]: " + entradaUsuario);
                ChatServer.broadcast("[" + usuario + "]: " + entradaUsuario, this);
            }

            ChatServer.removerClient(this);
            enviaMensagem.close();
            escreveMensagem.close();
            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getUsuario() throws IOException {
        escreveMensagem.println("Digite seu nome de usuário:");
        return enviaMensagem.readLine();
    }

    public void enviarMensagem(String mensagem) {
        escreveMensagem.println(mensagem);
    }
}