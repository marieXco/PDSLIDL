package fr.pds.floralis.server.service;

import java.sql.SQLException;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;

import fr.pds.floralis.gui.connexion.ConnectionClient;

public class TestMain {

	public static void main(String[] args) throws ClassNotFoundException, SQLException {

		String host = "127.0.0.1";
		int port = 2345;

		TimeServer ts = new TimeServer();
		ts.open();
		
		JFrame jf = new JFrame();
		jf.setVisible(true);
		
		System.out.println("Serveur initialisé.");
		
		jf.setBounds(0, 0, 300, 300);
		jf.add(new JLabel("Serveur initialisé"));
		jf.setTitle("Floralis");
		jf.setLocationRelativeTo(null);

		// DISPOSE --> ne ferme pas, laisse la place à la fenêtre de
		// déconnection
		jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jf.setVisible(true);
	}
}