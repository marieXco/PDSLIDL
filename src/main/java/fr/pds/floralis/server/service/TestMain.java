package fr.pds.floralis.server.service;

import java.sql.SQLException;
import java.util.logging.Logger;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextArea;

import fr.pds.floralis.gui.connexion.ConnectionClient;
import java.io.ByteArrayOutputStream;
import java.io.FileWriter;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;



public class TestMain extends JFrame {
	public static void main(String[] args) throws ClassNotFoundException, SQLException {
		String host = "127.0.0.1";

		int port = 2345;

		TimeServer ts = new TimeServer();
		ts.open();

		JFrame jf = new JFrame();

		jf.setVisible(true);

		System.out.println("Serveur initialisé.");


		jf.setBounds(0, 0, 300, 300);
		jf.setTitle("Floralis");
		jf.setLocationRelativeTo(null);

		// DISPOSE --> ne ferme pas, laisse la place à la fenêtre de
		// déconnection
		jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jf.setVisible(true);
	}


}