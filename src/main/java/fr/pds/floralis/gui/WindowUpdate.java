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
import java.sql.Date;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

import org.json.JSONObject;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import fr.pds.floralis.commons.bean.entity.Patients;
import fr.pds.floralis.commons.bean.entity.Sensor;
import fr.pds.floralis.gui.connexion.ConnectionClient;
import fr.pds.floralis.server.configurationpool.DataSource;
import fr.pds.floralis.server.configurationpool.JDBCConnectionPool;

public class WindowUpdate extends JFrame implements ActionListener {
	private JDBCConnectionPool jdb;
	private Connection connect;

	private int LG = 700;
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

	SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy");

	JTextField identifiant = new JTextField(10);
	JLabel identifiantLabel = new JLabel("Identifiant :");

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
	Button buttonUpdateSensor = new Button("Modifier");
	Button buttonUpdatePatient = new Button("Modifier");

	JTextField resultSend = new JTextField(10);
	JTextPane infos = new JTextPane();

	private List<Patients> patientData;
	private List<Sensor> sensorData;

	JSONObject obj = new JSONObject();

	SimpleAttributeSet centrer = new SimpleAttributeSet();
	String host = "127.0.0.1";
	int port = 2345 ;
	private int id;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public WindowUpdate(JDBCConnectionPool jdbc, Connection connection) {
		jdb = jdbc;
		connect = connection;
	}

	// public void initUpdatePersonnel() {
	// StyleConstants.setAlignment(centrer,StyleConstants.ALIGN_CENTER);
	//
	// newCode.setParagraphAttributes(centrer, true);
	// newCode.setText("Ajout d'un personnel");
	// newCode.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));
	// newCode.setOpaque(false);
	// newCode.setEditable(false);
	// newCode.setFocusable(false);
	//
	// buttonUpdatePersonnel.addActionListener(this);
	//
	// container.setPreferredSize(new Dimension(LG, HT));
	//
	// mainInfosPanel.add(lastnameLabel);
	// mainInfosPanel.add(lastname);
	// mainInfosPanel.add(nameLabel);
	// mainInfosPanel.add(firstname);
	// mainInfosPanel.add(fonctionLabel);
	// mainInfosPanel.add(fonction);
	//
	// otherInfosPanel.add(usernameLabel);
	// otherInfosPanel.add(username);
	//
	// otherInfosPanel.add(passwordLabel);
	// otherInfosPanel.add(password);
	// otherInfosPanel.add(codeLabel);
	// otherInfosPanel.add(code);
	//
	// container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
	//
	// container.add(BorderLayout.NORTH, mainInfosPanel);
	// container.add(BorderLayout.NORTH, otherInfosPanel);
	// container.add(newCode);
	// container.add(buttonUpdatePersonnel);
	//
	// this.addWindowListener(new WindowAdapter(){
	// public void windowClosed(WindowEvent e){
	// DataSource.backConnection(jdb, connect);
	// System.out.println("Connexion fermée");
	// }
	// });
	//
	// this.setTitle("Floralis - Ajout d'un personnel");
	// this.setContentPane(container);
	// pack();
	// this.setLocationRelativeTo(null);
	// this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	// this.setVisible(true);
	// }
	//
	public void initUpdatePatient(int id) throws SQLException {
		StyleConstants.setAlignment(centrer, StyleConstants.ALIGN_CENTER);

		infos.setParagraphAttributes(centrer, true);
		infos.setText("Modification des infos d'un patient");
		infos.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));
		infos.setOpaque(false);
		infos.setEditable(false);
		infos.setFocusable(false);

		Patients Patient = new Patients();
		String databaseName = Patient.getClass().getName().substring(4)
				.toLowerCase();

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
		container.add(infos);
		container.add(buttonUpdatePatient);

		this.addWindowListener(new WindowAdapter() {
			public void windowClosed(WindowEvent e) {
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

	@SuppressWarnings("deprecation")
	public void initUpdateSensor(int id) throws JsonParseException,
	JsonMappingException, IOException {
		StyleConstants.setAlignment(centrer, StyleConstants.ALIGN_CENTER);

		infos.setParagraphAttributes(centrer, true);
		infos.setText("Modification d'un capteur");
		infos.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));
		infos.setOpaque(false);
		infos.setEditable(false);
		infos.setFocusable(false);

		buttonUpdateSensor.addActionListener(this);

		days[0] = "Jour";

		months[0] = "Mois";

		years[0] = "Annee";

		setId(id);
		
		JSONObject obj = new JSONObject();
		obj.put("id", getId());
		

		ConnectionClient cc = new ConnectionClient(host, port,"SENSOR",
				"FINDBYID", obj.toString());
		cc.run();

		String retour = cc.getResponse();
		JSONObject retourJson = new JSONObject();
		retourJson.put("sensor", retour);

		ObjectMapper objectMapper = new ObjectMapper();

		Sensor sensorFound =  objectMapper.readValue(
				retourJson.get("sensor").toString(), Sensor.class);

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

		day = new JComboBox(days);

		month = new JComboBox(months);

		year = new JComboBox(years);

		container.setPreferredSize(new Dimension(LG + 200, HT));

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

		brand.setText(sensorFound.getBrand());
		macAddress.setText(sensorFound.getMacAdress().trim());
		day.setSelectedIndex(sensorFound.getInstallation().getDate());
		month.setSelectedIndex(sensorFound.getInstallation().getMonth() + 1);
		year.setSelectedIndex(sensorFound.getInstallation().getYear() - 118);
		caracteristics.setText(sensorFound.getCaracteristics());

		container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));

		container.add(BorderLayout.NORTH, mainInfosPanel);
		container.add(BorderLayout.NORTH, otherInfosPanel);
		container.add(infos);
		container.add(buttonUpdateSensor);

		this.addWindowListener(new WindowAdapter() {
			public void windowClosed(WindowEvent e) {
				DataSource.backConnection(jdb, connect);
				System.out.println("Connexion fermée");
			}
		});

		this.setTitle("Floralis - Modification d'un capteur");
		this.setContentPane(container);
		pack();
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.setVisible(true);
	}

	public void actionPerformed(ActionEvent e) {
		// if (e.getSource() == buttonUpdatePersonnel) {
		// newCode.setText("Ajout d'un personnel...");
		// if (firstname.getText().isEmpty() || lastname.getText().isEmpty() ||
		// fonction.getText().isEmpty()
		// || username.getText().isEmpty() || password.getText().isEmpty() ||
		// code.getText().isEmpty()){
		// newCode.setText("Un ou plusieurs champs sont manquants");
		// }
		// else {
		// Personnels Personnel = new Personnels();
		// Personnel.setFirstname(firstname.getText().toLowerCase());
		// Personnel.setLastname(lastname.getText().toLowerCase());
		// Personnel.setFonction(fonction.getText().toLowerCase());
		// Personnel.setUsername(username.getText());
		// Personnel.setPassword(password.getText());
		// Personnel.setCode(Integer.parseInt(code.getText()));
		//
		// JSONObject obj = new JSONObject(Personnel);
		// PGobject jsonObject = new PGobject();
		// jsonObject.setType("json");
		//
		// try {
		// jsonObject.setValue(obj.toString());
		// } catch (SQLException e2) {
		// e2.printStackTrace();
		// }
		//
		// String databaseName =
		// Personnel.getClass().getName().substring(4).toLowerCase();
		//
		// try {
		// Insert json2 = new Insert(databaseName, jsonObject);
		// this.setVisible(false);
		// } catch (SQLException e1) {
		// e1.printStackTrace();
		// }
		// }
		// }

		if (e.getSource() == buttonUpdateSensor) {
			try {
				Integer.parseInt(identifiant.getText());
			} catch (java.lang.NumberFormatException ex) {
				infos.setText("L'identifiant ne peut contenir que des chiffres");
			}

			infos.setText("Modification d'un capteur...");
			if (brand.getText().isEmpty() || macAddress.getText().isEmpty()
					|| caracteristics.getText().isEmpty()) {
				infos.setText("Un ou plusieurs champs sont manquants");
			} else {

				Sensor sensor = new Sensor();
				sensor.setBrand(brand.getText().trim());
				sensor.setMacAdress(macAddress.getText().trim());
				sensor.setCaracteristics(caracteristics.getText().trim());

				sensor.setId(getId());
				sensor.setAlerts(null);
				sensor.setBreakdowns(null);
				sensor.setState(true);

				int dayInstallation = day.getSelectedIndex();
				int monthInstallation = month.getSelectedIndex() - 1;
				int indexYear = year.getSelectedIndex();
				int yearInstallation = Integer.parseInt(years[indexYear]);

				Date dateInst = new Date(yearInstallation - 1900,
						monthInstallation, dayInstallation);

				sensor.setInstallation(dateInst);

				JSONObject obj = new JSONObject(sensor);

				ConnectionClient cc = new ConnectionClient(host, port, "SENSOR", "UPDATE", obj.toString());
				cc.run();

				this.setVisible(false);
			}
		}

		if (e.getSource() == buttonUpdatePatient) {
			// infos.setText("Modification d'un patient...");
			// if (firstname.getText().isEmpty() || lastname.getText().isEmpty()
			// || code.getText().isEmpty()){
			// infos.setText("Un ou plusieurs champs sont manquants");
			// }
			// else {
			// Patients Patient = new Patients();
			// Patient.setFirstname(firstname.getText());
			// Patient.setLastname(lastname.getText());
			// Patient.setCode(Integer.parseInt(code.getText()));
			//
			//
			// JSONObject obj = new JSONObject(Patient);
			// PGobject jsonObject = new PGobject();
			// jsonObject.setType("json");
			//
			// try {
			// jsonObject.setValue(obj.toString());
			// } catch (SQLException e2) {
			// e2.printStackTrace();
			// }
			//
			// String databaseName =
			// Patient.getClass().getName().substring(4).toLowerCase();
			//
			// try {
			// Update.UpdateData(jdb, connect, databaseName, id, jsonObject);
			// this.setVisible(false);
			// } catch (SQLException e1) {
			// e1.printStackTrace();
			// }
			// }
		}
	}
}
