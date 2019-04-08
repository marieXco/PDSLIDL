package fr.pds.floralis.gui.connexion;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class ConnectionClient implements Runnable{

	private Socket connexion = null;
	private PrintWriter writer = null;
	private BufferedInputStream reader = null;

	//Notre liste de commandes. Le serveur nous répondra différemment selon la commande utilisée.
	private static int count = 0;
	private String name = "Client-";
	private String table;  
	private String command;   
	private String response;
	private String parameters;
	private String close;

	public ConnectionClient(String host, int port, String table, String command, String parameters){
		name += ++count;
		this.table = table;
		this.command = command;
		this.parameters = parameters;
		try {
			connexion = new Socket(host, port);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	public void run(){
		try {

			writer = new PrintWriter(connexion.getOutputStream(), true);
			reader = new BufferedInputStream(connexion.getInputStream());
			//On envoie la commande au serveur

			String table = getTable();
			writer.println(table);

			String commande = getCommand();
			writer.println(commande);
			
			String parameters = getParameters();
			writer.println(parameters);

			writer.println("CLOSE");
			
			//TOUJOURS UTILISER flush() POUR ENVOYER RÉELLEMENT DES INFOS AU SERVEUR
			writer.flush();  

			System.out.println("Commande " + commande + " sur la table " + table + " envoyée au serveur");

			//On attend la réponse
			response = read();
			System.out.println("\t * " + name + " : Réponse reçue " + response.toString());
			
			connexion.close();

		} catch (IOException e1) {
			e1.printStackTrace();
		}

		try {
			Thread.currentThread();
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		// FIXME : gérer les close
		//writer.close();
	}

	private String getClose() {
		return close;
	}


	//Méthode qui permet d'envoyer la commande demandée
	private String getCommand(){
		return command;
	}

	private String getTable(){
		return table;
	}
	
	public String getParameters() {
		return parameters;
	}

	public String getResponse() {
		return response;
	}

	//Méthode pour lire les réponses du serveur
	private String read() throws IOException{      
		String response = "";
		int stream;
		byte[] b = new byte[4096];
		stream = reader.read(b);
		response = new String(b, 0, stream + 1);      
		return response;
	}   
}