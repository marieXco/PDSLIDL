package fr.pds.floralis.server.service;

import fr.pds.floralis.gui.connexion.ConnectionClient;

public class TestMain {

	public static void main(String[] args) {

		String host = "127.0.0.1";
		int port = 2345;

		TimeServer ts = new TimeServer(host, port);
		ts.open();

		System.out.println("Serveur initialisé.");

		Thread t = new Thread(new ConnectionClient(host, port, "SENSOR", "FINDALL", null));
		t.start();

	}
}