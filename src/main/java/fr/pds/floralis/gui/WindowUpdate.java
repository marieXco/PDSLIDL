package fr.pds.floralis.gui;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.sql.Date;
import java.sql.SQLException;
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

import fr.pds.floralis.commons.bean.entity.Location;
import fr.pds.floralis.commons.bean.entity.Sensor;
import fr.pds.floralis.gui.connexion.ConnectionClient;

public class WindowUpdate extends JFrame implements ActionListener {
	// watch WindowConfirm for serialVersionUID
	private static final long serialVersionUID = 1700387838741895744L;


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

	JComboBox daysComboBox = null;

	String[] daysTab = new String[32];

	JComboBox monthComboBox = null;

	String[] monthsTab = new String[13];

	JComboBox yearComboBox = null;

	String[] yearsTab = new String[12];

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

	JSONObject obj = new JSONObject();

	SimpleAttributeSet centrer = new SimpleAttributeSet();
	String host = "127.0.0.1";
	int port = 2345;
	private int id;

	List<?> locationsFoundList = new ArrayList<>();

	Location[] locationsFoundTab = null;

	JComboBox location = null;

	int sensorFoundLocationId;

	public void initUpdatePatient(int id) throws SQLException {

	}

	@SuppressWarnings("deprecation")
	public void initUpdateSensor(int id) throws JsonParseException,
	JsonMappingException, IOException {
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

		// debut de sensor Find by id, on y recupère le capteur associé à l'id qui à été envoyé en paramètre
		JSONObject sensorIdFindById = new JSONObject();
		sensorIdFindById.put("id", getId());

		ConnectionClient ccSensorFindById = new ConnectionClient(host, port,"SENSOR",
				"FINDBYID", sensorIdFindById.toString());
		ccSensorFindById.run();

		String retourSensorFindById = ccSensorFindById.getResponse();
		JSONObject sensorFoundJson = new JSONObject();
		sensorFoundJson.put("sensorFoundJson", retourSensorFindById);

		ObjectMapper objectMapper = new ObjectMapper();
		Sensor sensorFound =  objectMapper.readValue(
				sensorFoundJson.get("sensorFoundJson").toString(), Sensor.class);
		// Fin de sensor Find By Id

		// Début de location Find All, voir WindowWorker lignes 269
		ConnectionClient ccLocationFindAll = new ConnectionClient(host, port, "LOCATION", "FINDALL", null);
		ccLocationFindAll.run();

		String retourCcLocationFindAll = ccLocationFindAll.getResponse();
		JSONObject locationsFound = new JSONObject();	
		locationsFound.put("locationsFound", retourCcLocationFindAll);

		locationsFoundTab =  objectMapper.readValue(
				locationsFound.get("locationsFound").toString(), Location[].class);

		// On passe notre tableau en liste
		locationsFoundList = Arrays.asList(locationsFoundTab);

		String[] locationsComboBox = new String[locationsFoundList.size() + 1];
		locationsComboBox[0] = "--Localisation--";

		for (int listIndex = 0; listIndex < locationsFoundList.size(); listIndex++) {
			int tabIndex = listIndex + 1;
			locationsComboBox[tabIndex] = locationsFoundTab[listIndex].getBuilding().getTypeBuilding() + " - " + locationsFoundTab[listIndex].getRoom().getTypeRoom() + " - " + locationsFoundTab[listIndex].getFloor().getName();
		}

		// Fin de location Find all
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

		daysComboBox = new JComboBox(daysTab);

		monthComboBox = new JComboBox(monthsTab);

		yearComboBox = new JComboBox(yearsTab);

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

		otherInfosPanel.add(caracteristicsLabel);
		otherInfosPanel.add(caracteristics);

		// On ajoute aux champs de la fenêtre les infos du capteur trouvé
		brand.setText(sensorFound.getBrand());
		macAddress.setText(sensorFound.getMacAdress().trim());
		daysComboBox.setSelectedIndex(sensorFound.getInstallation().getDate());
		monthComboBox.setSelectedIndex(sensorFound.getInstallation().getMonth() + 1);
		yearComboBox.setSelectedIndex(sensorFound.getInstallation().getYear() - 118);
		caracteristics.setText(sensorFound.getCaracteristics());

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
			// Voir WindowAdd ligne 459
			try {
				Integer.parseInt(identifiant.getText());
			} catch (java.lang.NumberFormatException ex) {
				infos.setText("L'identifiant ne peut contenir que des chiffres");
			}
			if (brand.getText().isEmpty() || macAddress.getText().isEmpty()
					|| caracteristics.getText().isEmpty()) {
				infos.setText("Un ou plusieurs champs sont manquants");
			} 
			else {
				// Début du sensor Update
				Sensor sensorUpdate = new Sensor();
				sensorUpdate.setBrand(brand.getText().trim());
				sensorUpdate.setMacAdress(macAddress.getText().trim());
				sensorUpdate.setCaracteristics(caracteristics.getText().trim());
				sensorUpdate.setIdLocation(locationsFoundTab[location.getSelectedIndex()-1].getId());

				sensorUpdate.setId(getId());
				// Pour l'instant pas d'alertes, pas de pannes
				sensorUpdate.setAlerts(null);
				sensorUpdate.setBreakdowns(null);
				// TODO : l'état du capteur, automatiquement allumé ? 
				// bouton qui switch l'état du capteur selectionné dans la JComboBox
				sensorUpdate.setState(true);

				int dayInstallation = daysComboBox.getSelectedIndex();
				int monthInstallation = monthComboBox.getSelectedIndex() - 1;
				int yearInstallation = Integer.parseInt(yearsTab[yearComboBox.getSelectedIndex()]);

				@SuppressWarnings("deprecation")
				Date dateInstallation = new Date(yearInstallation - 1900,
						monthInstallation, dayInstallation);

				sensorUpdate.setInstallation(dateInstallation);

				JSONObject sensorUpdateJson = new JSONObject(sensorUpdate);
				ConnectionClient ccSensorUpdate = new ConnectionClient(host, port, "SENSOR", "UPDATE", sensorUpdateJson.toString());
				ccSensorUpdate.run();
				// Fin du sensorUpdate 

				// Début du old Location Update
				JSONObject objOldLocation = new JSONObject();

				objOldLocation.put("id", getSensorFoundLocationId()); 
				System.out.println("id :" + objOldLocation.toString());

				// Ici, il faut récupérer la localisation qui est associée au capteur pour 
				// supprimer dans cette localisation l'occurence du capteur supprimé
				ConnectionClient ccLocation = new ConnectionClient(host, port, "LOCATION", "FINDBYID", objOldLocation.toString());
				ccLocation.run();

				String retoursOldLocation = ccLocation.getResponse();
				JSONObject retourOldLocationJson = new JSONObject();	
				retourOldLocationJson.put("retourLocation", retoursOldLocation);

				ObjectMapper objectMapper = new ObjectMapper();
				Location oldLocation;

				try {
					oldLocation = objectMapper.readValue(
							retourOldLocationJson.get("retourLocation").toString(), Location.class);

					// Ici, on récupère tous les id des sensors de la localisation trouvée
					List <Integer> oldListSensorLocation = new ArrayList<Integer>();
					// On créer un nouveau tableau
					List <Integer> newListSensorLocation = new ArrayList<Integer>();


					// On ajoute dans ce nouveau tableau, tous les capteurs sauf celui qu'on vient de supprimer
					if(!oldListSensorLocation.contains(getId())) {
						newListSensorLocation.addAll(oldListSensorLocation);
					}
					else {
						newListSensorLocation.addAll(oldListSensorLocation);
						newListSensorLocation.add(getId());
					}

					// On modifie en mettant notre nouveau tableau puis on fait l'update sur la table des localisations
					oldLocation.setSensorId(newListSensorLocation);
					JSONObject parametersOldLocation = new JSONObject(oldLocation);	

					ConnectionClient ccLocationUpdate = new ConnectionClient(host, port, "LOCATION", "UPDATE", parametersOldLocation.toString());
					ccLocationUpdate.run();
					// Fin du old location Update

				} catch (JSONException | IOException e1) {
					e1.printStackTrace();
				}


				// Début du location Update, voir Window Add lignes 537
				Location locationUpdate = new Location();
				locationUpdate.setBuilding(locationsFoundTab[location.getSelectedIndex() - 1].getBuilding());
				locationUpdate.setRoom(locationsFoundTab[location.getSelectedIndex() - 1].getRoom());
				locationUpdate.setId(locationsFoundTab[location.getSelectedIndex() - 1].getId());
				locationUpdate.setFloor(locationsFoundTab[location.getSelectedIndex() - 1].getFloor());

				List <Integer> oldListSensorLocation = new ArrayList<Integer>();
				// On créer un nouveau tableau
				List <Integer> newListSensorLocation = new ArrayList<Integer>();


				// On ajoute dans ce nouveau tableau, tous les capteurs sauf celui qu'on vient de supprimer
				if(!oldListSensorLocation.contains(getId())) {
					newListSensorLocation.addAll(oldListSensorLocation);
				}
				else {
					newListSensorLocation.addAll(oldListSensorLocation);
					newListSensorLocation.add(getId());
				}		


				locationUpdate.setSensorId(newListSensorLocation);
				JSONObject locationUpdateJson = new JSONObject(locationUpdate);	

				ConnectionClient ccLocationUpdate = new ConnectionClient(host, port, "LOCATION", "UPDATE", locationUpdateJson.toString());
				ccLocationUpdate.run();
				// Fin du location Update

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
