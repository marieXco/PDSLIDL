package fr.pds.floralis.server.service;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.sql.SQLException;

import fr.pds.floralis.server.configurationpool.DataSource;
import fr.pds.floralis.server.configurationpool.JDBCConnectionPool;

public class TimeServer {

	//On initialise des valeurs par défaut
	private int port = 2412;
	private String host = "127.0.0.1";
	private ServerSocket server = null;
	private boolean isRunning = true;


	public TimeServer(){
		try {
			server = new ServerSocket(port, 100, InetAddress.getByName(host));
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public TimeServer(String pHost, int pPort){
		host = pHost;
		port = pPort;
		try {
			server = new ServerSocket(port, 100, InetAddress.getByName(host));
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	//On lance notre serveur
	public void open() throws ClassNotFoundException, SQLException{
		JDBCConnectionPool jdbc = DataSource.createPool();
		//Toujours dans un thread à part vu qu'il est dans une boucle infinie
		Thread t = new Thread(new Runnable(){
			public void run(){
				while(isRunning == true){
					try {
						//On attend une connexion d'un client
						Socket client = server.accept();

						//Une fois reçue, on la traite dans un thread séparé
						System.out.println("Connexion cliente reçue.");                  
						Thread t = new Thread(new RequestHandler(client, jdbc, DataSource.getConnectionFromPool(jdbc)));
						t.start();

					} catch (IOException e) {
						e.printStackTrace();
					} catch (ClassNotFoundException e) {
						e.printStackTrace();
					} catch (SQLException e) {
						System.out.println("Aucune connexon est disponible, veuillez patienter");
						// TODO : adding this messsage on a window
						e.printStackTrace();
					}
				}

				try {
					server.close();
				} catch (IOException e) {
					e.printStackTrace();
					server = null;
				}
			}
		});

		t.start();
	}

	public void close(){
		isRunning = false;
	}   
}
