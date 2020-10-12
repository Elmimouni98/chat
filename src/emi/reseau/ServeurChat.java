package emi.reseau;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ServeurChat extends Thread{
	private boolean isActive=true;
	private int nombreClient = 0;
	private List<Conversation> clients = new ArrayList<Conversation>();
	public static void main(String[]  args)
	{
		new ServeurChat().start();
	}
	
	@Override
	public void run()
	{
		try 
		{
			ServerSocket serverSocket = new ServerSocket(1234);
			while(isActive) {
				//Attendre une connexion du client
				Socket socket = serverSocket.accept();
				//Incrementer le nombre des clients
				++nombreClient;
				Conversation conversation = new Conversation(socket,nombreClient);
				clients.add(conversation);
				conversation.start();
			}
			
			serverSocket.close();	
		} 
		
		catch (IOException e) 
		{
			e.printStackTrace();
		}
		
	}

		class Conversation extends Thread
		{
			protected Socket socketClient;
			protected int numero;
			
			public Conversation(Socket socketClient, int numero) {
				super();
				this.socketClient = socketClient;
				this.numero = numero;
		}
			
		
			public void transfetToAll(String message,Socket socket)
			{
				try {
					for(Conversation client : clients)
					{
							if(client.socketClient != socket)
							{
									PrintWriter printWriter = new PrintWriter(client.socketClient.getOutputStream(),
											true);
									printWriter.println(message);
							}
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			
		public void transfetMessage(String message,Socket socket,int numeroC)
		{
			try {
				for(Conversation client : clients)
				{
						if(client.socketClient != socket)
						{
							if(client.numero == numeroC) {
								PrintWriter printWriter = new PrintWriter(client.socketClient.getOutputStream(),true);
								printWriter.println(message);
							}
						}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		@Override
		public void run() {
			try {
				InputStream inputStream = socketClient.getInputStream();
				InputStreamReader isr = new InputStreamReader(inputStream);
				BufferedReader bufferedReader = new BufferedReader(isr);
				
				PrintWriter printWriter = new PrintWriter(socketClient.getOutputStream(),true);
				String ipClient = socketClient.getRemoteSocketAddress().toString();
				printWriter.println("Bienvenue , vous etes le client numero : " + numero);
				System.out.println("Connexion du client numero : " + numero + " | IP Adresse : " + ipClient);
				
				while(true)
				{
					String req = bufferedReader.readLine();
					if(req.contains("=>"))
					{
						String[] reqParam = req.split("=>");
						if(reqParam.length == 2);
						String message = reqParam[1];
						String chaine = reqParam[0];
						int numeroC = Integer.parseInt(chaine);
						transfetMessage(message,socketClient,numeroC);
					}
					else
					{
						transfetToAll(req,socketClient);

					}
				}
				
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	}
}