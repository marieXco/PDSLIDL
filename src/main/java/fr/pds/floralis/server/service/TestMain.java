package fr.pds.floralis.server.service;

import fr.pds.floralis.gui.connexion.ConnectionClient;

public class TestMain {

	public static void main(String[] args) {

		String host = "127.0.0.1";

		int port = 2345;

		for (int i = 0; i < 3; i++) {

			Thread t = new Thread(new ConnectionClient(host, port));

			t.start();

		}

	}

}
