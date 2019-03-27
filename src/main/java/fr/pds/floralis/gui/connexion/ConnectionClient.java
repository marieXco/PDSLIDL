package fr.pds.floralis.gui.connexion;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Random;

public class ConnectionClient implements Runnable {


	// envoie requete à mon server en json

	private Socket connexion = null;
	private PrintWriter writer = null;
	private BufferedInputStream reader = null;

	// Notre liste de commandes. Le serveur nous répondra différemment selon la
	// commande utilisée.
	private String[] listCommands = { "CREATE", "FINDBYID", "FINDALL",
			"UPDATE", "DELETE" };
	private static int count = 0;
	private String name = "Client-";

	// passé dans le constructeur
	private static String objJSON;
	private String typeRequest;
	private String typeTable;

	// socket
	private int port = 2345;
	private String host = "127.0.0.1";

	public ConnectionClient(String objJSON, String typeRequest, String typeTable) {
		name += ++count;
		ConnectionClient.objJSON = objJSON;
		this.typeRequest = typeRequest;
		this.typeTable = typeRequest;
		try {
			connexion = new Socket(host, port);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	//appelé pour find all
	public ConnectionClient(String typeRequest, String typeTable) {
		name += ++count;
		this.typeRequest = typeRequest;
		this.typeTable = typeRequest;
		try {
			connexion = new Socket(host, port);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void run() {

		
		try {

			writer = new PrintWriter(connexion.getOutputStream(), true);
			reader = new BufferedInputStream(connexion.getInputStream());
			// On envoie la commande au serveur

			// String commande = getCommand();
			writer.write(typeRequest);
			// TOUJOURS UTILISER flush() POUR ENVOYER RÉELLEMENT DES INFOS
			// AU SERVEUR
			writer.flush();

			System.out.println("Commande " + typeRequest
					+ " envoyée au serveur");

			// On attend la réponse du server (RequestHandler)
			String response = read();
			System.out.println("\t * " + name + " : Réponse reçue " + response);
			
			writer.write(typeTable);
			// TOUJOURS UTILISER flush() POUR ENVOYER RÉELLEMENT DES INFOS
			// AU SERVEUR
			writer.flush();
			
			

			writer.write(objJSON); // on envoie la requete
			// TOUJOURS UTILISER flush() POUR ENVOYER RÉELLEMENT DES INFOS
			// AU SERVEUR
			writer.flush();

			if (typeRequest == "FINDBYID") {

				this.objJSON = read();

			}
			
			if (typeRequest == "FINDALL") {

				this.objJSON = read();

			}
		
			
			
			

		} catch (IOException e1) {
			e1.printStackTrace();
		}

		try {
			Thread.currentThread().sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		writer.write("CLOSE");
		writer.flush();
		writer.close();
	}

	

	// Méthode pour lire les réponses du serveur
	private String read() throws IOException {
		String response = "";
		int stream;
		byte[] b = new byte[4096];
		stream = reader.read(b);
		response = new String(b, 0, stream);
		return response;
	}

	public static String getobjJSON() {
		
		return objJSON;
	}
}
