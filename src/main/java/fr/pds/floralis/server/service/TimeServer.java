package fr.pds.floralis.server.service;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

public class TimeServer {

	// On initialise des valeurs par défaut

	private int port = 2345 ; 

	private String host = "127.0.0.1";

	private ServerSocket server = null;

	private boolean isRunning = true;

	public TimeServer() {
		System.out.println("constructeur");
		try {

			server = new ServerSocket(port, 100, InetAddress.getByName(host));

		} catch (UnknownHostException e) {
			System.out.println("unknow");
			e.printStackTrace();

		} catch (IOException e) {
			System.out.println("autre");
			e.printStackTrace();

		}

	}

	public TimeServer(String pHost, int pPort) {
		System.out.println("constructeur");
		host = pHost;

		port = pPort;

		try {
			//ERROR
			server = new ServerSocket(port, 100, InetAddress.getByName(host));

		} catch (UnknownHostException e) {
			System.out.println("catch unknow");
			e.printStackTrace();

		} catch (IOException e) {
			System.out.println("catch IOex");
			e.printStackTrace();

		}

	}

	// On lance notre serveur

	public void open() {

		// Toujours dans un thread à part vu qu'il est dans une boucle infinie

		Thread t = new Thread(new Runnable() {

			public void run() {

				while (isRunning == true) {

					try {

						// On attend une connexion d'un client
						//ERROR
						Socket client = server.accept();

						// Une fois reçue, on la traite dans un thread séparé

						System.out.println("Connexion cliente reçue.");

						Thread t = new Thread(new RequestHandler(client));

						t.start();

					} catch (IOException e) {

						System.out.println("catch ici");
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

	public void close() {

		isRunning = false;

	}

}
