package fr.pds.floralis.gui;

import java.sql.Connection;
import java.sql.SQLException;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import fr.pds.floralis.server.configurationpool.JDBCConnectionPool;

public class WindowConfirm extends JFrame{
	private JDBCConnectionPool jdb;
	private Connection connect;

	public WindowConfirm(JDBCConnectionPool jdbc, Connection connection) {
		jdb = jdbc;
		connect = connection;		
	}

	
	public void init(String phrase) {
		ImageIcon img = new ImageIcon("src/main/resources/images/info-icon.png");

		int input = JOptionPane.showConfirmDialog(null, "Voulez-vous vraiment " + phrase + " ?", "Confirmation",
				JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, img);
		
		
		System.out.println(input);  // 0=yes, 1=no, 2=cancel

		if (input == 0) {
			//Appeler la prochaine procédure avec les bons paramètres
		}
	}
	
	// TODO Rendre cela générique, uniquement pour les delete
	public void initDeletePatient(int id) throws SQLException {
		ImageIcon img = new ImageIcon("src/main/resources/images/info-icon.png");
		
		String databaseName = "patients";

		int input = JOptionPane.showConfirmDialog(null, "Voulez-vous vraiment supprimer ce patient ?", "Confirmation",
				JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, img);
		
		System.out.println(input);

		if (input == 0) {
			new Delete(jdb, connect, databaseName, id);
		}
	}
}
