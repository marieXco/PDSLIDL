package fr.pds.floralis.server.simulation;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

/** 
 * 
 * ConnectionClient
 * Link between the client and the server, between the GUIs and the Request Handler
 * @author alveslaura
 *
 */

public class ConnectionSimulation implements Runnable{

	private Socket connexion = null;
	private PrintWriter writer = null;
	private BufferedInputStream reader = null;

	private static int count = 0;
	private String name = "Client-";
	private String request;  
	private String response;

	public ConnectionSimulation(String request){
		name += ++count;
		this.request = request;
		try {
			connexion = new Socket("192.168.20.18", 2412);
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

			writer.print(getRequest());
			
			//TOUJOURS UTILISER flush() POUR ENVOYER RÉELLEMENT DES INFOS AU SERVEUR
			writer.flush();  

			System.out.println("\t * Request :" + getRequest());

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
	}

	//Méthode qui permet d'envoyer la commande demandée
	private String getRequest(){
		return request;
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