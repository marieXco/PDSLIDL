package fr.pds.floralis.gui;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Dimension;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.sql.Date;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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

import org.json.JSONException;
import org.json.JSONObject;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import fr.pds.floralis.commons.bean.entity.Location;
import fr.pds.floralis.commons.bean.entity.Request;
import fr.pds.floralis.commons.bean.entity.Sensor;
import fr.pds.floralis.gui.connexion.ConnectionClient;

public class WindowUpdate extends JFrame implements ActionListener {
	// watch WindowConfirm for serialVersionUID
	private static final long serialVersionUID = 1700387838741895744L;

	private int LG = 700;
	private int HT = 120;

	// Creation of panels
	JPanel container = new JPanel();
	JPanel otherInfosPanel = new JPanel();
	JPanel mainInfosPanel = new JPanel();
	
	// Creation of all parameters necessary 
	// For update a personnel, a patient and a sensor
	
	// Parameters for sensors
	JTextField brand = new JTextField(10);
	JLabel brandLabel = new JLabel("Marque :");

	JTextField macAddress = new JTextField(10);
	JLabel macAddressLabel = new JLabel("Adresse Mac :");

	JTextField dateInstallation = new JTextField(10);
	JLabel dateInstallationLabel = new JLabel("Date d'installation :");

	JComboBox<Object> daysComboBox = new JComboBox<Object>();

	String[] daysTab = new String[32];

	JComboBox<Object> monthComboBox = new JComboBox<Object>();

	String[] monthsTab = new String[13];

	JComboBox<Object> yearComboBox = new JComboBox<Object>();

	String[] yearsTab = new String[12];
	
	JComboBox<Object> location = new JComboBox<Object>();

	JTextField min = new JTextField(5);
	JLabel minLabel = new JLabel("Seuil Min :");
	
	JTextField max = new JTextField(5);
	JLabel maxLabel = new JLabel("Seuil Max :");

	SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy");
	
	// Parameters for patient/personnel
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
	
	// Button to update a personnel, a sensor and a patient
	Button buttonUpdatePersonnel = new Button("Modifier");
	Button buttonUpdateSensor = new Button("Modifier");
	Button buttonUpdatePatient = new Button("Modifier");

	JTextField resultSend = new JTextField(10);
	JTextPane infos = new JTextPane();

	JSONObject obj = new JSONObject();

	SimpleAttributeSet centrer = new SimpleAttributeSet();
	private String host;
	private int port;
	protected int id;

	List<Location> locationsFoundList = new ArrayList<>();

	Location[] locationsFoundTab = null;

	

	int sensorFoundLocationId;
	
	public WindowUpdate(String host, int port) throws HeadlessException {
		super();
		this.host = host;
		this.port = port;
	}

	// TODO method to Update a patient
	public void initUpdatePatient(int id) throws SQLException {

	}
	
	// method to update a sensor
	@SuppressWarnings("deprecation")
	public void initUpdateSensor(int id) throws JsonParseException,
	JsonMappingException, IOException, JSONException, InterruptedException {
		setId(id);

		StyleConstants.setAlignment(centrer, StyleConstants.ALIGN_CENTER);

		infos.setParagraphAttributes(centrer, true);
		infos.setText("Modification d'un capteur");
		infos.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));
		infos.setOpaque(false);
		infos.setEditable(false);
		infos.setFocusable(false);

		buttonUpdateSensor.addActionListener(this);

		daysTab[0] = "Jour";

		monthsTab[0] = "Mois";

		yearsTab[0] = "Annee";

		// Beginning of sensor Find by id
		// Recovery of the sensor associate at the id in parameter
		JSONObject sensorIdFindById = new JSONObject();
		sensorIdFindById.put("id", getId());
		
		Request request = new Request();
		request.setType("FINDBYID");
		request.setEntity("SENSOR");
		request.setFields(sensorIdFindById);
		
		ConnectionClient ccSensorFindById = new ConnectionClient(host, port, request.toJSON().toString());
		ccSensorFindById.run();

		String retourSensorFindById = ccSensorFindById.getResponse();
		JSONObject sensorFoundJson = new JSONObject();
		sensorFoundJson.put("sensorFoundJson", retourSensorFindById);

		ObjectMapper objectMapper = new ObjectMapper();
		Sensor sensorFound =  objectMapper.readValue(
				sensorFoundJson.get("sensorFoundJson").toString(), Sensor.class);
		// End sensor Find By Id

		// Beginning of location Find All
		// To see WindowWorker lines 269
		findAllLocation fl = new findAllLocation(host, port);
		locationsFoundList = fl.findAll(false);

		String[] locationsComboBox = new String[locationsFoundList.size() + 1];
		locationsComboBox[0] = "--Localisation--";

		for (int listIndex = 0; listIndex < locationsFoundList.size(); listIndex++) {
			int tabIndex = listIndex + 1;
			locationsComboBox[tabIndex] = locationsFoundList.get(listIndex).getBuildingId() + " - " + locationsFoundList.get(listIndex).getRoomId() + " - " + locationsFoundList.get(listIndex).getFloorId();
		}
		// End location Find all
		
		// Ajouter des choses dans la comboBox
		location = new JComboBox<Object>(locationsComboBox);

		for (int dayIndex = 1; dayIndex < daysTab.length; dayIndex++) {
			String daysMax = (dayIndex) + "";
			daysTab[dayIndex] = daysMax;
		}

		for (int monthIndex = 1; monthIndex < monthsTab.length; monthIndex++) {
			String monthMax = (monthIndex) + "";
			monthsTab[monthIndex] = monthMax;
		}

		for (int yearIndex = 1; yearIndex < yearsTab.length; yearIndex++) {
			String yearMax = (yearIndex + 2018) + "";
			yearsTab[yearIndex] = yearMax;
		}

		daysComboBox = new JComboBox<Object>(daysTab);

		monthComboBox = new JComboBox<Object>(monthsTab);

		yearComboBox = new JComboBox<Object>(yearsTab);

		container.setPreferredSize(new Dimension(LG + 200, HT));

		mainInfosPanel.add(brandLabel);
		mainInfosPanel.add(brand);
		mainInfosPanel.add(macAddressLabel);
		mainInfosPanel.add(macAddress);
		mainInfosPanel.add(location);

		otherInfosPanel.add(dateInstallationLabel);
		otherInfosPanel.add(daysComboBox);
		otherInfosPanel.add(monthComboBox);
		otherInfosPanel.add(yearComboBox);

		otherInfosPanel.add(minLabel);
		otherInfosPanel.add(min);
		
		otherInfosPanel.add(maxLabel);
		otherInfosPanel.add(max);

		// Adding of window parameters informations of found sensor
		brand.setText(sensorFound.getBrand());
		macAddress.setText(sensorFound.getMacAddress().trim());
		daysComboBox.setSelectedIndex(sensorFound.getInstallation().getDate());
		monthComboBox.setSelectedIndex(sensorFound.getInstallation().getMonth() + 1);
		yearComboBox.setSelectedIndex(sensorFound.getInstallation().getYear() - 118);
		min.setText(sensorFound.getMin());
		max.setText(sensorFound.getMax());

		sensorFoundLocationId = sensorFound.getIdLocation();

		container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));

		container.add(BorderLayout.NORTH, mainInfosPanel);
		container.add(BorderLayout.NORTH, otherInfosPanel);
		container.add(infos);
		container.add(buttonUpdateSensor);

		this.addWindowListener(new WindowAdapter() {
			public void windowClosed(WindowEvent e) {
				//DataSource.backConnection(jdb, connect);
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
		if (e.getSource() == buttonUpdateSensor) {
			// To see WindowAdd line 459
/*			try {
				Integer.parseInt(identifiant.getText());
			} catch (java.lang.NumberFormatException ex) {
				infos.setText("L'identifiant ne peut contenir que des chiffres");
			}*/
			try {
				Integer.parseInt(min.getText());
				Integer.parseInt(max.getText());
			} catch (java.lang.NumberFormatException ex) {
				infos.setText("Les seuils ne peuvent contenir que des chiffres");
			}
			
			// Verification that the sill contains just number
			try {
				Integer.parseInt(min.getText());
				Integer.parseInt(max.getText());
			} catch (java.lang.NumberFormatException ex) {
				infos.setText("Les seuils ne peuvent contenir que des chiffres");
			}
			
			if (brand.getText().isEmpty() || macAddress.getText().isEmpty()
					|| min.getText().isEmpty() || max.getText().isEmpty() ) {
				infos.setText("Un ou plusieurs champs sont manquants");
			} 
			
			else if (location.getSelectedIndex() <= 0 ) {
				infos.setText("Veuillez selectionner une localisation valable");
			}
			
			// If min > max
			else if (Integer.parseInt(min.getText()) > Integer.parseInt(max.getText())) {
				infos.setText("La valeur minimum doit être inferieure à la valeur maximum");
			}
			else {
				// Beginning of sensor Update
				Sensor sensorUpdate = new Sensor();
				sensorUpdate.setBrand(brand.getText().trim());
				sensorUpdate.setMacAddress(macAddress.getText().trim());
				sensorUpdate.setMin(min.getText().trim());
				sensorUpdate.setMax(max.getText().trim());
				sensorUpdate.setIdLocation(locationsFoundList.get(location.getSelectedIndex()-1).getId());

				sensorUpdate.setId(getId());
				// For the moment, not alert, no breakdown
				sensorUpdate.setAlert(false);
				sensorUpdate.setBreakdown(false);
				// This button switch the state of the sensor selected in the comboBox
				sensorUpdate.setState(false);
				sensorUpdate.setConfigure(false);
				sensorUpdate.setIpAddress(null);
				sensorUpdate.setPort(null);

				int dayInstallation = daysComboBox.getSelectedIndex();
				int monthInstallation = monthComboBox.getSelectedIndex() - 1;
				int yearInstallation = Integer.parseInt(yearsTab[yearComboBox.getSelectedIndex()]);

				@SuppressWarnings("deprecation")
				Date dateInstallation = new Date(yearInstallation - 1900,
						monthInstallation, dayInstallation);

				sensorUpdate.setInstallation(dateInstallation);

				JSONObject sensorUpdateJson = new JSONObject();
				sensorUpdateJson.put("id", sensorUpdate.getId());
				sensorUpdateJson.put("sensorToUpdate", sensorUpdate.toJSON());
				
				Request thirdRequest = new Request();
				thirdRequest.setType("UPDATE");
				thirdRequest.setEntity("SENSOR");
				thirdRequest.setFields(sensorUpdateJson);
				
				ConnectionClient ccSensorUpdate = new ConnectionClient(host, port, thirdRequest.toJSON().toString());
				ccSensorUpdate.run();
				// End sensorUpdate 

				// Beginning of old Location Update
				// TODO : locationUpdate --> taking of the sensorId on the old location
				// Adding the sensorId on the new Location
				// End old location Update


				this.setVisible(false);
			}
		}

		if (e.getSource() == buttonUpdatePatient) {

		}
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getSensorFoundLocationId() {
		return sensorFoundLocationId;
	}

	public void setSensorFoundLocationId(int sensorFoundLocationId) {
		this.sensorFoundLocationId = sensorFoundLocationId;
	}


}
