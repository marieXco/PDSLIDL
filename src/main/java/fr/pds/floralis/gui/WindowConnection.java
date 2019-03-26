package fr.pds.floralis.gui;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

import fr.pds.floralis.server.configurationpool.DataSource;
import fr.pds.floralis.server.configurationpool.JDBCConnectionPool;

public class WindowConnection extends Thread implements ActionListener{
	private JDBCConnectionPool jdb;
	private Connection connect;

	public final Object valueWaitConnection = new Object();

	private int LG = 350;
	private int HT = 140;

	JFrame window = new JFrame();

	JPanel container = new JPanel();
	JPanel loginPanel = new JPanel();
	JPanel passwordPanel = new JPanel();

	JTextField login = new JTextField(10);
	JLabel loginLabel = new JLabel("Login :");

	JPasswordField password = new JPasswordField(10);
	JLabel passwordLabel = new JLabel("Mot de passe :");

	Button buttonSend = new Button("Connexion");

	JTextField resultSend = new JTextField(10);
	JTextPane forgotPassword = new JTextPane();

	SimpleAttributeSet centrer = new SimpleAttributeSet();
	
	public WindowConnection(JDBCConnectionPool jdbc, Connection connection) {
		jdb = jdbc;
		connect = connection;	
	}

	public void init() {	
		StyleConstants.setAlignment(centrer,StyleConstants.ALIGN_CENTER); 

		forgotPassword.setParagraphAttributes(centrer, true);    
		forgotPassword.setText("Si vous avez oublié votre mot de passe \nveuillez contacter un administrateur");
		forgotPassword.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));
		forgotPassword.setOpaque(false);
		forgotPassword.setEditable(false);
		forgotPassword.setFocusable(false);

		buttonSend.addActionListener(this);

		container.setPreferredSize(new Dimension(LG, HT));

		passwordPanel.add(passwordLabel);
		passwordPanel.add(password);

		loginPanel.add(loginLabel);
		loginPanel.add(login);

		container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));

		container.add(BorderLayout.NORTH, loginPanel);
		container.add(BorderLayout.NORTH, passwordPanel);
		container.add(forgotPassword);
		container.add(buttonSend);

		window.setTitle("Floralis - Connexion");
		window.setContentPane(container);
		window.pack();
		window.setLocationRelativeTo(null);
		window.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		window.setVisible(true);
		
		window.addWindowListener(new WindowAdapter(){
			public void windowClosed(WindowEvent e){
				DataSource.backConnection(jdb, connect);
				System.exit(0);
			}
		});
	}
	
	public void run() {
		init();
	}

	@SuppressWarnings("deprecation")
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == buttonSend) {
			if (login.getText().isEmpty() || password.getText().isEmpty()){
				forgotPassword.setText("Un ou plusieurs champs sont manquants");
			} 
			else { 
				ArrayList<?> toto = null;

				try {
					toto = Selects.SelectPersonnelForConnection(jdb, connect, login.getText(), password.getText());
				} catch (SQLException e2) {
					e2.printStackTrace();
				}

				try {
					TimeUnit.SECONDS.sleep(3);
				} catch (InterruptedException e2) {
					e2.printStackTrace();
				}

				if (toto.size() == 1) {			
					synchronized (valueWaitConnection) {
						window.setVisible(false);
						valueWaitConnection.notify();	
					}
				} 
				else {
					forgotPassword.setText("Un des champs n'est pas le bon,\nveuillez réessayer");
				} 
			}
			
		}
	}

}
