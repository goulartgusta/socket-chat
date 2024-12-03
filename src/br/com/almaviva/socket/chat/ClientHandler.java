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
	private String entradaUsuario;

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
			receberMensagem();
		} finally {
			encerrarConexoes();
		}
	}

	
	private void receberNomeUsuario()  {
		try {
			usuario = recebeMensagem.readLine();
			retornarNomeUsuario();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void retornarNomeUsuario() {
		System.out.println("Usuário [" + usuario + "] conectado.");
		enviaMensagem.println("Bem-vindo ao chat, [" + usuario + "]!");
		ChatServer.broadcast("Usuário " + usuario + " entrou no chat!", this);
		
	}
	
    private void receberMensagem()  {
		try {
			while ((entradaUsuario = recebeMensagem.readLine()) != null) {
				retornarMensagem();
				retornarDesconexaoDoUsuario();
			}
		} catch (IOException e) {
			System.err.println("Erro ao receber mensagem: " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	private void retornarMensagem() {
		System.out.println("[" + usuario + "]: " + entradaUsuario);
		ChatServer.broadcast("[" + usuario + "]: " + entradaUsuario, this);
		
	}

	private void retornarDesconexaoDoUsuario() {
		if (entradaUsuario.equalsIgnoreCase("sair")) {
			System.out.println("Usuário [" + usuario + "] saiu do chat.");
			ChatServer.broadcast("Usuário " + usuario + " saiu do chat.", this);
			
		}
		
	}
	
	private void encerrarConexoes() {
		try {
			if (recebeMensagem != null)
				recebeMensagem.close();
			if (enviaMensagem != null)
				enviaMensagem.close();
			if (clientSocket != null)
				clientSocket.close();
		} catch (IOException e) {
			System.err.println("Erro ao encerrar conexões do cliente [" + usuario + "]: " + e.getMessage());
		}
	}

	public void enviarMensagem(String mensagem) {
		enviaMensagem.println(mensagem);
	}

}