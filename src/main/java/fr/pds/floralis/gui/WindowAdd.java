package fr.pds.floralis.gui;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JDialog;
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

import fr.pds.floralis.commons.bean.entity.Building;
import fr.pds.floralis.commons.bean.entity.Floor;
import fr.pds.floralis.commons.bean.entity.Location;
import fr.pds.floralis.commons.bean.entity.Request;
import fr.pds.floralis.commons.bean.entity.Room;
import fr.pds.floralis.commons.bean.entity.Sensor;
import fr.pds.floralis.commons.bean.entity.TypeSensor;
import fr.pds.floralis.gui.connexion.ConnectionClient;

public class WindowAdd extends JFrame implements ActionListener {
	// watch WindowConfirm for serialVersionUID
	private static final long serialVersionUID = -5982857209357189773L;
	
	JDialog window = new JDialog();

	private int LG = 950;
	private int HT = 120;
	
	// Creation of panels
	JPanel container = new JPanel();
	JPanel otherInfosPanel = new JPanel();
	JPanel mainInfosPanel = new JPanel();


	// Creation of all parameters necessary 
	// For add a personnel, a patient, a sensor and a location
	
	JComboBox<Object> room = new JComboBox<Object>();

	JComboBox<Object> location = new JComboBox<Object>();
	
	JComboBox<Object> typeSensor = new JComboBox<Object>();

	JComboBox<Object> building = new JComboBox<Object>();

	JComboBox<Object> floor = new JComboBox<Object>();

	// Parameters for sensors
	JTextField brand = new JTextField(10);
	JLabel brandLabel = new JLabel("Marque :");

	JTextField macAddress = new JTextField(10);
	JLabel macAddressLabel = new JLabel("Adresse Mac :");
	
	// Parameters for patients/personnel
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

	// Button to add a personnel, a sensor, a patient and a location
	// To see lines 378 - 585 to the Listener of these button
	Button buttonAddPersonnel = new Button("Ajouter");
	Button buttonAddSensor = new Button("Ajouter");
	Button buttonAddPatient = new Button("Ajouter");
	Button buttonAddLocation = new Button("Ajouter");

	JTextField resultSend = new JTextField(10);
	JTextPane infos = new JTextPane();

	SimpleAttributeSet centrer = new SimpleAttributeSet();
	SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy");

	// Parameters for locations
	JTextField identifiant = new JTextField(10);
	JLabel identifiantLabel = new JLabel("Identifiant :");
	public final Object waitAdd = new Object();

	List<Floor> floorFoundList = new ArrayList<Floor>();

	List<Building> buildingsFoundList = new ArrayList<Building>();

	List<Room> roomsFoundList = new ArrayList<Room>();

	List<Location> locationsFoundList = new ArrayList<Location>();

	List<TypeSensor> typeSensorsFoundList = new ArrayList<TypeSensor>();
	
	Location[] locationsFoundTab = null;

	Building[] buildingsFoundTab;

	Room[] roomsFoundTab;

	Floor[] floorsFoundTab;

	ObjectMapper objectMapper = new ObjectMapper();
	
	//TODO delete
	int port = 1234;
	String host = "1921685353";

	
	// TODO method for the Pop-up to add a Personnel
	public void initAddPersonnel() {

	}
	
	// method for the Pop-up to add a sensor
	// You need location to add sensor
	public void initAddSensor() throws JsonParseException, JsonMappingException, JSONException, IOException, InterruptedException {
		StyleConstants.setAlignment(centrer, StyleConstants.ALIGN_CENTER);
		infos.setParagraphAttributes(centrer, true);
		infos.setText("Ajout d'un capteur");
		infos.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));
		infos.setOpaque(false);
		infos.setEditable(false);
		infos.setFocusable(false);

		infos.setText("L'identifiant ne peut contenir que des chiffres, il sera impossible de le changer");

		buttonAddSensor.addActionListener(this);
		
		//Beginning typeSensor
		FindAllTypeSensor tf = new FindAllTypeSensor(host,port);
		typeSensorsFoundList = tf.findAll();

		String[] typeSensorComboBox = new String[typeSensorsFoundList.size() + 1];
		typeSensorComboBox[0] = "--Type du capteur--";

		for (int listIndex = 0; listIndex < typeSensorsFoundList.size(); listIndex++) {
			int tabIndex = listIndex + 1;
			typeSensorComboBox[tabIndex] = typeSensorsFoundList.get(listIndex).getType();
		}
		
		typeSensor = new JComboBox<Object>(typeSensorComboBox);
		//End ccTypeSensorsFindAll 


		container.setPreferredSize(new Dimension(LG, HT));

		
		mainInfosPanel.add(typeSensor);
		mainInfosPanel.add(identifiantLabel);
		mainInfosPanel.add(identifiant);
		
		otherInfosPanel.add(brandLabel);
		otherInfosPanel.add(brand);
		otherInfosPanel.add(macAddressLabel);
		otherInfosPanel.add(macAddress);

		container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));

		container.add(BorderLayout.NORTH, mainInfosPanel);
		container.add(BorderLayout.NORTH, otherInfosPanel);
		container.add(infos);
		container.add(buttonAddSensor);

		window.setTitle("Floralis - Ajout d'un capteur");
		window.setContentPane(container);
		window.pack();
		window.setLocationRelativeTo(null);
		window.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		window.setVisible(true);
		window.setModal(true);
		

	}

	//TODO method for the Pop-up to add a Patient
	public void initAddPatient() {

	}
	
	// method for the Pop-up to add a location
	// You need location to add sensor
	public void initAddLocation() throws JsonParseException, JsonMappingException, IOException {
		StyleConstants.setAlignment(centrer, StyleConstants.ALIGN_CENTER);
		infos.setParagraphAttributes(centrer, true);
		infos.setText("Ajout d'une localisation");
		infos.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));
		infos.setOpaque(false);
		infos.setEditable(false);
		infos.setFocusable(false);
		infos.setText("L'identifiant ne peut contenir que des chiffres, il sera impossible de le changer");

		// Beginning of the Room Find All - to see WindowWorker lines 269-287
		Request request = new Request();
		request.setType("FINDALL");
		request.setEntity("ROOM");
		request.setFields(new JSONObject());
		
		ConnectionClient ccRoomFindAll = new ConnectionClient(host, port, request.toJSON().toString());
		ccRoomFindAll.run();

		String retourCcRoomFindAll = ccRoomFindAll.getResponse();
		JSONObject roomsFound = new JSONObject();
		roomsFound.put("roomsFound", retourCcRoomFindAll);

		roomsFoundTab =  objectMapper.readValue(
				roomsFound.getString("roomsFound").toString(), Room[].class);

		roomsFoundList = Arrays.asList(roomsFoundTab);

		String[] roomsComboBox = new String[roomsFoundList.size() + 1];
		roomsComboBox[0] = "-- Nom de la pièce --";

		for (int listIndex = 0; listIndex < roomsFoundList.size(); listIndex++) {
			int tabIndex = listIndex + 1;
			roomsComboBox[tabIndex] = roomsFoundTab[listIndex].getTypeRoom();
		}

		room = new JComboBox<Object>(roomsComboBox);
		// End of room find all

		// Beginning of Building Find All - to see WindowWorker lines 269-287
		Request secondRequest = new Request();
		secondRequest.setType("FINDALL");
		secondRequest.setEntity("BUILDING");
		secondRequest.setFields(new JSONObject());
		
		ConnectionClient ccBuildingFindAll = new ConnectionClient(host, port, secondRequest.toJSON().toString());
		ccBuildingFindAll.run();

		String retourCcBuildingFindAll = ccBuildingFindAll.getResponse();
		JSONObject buildingsFound = new JSONObject();
		buildingsFound.put("buildingsFound", retourCcBuildingFindAll);

		buildingsFoundTab =  objectMapper.readValue(
				buildingsFound.get("buildingsFound").toString(), Building[].class);

		buildingsFoundList = Arrays.asList(buildingsFoundTab);

		String[] buildingsComboBox = new String[buildingsFoundList.size() + 1];
		buildingsComboBox[0] = "-- Nom du batiment --";

		for (int listIndex = 0; listIndex < buildingsFoundList.size(); listIndex++) {
			int tabIndex = listIndex + 1;
			buildingsComboBox[tabIndex] = buildingsFoundTab[listIndex].getTypeBuilding();
		}

		building = new JComboBox<Object>(buildingsComboBox);
		// End building Find All

		// Beginning of Floor Find All - to see WindowWorker lines 269-287
		Request thirdRequest = new Request();
		thirdRequest.setType("FINDALL");
		thirdRequest.setEntity("FLOOR");
		thirdRequest.setFields(new JSONObject());
		
		ConnectionClient ccFloorFindAll = new ConnectionClient(host, port, thirdRequest.toJSON().toString());
		ccFloorFindAll.run();

		String retoursCcFloorFindAll = ccFloorFindAll.getResponse();
		JSONObject floorFound = new JSONObject();
		floorFound.put("floorFound", retoursCcFloorFindAll);


		floorsFoundTab =  objectMapper.readValue(
				floorFound.get("floorFound").toString(), Floor[].class);

		floorFoundList = Arrays.asList(floorsFoundTab);

		String[] floorsComboBox = new String[floorFoundList.size() + 1];
		floorsComboBox[0] = "-- Etage --";

		for (int listIndex = 0; listIndex < floorFoundList.size(); listIndex++) {
			int tabIndex = listIndex + 1;
			floorsComboBox[tabIndex] = floorsFoundTab[listIndex].getName();
		}

		floor = new JComboBox<Object>(floorsComboBox);
		// End Floor Find All

		buttonAddLocation.addActionListener(this);

		container.setPreferredSize(new Dimension(LG, HT - 80));

		mainInfosPanel.add(identifiantLabel);
		mainInfosPanel.add(identifiant);
		mainInfosPanel.add(room);
		mainInfosPanel.add(building);
		mainInfosPanel.add(floor);

		container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));

		container.add(BorderLayout.NORTH, mainInfosPanel);
		container.add(infos);
		container.add(buttonAddLocation);

		window.setTitle("Floralis - Ajout d'une localisation");
		window.setContentPane(container);
		window.pack();
		window.setLocationRelativeTo(null);
		window.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		window.setVisible(true);
		
	}

	// Listen of action on the button to add a personnel, a patient, a location or a sensor
	@SuppressWarnings("deprecation")
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == buttonAddPersonnel) {

		}

		if (e.getSource() == buttonAddPatient) {

		}

		if (e.getSource() == buttonAddLocation) {	
			try {
				Integer.parseInt(identifiant.getText());
			} catch (java.lang.NumberFormatException ex) {
				infos.setText("L'identifiant ne peut contenir que des chiffres");
			}
			
			// If the index is 0, so there are no selected informations
			if (building.getSelectedIndex() <= 0 || room.getSelectedIndex() <= 0 || floor.getSelectedIndex() <= 0) {
				infos.setText("Veuillez selectionner une localisation valide");
			}
			else {
				// Beginning of Location Find by Id
				// Verification that the location id not already used - to see WindowWorker lines 269-287
				JSONObject locationFindById = new JSONObject();
				// Recovery of the id in parameter
				locationFindById.put("id", Integer.parseInt(identifiant.getText()));

				Request request = new Request();
				request.setType("FINDBYID");
				request.setEntity("LOCATION");
				request.setFields(locationFindById);
				
				ConnectionClient ccLocationFindById = new ConnectionClient(host, port, request.toJSON().toString());
				ccLocationFindById.run();

				String retourLocationFindById = ccLocationFindById.getResponse();
				JSONObject locationFoundJson = new JSONObject();
				locationFoundJson.put("locationFoundJson", retourLocationFindById);

				ObjectMapper objectMapper = new ObjectMapper();
				// It is not a table anymore, it is a unique location
				Location locationFound;
				try {
					locationFound = objectMapper.readValue(
							locationFoundJson.get("locationFoundJson").toString(), Location.class);
					
					// If the search location doesn't exist, a empty location is return
					// The empty location is : { "id" : 0, brand : "null"...}
					// Verification that the id is not 0 
					// If the id is 0, so the id of new location is already used 
					if (locationFound.getId() != 0) {
						infos.setText("Une localisation a déja cet identifiant, veuillez le modifier");
					} 
					// End of location Find By Id
					
					else {
						
						// At the creation of the location, it is not already sensor, so it is a empty table of Id sensors
						List<Integer> locationSensors = new ArrayList<Integer>();

						// Creation of location with all informations then insertion is a JSON
						// Then insertion in the ccLocationCreate
						Location locationCreate = new Location();
						try {
							
							locationCreate.setSensorId(locationSensors);
							locationCreate.setId(Integer.parseInt(identifiant.getText()));
							locationCreate.setRoomId(roomsFoundTab[room.getSelectedIndex() - 1].getId());
							
							locationCreate.setFloorId(floorsFoundTab[floor.getSelectedIndex() - 1].getId());
							locationCreate.setBuildingId(buildingsFoundTab[building.getSelectedIndex() - 1].getId());

							JSONObject locationCreateJson = new JSONObject(locationCreate);
							
							Request secondRequest = new Request();
							secondRequest.setType("CREATE");
							secondRequest.setEntity("LOCATION");
							secondRequest.setFields(locationCreateJson);
							
							ConnectionClient ccLocationCreate = new ConnectionClient(host, port, secondRequest.toJSON().toString());
							ccLocationCreate.run();
							window.setVisible(false);
						} catch (JSONException e1) {
							e1.printStackTrace();
						}
					}
				} catch (JSONException | IOException e2) {
					e2.printStackTrace();
				}
			}
		}

		if (e.getSource() == buttonAddSensor) {
			// Verification that the id contains just number
			try {
				int idChoose = Integer.parseInt(identifiant.getText());
				if(idChoose <= 0) infos.setText("L'identifiant doit être posifif");
			} catch (java.lang.NumberFormatException ex) {
				infos.setText("L'identifiant ne peut contenir que des chiffres");
			}
			
			
			// Verification that the sill contains just number
			if (brand.getText().isEmpty() || macAddress.getText().isEmpty() || identifiant.getText().isEmpty()) {
				infos.setText("Un ou plusieurs champs sont manquants");
			}
			
			else if(typeSensor.getSelectedIndex() <= 0) {
				infos.setText("Veuillez selectionner un type de capteur valable");
			}
	
			else {
				// Beginning of sensor Find By Id, to see lines 384-412
				JSONObject sensorIdFindById = new JSONObject();
				sensorIdFindById.put("id", Integer.parseInt(identifiant.getText()));

				Request thirdRequest = new Request();
				thirdRequest.setType("FINDBYID");
				thirdRequest.setEntity("SENSOR");
				thirdRequest.setFields(sensorIdFindById);
				
				ConnectionClient ccSensorFindById = new ConnectionClient(host, port, thirdRequest.toJSON().toString());
				ccSensorFindById.run();

				String retourSensorFindById = ccSensorFindById.getResponse();
				JSONObject sensorFoundJson = new JSONObject();
				sensorFoundJson.put("sensorFoundJson", retourSensorFindById);

				ObjectMapper objectMapper = new ObjectMapper();
				try {
					Sensor sensorFound = objectMapper.readValue(
							sensorFoundJson.get("sensorFoundJson").toString(), Sensor.class);

					if (sensorFound.getId() != 0) {
						infos.setText("Un capteur a déja cet identifiant, veuillez le modifier");
					} 
					// End Sensor Find By Id

					else {
						// Beginning of sensorCreate
						// Creation of a sensor 
						// Recovery of all informations to insert
						Sensor sensorCreate = new Sensor();
						sensorCreate.setBrand(brand.getText().trim());
						sensorCreate.setType(typeSensorsFoundList.get(typeSensor.getSelectedIndex() - 1).getType().toString());
						sensorCreate.setMacAddress(macAddress.getText().trim());
						sensorCreate.setId(Integer.parseInt(identifiant.getText()));
						
						//To configure
						sensorCreate.setMin(0);
						sensorCreate.setMax(0);
						sensorCreate.setIdLocation(0);
						sensorCreate.setAlert(false);
						sensorCreate.setBreakdown(false);
						sensorCreate.setIpAddress(null);
						sensorCreate.setPort(null);
						sensorCreate.setState(false);
						sensorCreate.setInstallation(null);
						
						//The sensor is not configure
						sensorCreate.setConfigure(false);
						
						Request forthRequest = new Request();
						forthRequest.setType("CREATE");
						forthRequest.setEntity("SENSOR");
						forthRequest.setFields(sensorCreate.toJSON());
						
						ConnectionClient ccSensorCreate = new ConnectionClient(host, port, forthRequest.toJSON().toString());
						ccSensorCreate.run();
						// End sensor Create

						
						// TODO : problème : arraylist ne se mets pas à jours
						
						// Beginning of the location update
						// This function is here because
						// When you add sensor, you attribute a location to this sensor
						// So, it have to add the new sensors at the 'sensor id table' of this location
						// TODO : modifier l'ancienne localisation 
						window.setVisible(false);
						
					}

				} catch (JSONException | IOException e1) {
					e1.printStackTrace();
				}
			}
		}
	}

	public void run() {
		initAddPatient(); 
		try {
			initAddSensor();
		} catch (JSONException | IOException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


}
