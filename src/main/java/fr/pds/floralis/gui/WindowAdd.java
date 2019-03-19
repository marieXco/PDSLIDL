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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.concurrent.TimeUnit;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

import org.json.JSONObject;
import org.postgresql.util.PGobject;

import fr.pds.floralis.commons.bean.entity.Patients;
import fr.pds.floralis.commons.bean.entity.Personnels;
import fr.pds.floralis.commons.bean.entity.Sensors;
import fr.pds.floralis.server.configurationpool.DataSource;
import fr.pds.floralis.server.configurationpool.JDBCConnectionPool;

public class WindowAdd extends JFrame implements ActionListener{
	private JDBCConnectionPool jdb;
	private Connection connect;

	private int LG = 900;
	private int HT = 120;

	JPanel container = new JPanel();
	JPanel otherInfosPanel = new JPanel();
	JPanel mainInfosPanel = new JPanel();

	JTextField brand = new JTextField(10);
	JLabel brandLabel = new JLabel("Marque :");

	JTextField macAddress = new JTextField(10);
	JLabel macAddressLabel = new JLabel("Adresse Mac :");

	JTextField dateInstallation = new JTextField(10);
	JLabel dateInstallationLabel = new JLabel("Date d'installation :");
	
	JComboBox day = null;
	
	String[] days = new String[32];
	
	JComboBox month = null;
	
	String[] months = new String[13]; 
	
	JComboBox year = null;
	
	String[] years = new String[12]; 
	
	JTextField caracteristics = new JTextField(30);
	JLabel caracteristicsLabel = new JLabel("Caractéristiques :");
	
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

	Button buttonAddPersonnel = new Button("Ajouter");
	Button buttonAddSensor = new Button("Ajouter");
	Button buttonAddPatient = new Button("Ajouter");

	JTextField resultSend = new JTextField(10);
	JTextPane newCode = new JTextPane();

	SimpleAttributeSet centrer = new SimpleAttributeSet();
	SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy");

	public WindowAdd(JDBCConnectionPool jdbc, Connection connection) {
		jdb = jdbc;
		connect = connection;		
	}

	public void initAddPersonnel() {
		StyleConstants.setAlignment(centrer,StyleConstants.ALIGN_CENTER); 

		newCode.setParagraphAttributes(centrer, true);    
		newCode.setText("Ajout d'un personnel");
		newCode.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));
		newCode.setOpaque(false);
		newCode.setEditable(false);
		newCode.setFocusable(false);

		buttonAddPersonnel.addActionListener(this);

		container.setPreferredSize(new Dimension(LG, HT));

		mainInfosPanel.add(lastnameLabel);
		mainInfosPanel.add(lastname);
		mainInfosPanel.add(nameLabel);
		mainInfosPanel.add(firstname);
		mainInfosPanel.add(fonctionLabel);
		mainInfosPanel.add(fonction);

		otherInfosPanel.add(usernameLabel);
		otherInfosPanel.add(username);

		otherInfosPanel.add(passwordLabel);
		otherInfosPanel.add(password);
		otherInfosPanel.add(codeLabel);
		otherInfosPanel.add(code);

		container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));

		container.add(BorderLayout.NORTH, mainInfosPanel);
		container.add(BorderLayout.NORTH, otherInfosPanel);	
		container.add(newCode);
		container.add(buttonAddPersonnel);

		this.addWindowListener(new WindowAdapter(){
			public void windowClosed(WindowEvent e){
				DataSource.backConnection(jdb, connect);
				System.out.println("Connexion fermée");
			}
		}); 

		this.setTitle("Floralis - Ajout d'un personnel");
		this.setContentPane(container);
		pack();
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.setVisible(true);
	}

	public void initAddSensor() {
		StyleConstants.setAlignment(centrer,StyleConstants.ALIGN_CENTER); 

		newCode.setParagraphAttributes(centrer, true);    
		newCode.setText("Ajout d'un capteur");
		newCode.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));
		newCode.setOpaque(false);
		newCode.setEditable(false);
		newCode.setFocusable(false);

		buttonAddSensor.addActionListener(this);
		
		days[0] = "Jour";
		
		months[0] = "Mois";
		
		years[0] = "Annee";
		
		for (int dayIndex = 1; dayIndex < days.length; dayIndex++) {
			String daysMax = (dayIndex) + "";
			days[dayIndex] = daysMax;
		}
		
		for (int monthIndex = 1; monthIndex < months.length; monthIndex++) {
			String monthMax = (monthIndex) + "";
			months[monthIndex] = monthMax;
		}	
		
		for (int yearIndex = 1; yearIndex < years.length; yearIndex++) {
			String yearMax = (yearIndex + 2018) + "";
			years[yearIndex] = yearMax;
		}
		
		day =  new JComboBox(days);
		
		month = new JComboBox(months);
		
		year = new JComboBox(years);	

		container.setPreferredSize(new Dimension(LG, HT));

		mainInfosPanel.add(brandLabel);
		mainInfosPanel.add(brand);
		mainInfosPanel.add(macAddressLabel);
		mainInfosPanel.add(macAddress);
		
		otherInfosPanel.add(dateInstallationLabel);
		otherInfosPanel.add(day);
		otherInfosPanel.add(month);
		otherInfosPanel.add(year);

		otherInfosPanel.add(caracteristicsLabel);
		otherInfosPanel.add(caracteristics);

		container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));

		container.add(BorderLayout.NORTH, mainInfosPanel);
		container.add(BorderLayout.NORTH, otherInfosPanel);	
		container.add(newCode);
		container.add(buttonAddSensor);

		this.addWindowListener(new WindowAdapter(){
			public void windowClosed(WindowEvent e){
				DataSource.backConnection(jdb, connect);
				System.out.println("Connexion fermée");
			}
		}); 

		this.setTitle("Floralis - Ajout d'un capteur");
		this.setContentPane(container);
		pack();
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.setVisible(true);
	}

	public void initAddPatient() {
		StyleConstants.setAlignment(centrer,StyleConstants.ALIGN_CENTER); 

		newCode.setParagraphAttributes(centrer, true);    
		newCode.setText("Ajout d'un patient");
		newCode.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));
		newCode.setOpaque(false);
		newCode.setEditable(false);
		newCode.setFocusable(false);

		buttonAddPatient.addActionListener(this);

		container.setPreferredSize(new Dimension(LG, HT));

		mainInfosPanel.add(lastnameLabel);
		mainInfosPanel.add(lastname);
		mainInfosPanel.add(nameLabel);
		mainInfosPanel.add(firstname);

		otherInfosPanel.add(codeLabel);
		otherInfosPanel.add(code);

		container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));

		container.add(BorderLayout.NORTH, mainInfosPanel);
		container.add(BorderLayout.NORTH, otherInfosPanel);	
		container.add(newCode);
		container.add(buttonAddPatient);

		this.addWindowListener(new WindowAdapter(){
			public void windowClosed(WindowEvent e){
				DataSource.backConnection(jdb, connect);
				System.out.println("Connexion fermée");
			}
		}); 

		this.setTitle("Floralis - Ajout d'un patient");
		this.setContentPane(container);
		pack();
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.setVisible(true);
	}


	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == buttonAddPersonnel) {
			newCode.setText("Ajout d'un personnel...");
			if (firstname.getText().isEmpty() || lastname.getText().isEmpty() || fonction.getText().isEmpty()
					|| username.getText().isEmpty() || password.getText().isEmpty() || code.getText().isEmpty()){
				newCode.setText("Un ou plusieurs champs sont manquants");
			}
			else {
				Personnels Personnel = new Personnels();
				Personnel.setFirstname(firstname.getText().toLowerCase());
				Personnel.setLastname(lastname.getText().toLowerCase());
				Personnel.setFonction(fonction.getText().toLowerCase());
				Personnel.setUsername(username.getText());
				Personnel.setPassword(password.getText());
				Personnel.setCode(Integer.parseInt(code.getText()));

				JSONObject obj = new JSONObject(Personnel);
				PGobject jsonObject = new PGobject();
				jsonObject.setType("json");

				try {
					jsonObject.setValue(obj.toString());
				} catch (SQLException e2) {
					e2.printStackTrace();
				}

				String databaseName = Personnel.getClass().getName().substring(4).toLowerCase();

				try {
					Insert json2 = new Insert(jdb, connect, databaseName, jsonObject);
					this.setVisible(false);
				} catch (SQLException e1) {
					e1.printStackTrace();
				} 
			}
		}

		if (e.getSource() == buttonAddPatient) {
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
					Insert json2 = new Insert(jdb, connect, databaseName, jsonObject);
					this.setVisible(false);
				} catch (SQLException e1) {
					e1.printStackTrace();
				} 
			}
		}

		if (e.getSource() == buttonAddSensor) {
			if (brand.getText().isEmpty() || macAddress.getText().isEmpty() || caracteristics.getText().isEmpty()){
				newCode.setText("Un ou plusieurs champs sont manquants");
			}
			else {
				Sensors sensor = new Sensors();
				sensor.setBrand(brand.getText());
				sensor.setMacAddress(macAddress.getText());
				sensor.setCaracteristics(caracteristics.getText());
				sensor.setAlert(false);
				sensor.setBreakdown(false);
				sensor.setState(false);
				
				//TODO vérifier ques les selectedIndex > 0, vérifier si foncitionne
				int dayInstallation = day.getSelectedIndex();
				int monthInstallation = month.getSelectedIndex()-1;
				int indexYear = year.getSelectedIndex();
				int yearInstallation = Integer.parseInt(years[indexYear]);
				
				
				Date dateInst = new Date(yearInstallation - 1900, monthInstallation, dayInstallation);
				sensor.setDateInstallation(dateInst);
				
				//TODO créer 3 combo box, jour / date / année
				//sensor.setDateInstallation(d2);

				JSONObject obj = new JSONObject(sensor);
				PGobject jsonObject = new PGobject();
				jsonObject.setType("json");

				try {
					jsonObject.setValue(obj.toString());
				} catch (SQLException e2) {
					e2.printStackTrace();
				}

				String databaseName = sensor.getClass().getName().substring(4).toLowerCase();

				try {
					Insert json2 = new Insert(jdb, connect, databaseName, jsonObject);
					this.setVisible(false);
				} catch (SQLException e1) {
					e1.printStackTrace();
				} 
			}
		}
	}
}
