package fr.pds.floralis.server.service;

import java.sql.SQLException;

import javax.swing.JFrame;
import javax.swing.JTextArea;



public class TestMain extends JFrame {
	public static JTextArea log;
	public static void main(String[] args) throws ClassNotFoundException, SQLException {
		TimeServer ts = new TimeServer();
		ts.open();

		JFrame jf = new JFrame();
		log = new JTextArea();

		jf.setVisible(true);

		prompt("Serveur initialisé.");
		


		jf.setBounds(0, 0, 300, 300);
		jf.setTitle("Floralis");
		jf.setLocationRelativeTo(null);

		// DISPOSE --> ne ferme pas, laisse la place à la fenêtre de
		// déconnection
		jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jf.setVisible(true);
	}
	
	public static void prompt(String msg) {
	    log.append(msg + "\n"); 
	}


}