package br.com.almaviva.socket.jokenpo;

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
			System.err.println("Erro na configuração de conexões do cliente: " + e);
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
				
				if ("jogar".equalsIgnoreCase(entradaUsuario)) {
					ClientHandler adversario = encontrarAdversario();
					if (adversario != null) {
						ChatServer.jogarJokenpo(this, adversario);
					} else {
						enviarMensagem("Nenhum adversário disponível no momento.");
					}
					
				} else if ("sair".equalsIgnoreCase(entradaUsuario)) {
					break;
					
				} else {
					System.out.println("[" + usuario + "]: " + entradaUsuario);
					ChatServer.broadcast("[" + usuario + "]: " + entradaUsuario, this);
				}
			}
		
		} catch (IOException e) {
			System.err.println("Erro ao processar mensagens do cliente [" + usuario + "]: " + e.getMessage());
		
		} finally {
			try {
				ChatServer.removerClient(this);
				if (enviaMensagem != null)
					enviaMensagem.close();
				if (escreveMensagem != null)
					escreveMensagem.close();
				if (clientSocket != null)
					clientSocket.close();
		
			} catch (IOException e) {
				System.err.println("Erro ao desconectar cliente [" + usuario + "]: " + e.getMessage());
			}
		}
	}

	public String getUsuario() throws IOException {
		escreveMensagem.println("Digite seu nome:");
		return enviaMensagem.readLine();
	}

	public void enviarMensagem(String mensagem) {
		escreveMensagem.println(mensagem);
	}

	public String receberMensagem() throws IOException {
		return enviaMensagem.readLine();
	}

	private ClientHandler encontrarAdversario() {
		for (ClientHandler client : ChatServer.clients) {
			if (client != this) {
				return client;
			}
		}
		return null;
	}
}