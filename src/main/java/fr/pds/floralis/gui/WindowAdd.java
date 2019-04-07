package fr.pds.floralis.gui;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Dimension;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
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

import fr.pds.floralis.commons.bean.entity.Building;
import fr.pds.floralis.commons.bean.entity.Floor;
import fr.pds.floralis.commons.bean.entity.Location;
import fr.pds.floralis.commons.bean.entity.Room;
import fr.pds.floralis.commons.bean.entity.Sensor;
import fr.pds.floralis.gui.connexion.ConnectionClient;

public class WindowAdd extends JFrame implements ActionListener {
	// watch WindowConfirm for serialVersionUID
	private static final long serialVersionUID = -5982857209357189773L;

	private String host;
	private int port;

	private int LG = 950;
	private int HT = 180;

	JPanel container = new JPanel();
	JPanel otherInfosPanel = new JPanel();
	JPanel mainInfosPanel = new JPanel();
	JPanel locationPanel = new JPanel();

	JComboBox room = null;

	JComboBox location = null;

	JComboBox building = null;

	JComboBox floor = null;

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
	Button buttonAddLocation = new Button("Ajouter");

	JTextField resultSend = new JTextField(10);
	JTextPane infos = new JTextPane();

	SimpleAttributeSet centrer = new SimpleAttributeSet();
	SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy");

	JTextField identifiant = new JTextField(10);
	JLabel identifiantLabel = new JLabel("Identifiant :");
	public final Object waitAdd = new Object();

	List<?> floorFoundList = new ArrayList();

	List<?> buildingsFoundList = new ArrayList();

	List<?> roomsFoundList = new ArrayList();

	List<?> locationsFoundList = new ArrayList();

	Location[] locationsFoundTab = null;

	Building[] buildingsFoundTab;

	Room[] roomsFoundTab;

	Floor[] floorsFoundTab;

	ObjectMapper objectMapper = new ObjectMapper();


	public WindowAdd(String host, int port) throws HeadlessException {
		super();
		this.host = host;
		this.port = port;
	}

	public void initAddPersonnel() {

	}

	public void initAddSensor() throws JsonParseException, JsonMappingException, JSONException, IOException {
		StyleConstants.setAlignment(centrer, StyleConstants.ALIGN_CENTER);
		infos.setParagraphAttributes(centrer, true);
		infos.setText("Ajout d'un capteur");
		infos.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));
		infos.setOpaque(false);
		infos.setEditable(false);
		infos.setFocusable(false);

		infos.setText("L'identifiant ne peut contenir que des chiffres, il sera impossible de le changer");

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

		day = new JComboBox(days);

		month = new JComboBox(months);

		year = new JComboBox(years);

		// Voir WindowWorker lignes 269-287
		ConnectionClient ccLocationFindAll = new ConnectionClient(host, port, "LOCATION", "FINDALL", null);
		ccLocationFindAll.run();

		String retourCcLocationFindAll = ccLocationFindAll.getResponse();
		JSONObject locationsFound = new JSONObject();	
		locationsFound.put("locationsFound", retourCcLocationFindAll);

		locationsFoundTab =  objectMapper.readValue(
				locationsFound.get("locationsFound").toString(), Location[].class);

		locationsFoundList = Arrays.asList(locationsFoundTab);

		String[] locationsComboBox = new String[locationsFoundList.size() + 1];
		locationsComboBox[0] = "--Localisation--";

		for (int listIndex = 0; listIndex < locationsFoundList.size(); listIndex++) {
			int tabIndex = listIndex + 1;
			locationsComboBox[tabIndex] = locationsFoundTab[listIndex].getBuilding().getTypeBuilding() + " - " + locationsFoundTab[listIndex].getRoom().getTypeRoom() + " - " + locationsFoundTab[listIndex].getFloor().getName();
		}

		location = new JComboBox<Object>(locationsComboBox);
		// Fin ccLocationFindAll

		container.setPreferredSize(new Dimension(LG, HT));

		mainInfosPanel.add(brandLabel);
		mainInfosPanel.add(brand);
		mainInfosPanel.add(macAddressLabel);
		mainInfosPanel.add(macAddress);
		mainInfosPanel.add(identifiantLabel);
		mainInfosPanel.add(identifiant);

		otherInfosPanel.add(dateInstallationLabel);
		otherInfosPanel.add(day);
		otherInfosPanel.add(month);
		otherInfosPanel.add(year);

		otherInfosPanel.add(caracteristicsLabel);
		otherInfosPanel.add(caracteristics);

		locationPanel.add(location);

		container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));

		container.add(BorderLayout.NORTH, mainInfosPanel);
		container.add(BorderLayout.NORTH, otherInfosPanel);
		container.add(BorderLayout.NORTH, locationPanel);
		container.add(infos);
		container.add(buttonAddSensor);

		this.setTitle("Floralis - Ajout d'un capteur");
		this.setContentPane(container);
		pack();
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.setVisible(true);
	}

	public void initAddPatient() {

	}

	public void initAddLocation() throws JsonParseException, JsonMappingException, IOException {
		StyleConstants.setAlignment(centrer, StyleConstants.ALIGN_CENTER);
		infos.setParagraphAttributes(centrer, true);
		infos.setText("Ajout d'une localisation");
		infos.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));
		infos.setOpaque(false);
		infos.setEditable(false);
		infos.setFocusable(false);
		infos.setText("L'identifiant ne peut contenir que des chiffres, il sera impossible de le changer");

		// Début de Room Find All - voir WindowWorker lignes 269-287
		ConnectionClient ccRoomFindAll = new ConnectionClient(host, port, "ROOM", "FINDALL", null);
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
		// Fin de room find all

		// Début de Building Find All - voir WindowWorker lignes 269-287
		ConnectionClient ccBuildingFindAll = new ConnectionClient(host, port, "BUILDING", "FINDALL", null);
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
		// Fin building Find All

		// Début de Floor Find All - voir WindowWorker lignes 269-287
		ConnectionClient ccFloorFindAll = new ConnectionClient(host, port, "FLOOR", "FINDALL", null);
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
		// Fin Floor Find All

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

		this.setTitle("Floralis - Ajout d'une localisation");
		this.setContentPane(container);
		pack();
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.setVisible(true);
	}

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

			// Si l'index est à 0, c'est qu'aucun vraie information n'a été selectionnée
			if (building.getSelectedIndex() <= 0 || room.getSelectedIndex() <= 0 || floor.getSelectedIndex() <= 0) {
				infos.setText("Veuillez selectionner une localisation valide");
			}
			else {

				// Début de location Find By Id, ici, on vérifie que l'identifiant pour 
				// la nouvelle localisation n'est pas déja utilisé (voir WindowWorker lignes 269-287)
				JSONObject locationFindById = new JSONObject();
				// On récupère l'id rentré en paramètre
				locationFindById.put("id", Integer.parseInt(identifiant.getText()));

				ConnectionClient ccLocationFindById = new ConnectionClient(host, port,"LOCATION",
						"FINDBYID", locationFindById.toString());
				ccLocationFindById.run();

				String retourLocationFindById = ccLocationFindById.getResponse();
				JSONObject locationFoundJson = new JSONObject();
				locationFoundJson.put("locationFoundJson", retourLocationFindById);

				ObjectMapper objectMapper = new ObjectMapper();
				// On ne trouve plus un tableau mais une localisation unique
				Location locationFound;
				try {
					locationFound = objectMapper.readValue(
							locationFoundJson.get("locationFoundJson").toString(), Location.class);
					
					// Si la localisation cherchée n'existe pas, une location vide est renvoyée 
					// C'est à dire { "id" : 0, brand : "null"...}
					// On vérifie si l'id est différent de zéro, si oui, c'est que l'id de la 
					// nouvelle localisation est déja pris
					if (locationFound.getId() != 0) {
						infos.setText("Une localisation a déja cet identifiant, veuillez le modifier");
					} 
					// Fin de location Find By Id
					
					else {
						// On créer un building, une room et un floor et on y met les infos des comboBox
						// - 1 sur les index car l'index 0 est vide, voir window Worker avec la ComboBox des Id des capteurs
						Building locationBuilding = new Building();
						locationBuilding.setId(buildingsFoundTab[building.getSelectedIndex() - 1].getId());
						locationBuilding.setTypeBuilding(buildingsFoundTab[building.getSelectedIndex() - 1].getTypeBuilding());

						Room locationRoom = new Room();
						locationRoom.setId(roomsFoundTab[room.getSelectedIndex() - 1].getId());
						locationRoom.setTypeRoom(roomsFoundTab[room.getSelectedIndex() - 1].getTypeRoom());

						Floor locationFloor = new Floor();
						locationFloor.setId(floorsFoundTab[floor.getSelectedIndex() - 1].getId());
						locationFloor.setName(floorsFoundTab[floor.getSelectedIndex() - 1].getName());
						
						// En créant la localisation, elle n'a pas encore de capteur attitrée donc on lui met un tableau 
						// vide de capteursId 
						int[] locationSensors = new int[0];

						// On créer une localisation avec le tout puis on l'insère dans un Json puis dans le ccLocationCreate
						Location locationCreate = new Location();
						try {
							
							locationCreate.setSensorId(locationSensors);
							locationCreate.setId(Integer.parseInt(identifiant.getText()));
							locationCreate.setRoom(locationRoom);
							locationCreate.setFloor(locationFloor);
							locationCreate.setBuilding(locationBuilding);

							JSONObject locationCreateJson = new JSONObject(locationCreate);

							ConnectionClient ccLocationCreate = new ConnectionClient(host, port, "LOCATION", "CREATE", locationCreateJson.toString());
							ccLocationCreate.run();
							this.setVisible(false);
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
			// On vérifie que l'identifiant ne contient que des chiffres
			try {
				Integer.parseInt(identifiant.getText());
			} catch (java.lang.NumberFormatException ex) {
				infos.setText("L'identifiant ne peut contenir que des chiffres");
			}
			
			if (brand.getText().isEmpty() || macAddress.getText().isEmpty() || identifiant.getText().isEmpty()
					|| caracteristics.getText().isEmpty()) {
				infos.setText("Un ou plusieurs champs sont manquants");
			}
			
			// Si date = 0 : aucune localisation n'a été selectionnée
			else if (day.getSelectedIndex() <= 0 || month.getSelectedIndex() <= 0 || year.getSelectedIndex() <= 0) {
				infos.setText("Veuillez selectionner une date valide");
			}
			
			// Si index = 0 : aucune localisation n'a été selectionnée
			else if (location.getSelectedIndex() <= 0 ) {
				infos.setText("Veuillez selectionner une localisation valable");
			}
			else {
				// Début de sensor Find By Id, ici, voir lignes 384-412
				JSONObject sensorIdFindById = new JSONObject();
				sensorIdFindById.put("id", Integer.parseInt(identifiant.getText()));

				ConnectionClient ccSensorFindById = new ConnectionClient(host, port,"SENSOR",
						"FINDBYID", sensorIdFindById.toString());
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
					// Fin de Sensor Find By Id

					else {
						// début du sensorCreate
						// On créer un sensor et récupère toutes les infomations à insérer
						Sensor sensorCreate = new Sensor();
						sensorCreate.setBrand(brand.getText().trim()); 
						sensorCreate.setMacAdress(macAddress.getText().trim());
						sensorCreate.setCaracteristics(caracteristics.getText().trim());
						sensorCreate.setId(Integer.parseInt(identifiant.getText()));
						sensorCreate.setIdLocation(locationsFoundTab[location.getSelectedIndex() - 1].getId());
						sensorCreate.setAlerts(null);
						sensorCreate.setBreakdowns(null);
						sensorCreate.setState(true);

						int dayInstallation;
						int monthInstallation;
						int yearInstallation;

						dayInstallation = day.getSelectedIndex();
						monthInstallation = month.getSelectedIndex() - 1;
						// FIXME : pourquoi ? 
						yearInstallation = Integer.parseInt(years[year.getSelectedIndex()]);
						
						// on créé une Date, - 1900 car le champ 'Year' de date commence en 1900
						// donc si on selection l'année 2018, il faut insérer l'année 2018 - 1900 pour 
						// que la date soit bien formée 
						Date dateInst = new Date(yearInstallation - 1900, monthInstallation, dayInstallation);

						sensorCreate.setInstallation(dateInst);

						JSONObject sensorCreateJson = new JSONObject(sensorCreate);
						ConnectionClient ccSensorCreate = new ConnectionClient(host, port, "SENSOR", "CREATE", sensorCreateJson.toString());
						ccSensorCreate.run();
						// Fin du sensor Create
						
						// début du location Update, fait ici car en ajoutant un capteur
						// on lui attribue une localisation et il faut donc ajouter notre nouveau 
						// capteur au tableau d'identifiants de capteurs de la localisation selectionnée
						Location locationUpdate = new Location();
						locationUpdate.setBuilding(locationsFoundTab[location.getSelectedIndex() - 1].getBuilding());
						locationUpdate.setRoom(locationsFoundTab[location.getSelectedIndex() - 1].getRoom());
						locationUpdate.setId(locationsFoundTab[location.getSelectedIndex() - 1].getId());
						locationUpdate.setFloor(locationsFoundTab[location.getSelectedIndex() - 1].getFloor());

						// On récupère tous les anciens capteurs de la localisation 
						int[] locationSensorsId  = locationsFoundTab[location.getSelectedIndex() - 1].getSensorId();
						// On créer un tableau avec une rangée en plus
						int[] locationNewSensorsId = new int[locationSensorsId.length + 1];

						// On mets les anciennes valeurs dans le nouveau
						for(int i = 0; i < locationSensorsId.length; i++) {
							locationNewSensorsId[i] = locationSensorsId[i];
						}
						
						// On ajoute au nouveau tableau, l'identifiant du capteur tout juste créer
						// puis on ajoute ses nouveaux capteurs dans la localisation
						locationNewSensorsId[locationSensorsId.length] = sensorCreate.getId();
						locationUpdate.setSensorId(locationNewSensorsId);
						
						JSONObject locationUpdateJson = new JSONObject(locationUpdate);	
						try {
							ConnectionClient ccLocationUpdate = new ConnectionClient(host, port, "LOCATION", "UPDATE", locationUpdateJson.toString());
							ccLocationUpdate.run();
							// fin du locationUpdate
							this.setVisible(false);
						} catch (JSONException e1) {
							e1.printStackTrace();
						}
					}

				} catch (JSONException | IOException e1) {
					e1.printStackTrace();
				}
			}
		}
	}


	public void run() {
		initAddPatient();
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

}
