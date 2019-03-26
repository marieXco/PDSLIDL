package fr.pds.floralis.gui;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

import org.json.JSONObject;
import org.postgresql.util.PGobject;

import fr.pds.floralis.commons.bean.entity.Patients;
import fr.pds.floralis.server.configurationpool.DataSource;
import fr.pds.floralis.server.configurationpool.JDBCConnectionPool;

public class WindowUpdate extends JFrame implements ActionListener{
	private JDBCConnectionPool jdb;
	private Connection connect;

	private int LG = 700;
	private int HT = 120;

	JPanel container = new JPanel();
	JPanel otherInfosPanel = new JPanel();
	JPanel mainInfosPanel = new JPanel();
	

	JTextField firstname = new JTextField(10);
	JLabel nameLabel = new JLabel("Prenom :");

	JTextField lastname = new JTextField(10);
	JLabel lastnameLabel = new JLabel("Nom :");

	JTextField fonction = new JTextField(10);
	JLabel fonctionLabel = new JLabel("Fonction :");

	JTextField username = new JTextField(10);
	JLabel usernameLabel = new JLabel("Username :");

	JPasswordField password = new JPasswordField(10);
	JLabel passwordLabel = new JLabel("Mot de passe :");

	JTextField code = new JTextField(10);
	JLabel codeLabel = new JLabel("Code :");

	Button buttonUpdatePersonnel = new Button("Modifier");
	Button buttonUpdatePatient = new Button("Modifier");

	JTextField resultSend = new JTextField(10);
	JTextPane newCode = new JTextPane();
	
	private List<Patients> patientData;
	
	SimpleAttributeSet centrer = new SimpleAttributeSet();
	private int id;

	public WindowUpdate(JDBCConnectionPool jdbc, Connection connection) {
		jdb = jdbc;
		connect = connection;		
	}

//	public void initUpdatePersonnel() {
//		StyleConstants.setAlignment(centrer,StyleConstants.ALIGN_CENTER); 
//
//		newCode.setParagraphAttributes(centrer, true);    
//		newCode.setText("Ajout d'un personnel");
//		newCode.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));
//		newCode.setOpaque(false);
//		newCode.setEditable(false);
//		newCode.setFocusable(false);
//
//		buttonUpdatePersonnel.addActionListener(this);
//
//		container.setPreferredSize(new Dimension(LG, HT));
//
//		mainInfosPanel.add(lastnameLabel);
//		mainInfosPanel.add(lastname);
//		mainInfosPanel.add(nameLabel);
//		mainInfosPanel.add(firstname);
//		mainInfosPanel.add(fonctionLabel);
//		mainInfosPanel.add(fonction);
//
//		otherInfosPanel.add(usernameLabel);
//		otherInfosPanel.add(username);
//
//		otherInfosPanel.add(passwordLabel);
//		otherInfosPanel.add(password);
//		otherInfosPanel.add(codeLabel);
//		otherInfosPanel.add(code);
//
//		container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
//
//		container.add(BorderLayout.NORTH, mainInfosPanel);
//		container.add(BorderLayout.NORTH, otherInfosPanel);	
//		container.add(newCode);
//		container.add(buttonUpdatePersonnel);
//
//		this.addWindowListener(new WindowAdapter(){
//			public void windowClosed(WindowEvent e){
//				DataSource.backConnection(jdb, connect);
//				System.out.println("Connexion fermée");
//			}
//		}); 
//
//		this.setTitle("Floralis - Ajout d'un personnel");
//		this.setContentPane(container);
//		pack();
//		this.setLocationRelativeTo(null);
//		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
//		this.setVisible(true);
//	}
//	
	public void initUpdatePatient(int id) throws SQLException {
		this.id = id;
		StyleConstants.setAlignment(centrer,StyleConstants.ALIGN_CENTER); 

		newCode.setParagraphAttributes(centrer, true);    
		newCode.setText("Modification des infos d'un patient");
		newCode.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));
		newCode.setOpaque(false);
		newCode.setEditable(false);
		newCode.setFocusable(false);

		Patients Patient = new Patients();
		String databaseName = Patient.getClass().getName().substring(4).toLowerCase();
		
		patientData = Selects.SelectPatientWithValues(jdb, connect, id);
		
		buttonUpdatePatient.addActionListener(this);

		container.setPreferredSize(new Dimension(LG, HT));

		mainInfosPanel.add(lastnameLabel);
		mainInfosPanel.add(lastname);
		mainInfosPanel.add(nameLabel);
		mainInfosPanel.add(firstname);

		otherInfosPanel.add(codeLabel);
		otherInfosPanel.add(code);
		
		firstname.setText(patientData.get(0).getFirstname());
		lastname.setText(patientData.get(0).getLastname());
		code.setText(String.valueOf(patientData.get(0).getCode()));

		container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));

		container.add(BorderLayout.NORTH, mainInfosPanel);
		container.add(BorderLayout.NORTH, otherInfosPanel);	
		container.add(newCode);
		container.add(buttonUpdatePatient);

		this.addWindowListener(new WindowAdapter(){
			public void windowClosed(WindowEvent e){
				DataSource.backConnection(jdb, connect);
				System.out.println("Connexion fermée");
			}
		}); 

		this.setTitle("Floralis - Modification d'un patient");
		this.setContentPane(container);
		pack();
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.setVisible(true);
	}
	
//	public void initAddSensor() {
//		StyleConstants.setAlignment(centrer,StyleConstants.ALIGN_CENTER); 
//
//		newCode.setParagraphAttributes(centrer, true);    
//		newCode.setText("Ajout d'un personnel");
//		newCode.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));
//		newCode.setOpaque(false);
//		newCode.setEditable(false);
//		newCode.setFocusable(false);
//
//		buttonAdd.addActionListener(this);
//
//		container.setPreferredSize(new Dimension(LG, HT));
//
//		mainInfosPanel.add(lastnameLabel);
//		mainInfosPanel.add(lastname);
//		mainInfosPanel.add(nameLabel);
//		mainInfosPanel.add(firstname);
//		mainInfosPanel.add(fonctionLabel);
//		mainInfosPanel.add(fonction);
//
//		otherInfosPanel.add(usernameLabel);
//		otherInfosPanel.add(username);
//
//		otherInfosPanel.add(passwordLabel);
//		otherInfosPanel.add(password);
//		otherInfosPanel.add(codeLabel);
//		otherInfosPanel.add(code);
//
//		container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
//
//		container.add(BorderLayout.NORTH, mainInfosPanel);
//		container.add(BorderLayout.NORTH, otherInfosPanel);	
//		container.add(newCode);
//		container.add(buttonAdd);
//
//		this.addWindowListener(new WindowAdapter(){
//			public void windowClosed(WindowEvent e){
//				DataSource.backConnection(jdb, connect);
//				System.out.println("Connexion fermée");
//			}
//		}); 
//
//		this.setTitle("Floralis - Ajout d'un personnel");
//		this.setContentPane(container);
//		pack();
//		this.setLocationRelativeTo(null);
//		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
//		this.setVisible(true);
//	}

	public void actionPerformed(ActionEvent e) {
//		if (e.getSource() == buttonUpdatePersonnel) {
//			newCode.setText("Ajout d'un personnel...");
//			if (firstname.getText().isEmpty() || lastname.getText().isEmpty() || fonction.getText().isEmpty()
//					|| username.getText().isEmpty() || password.getText().isEmpty() || code.getText().isEmpty()){
//				newCode.setText("Un ou plusieurs champs sont manquants");
//			}
//			else {
//				Personnels Personnel = new Personnels();
//				Personnel.setFirstname(firstname.getText().toLowerCase());
//				Personnel.setLastname(lastname.getText().toLowerCase());
//				Personnel.setFonction(fonction.getText().toLowerCase());
//				Personnel.setUsername(username.getText());
//				Personnel.setPassword(password.getText());
//				Personnel.setCode(Integer.parseInt(code.getText()));
//				
//				JSONObject obj = new JSONObject(Personnel);
//				PGobject jsonObject = new PGobject();
//				jsonObject.setType("json");
//				
//				try {
//					jsonObject.setValue(obj.toString());
//				} catch (SQLException e2) {
//					e2.printStackTrace();
//				}
//				
//				String databaseName = Personnel.getClass().getName().substring(4).toLowerCase();
//				
//				try {
//					Insert json2 = new Insert(databaseName, jsonObject);
//					this.setVisible(false);
//				} catch (SQLException e1) {
//					e1.printStackTrace();
//				} 
//			}
//		}
		
		if (e.getSource() == buttonUpdatePatient) {
			newCode.setText("Ajout d'un patient...");
			if (firstname.getText().isEmpty() || lastname.getText().isEmpty() || code.getText().isEmpty()){
				newCode.setText("Un ou plusieurs champs sont manquants");
			}
			else {
				Patients Patient = new Patients();
				Patient.setFirstname(firstname.getText());
				Patient.setLastname(lastname.getText());
				Patient.setCode(Integer.parseInt(code.getText()));
				
				
				JSONObject obj = new JSONObject(Patient);
				PGobject jsonObject = new PGobject();
				jsonObject.setType("json");
				
				try {
					jsonObject.setValue(obj.toString());
				} catch (SQLException e2) {
					e2.printStackTrace();
				}
				
				String databaseName = Patient.getClass().getName().substring(4).toLowerCase();
				
				try {
					Update.UpdateData(jdb, connect, databaseName, id, jsonObject);
					this.setVisible(false);
				} catch (SQLException e1) {
					e1.printStackTrace();
				} 
			}
		}
	}
}
