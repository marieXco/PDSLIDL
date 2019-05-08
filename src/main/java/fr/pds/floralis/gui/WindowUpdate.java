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
	private int HT = 100;

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

	Sensor sensorFound;

	int sensorFoundLocationId;
	
	public WindowUpdate(String host, int port) throws HeadlessException {
		super();
		this.host = host;
		this.port = port;
	}

	public void initUpdatePatient(int id) throws SQLException {
		// TODO
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
		sensorFound =  objectMapper.readValue(sensorFoundJson.get("sensorFoundJson").toString(), Sensor.class);
		// End sensor Find By Id

		container.setPreferredSize(new Dimension(LG + 200, HT));

		mainInfosPanel.add(brandLabel);
		mainInfosPanel.add(brand);
		mainInfosPanel.add(macAddressLabel);
		mainInfosPanel.add(macAddress);

		// Adding of window parameters informations of found sensor
		brand.setText(sensorFound.getBrand());
		macAddress.setText(sensorFound.getMacAddress().trim());

		sensorFoundLocationId = sensorFound.getIdLocation();

		container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));

		container.add(BorderLayout.NORTH, mainInfosPanel);
		container.add(BorderLayout.NORTH, otherInfosPanel);
		container.add(infos);
		container.add(buttonUpdateSensor);

		this.setTitle("Floralis - Modification des seuils d'un capteur");
		this.setContentPane(container);
		pack();
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.setVisible(true);
	}


	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == buttonUpdateSensor) {
			
			if (brand.getText().isEmpty() || macAddress.getText().isEmpty()) {
				infos.setText("Un ou plusieurs champs sont manquants");
			} else {
				// Beginning of sensor Update
				sensorFound.setBrand(brand.getText().trim());
				sensorFound.setMacAddress(macAddress.getText().trim());

				JSONObject sensorUpdateJson = new JSONObject();
				sensorUpdateJson.put("id", sensorFound.getId());
				sensorUpdateJson.put("sensorToUpdate", sensorFound.toJSON());
				
				Request thirdRequest = new Request();
				thirdRequest.setType("UPDATE");
				thirdRequest.setEntity("SENSOR");
				thirdRequest.setFields(sensorUpdateJson);
				
				ConnectionClient ccSensorUpdate = new ConnectionClient(host, port, thirdRequest.toJSON().toString());
				ccSensorUpdate.run();
				// End sensorUpdate 

				this.setVisible(false);
			}
		}

		if (e.getSource() == buttonUpdatePatient) {
			// TODO 
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
